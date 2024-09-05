package code.ecole_directe.emploi_du_temps;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.*;
import static code.base.Embed_For_All.s_enregistrer;
import static code.base.Emoji_For_All.Refresh_emoji;
import static code.base.path_for_all.PATH;
import static code.base.path_for_all.PATH_IDENT;
import static code.color.generateRandomColor.generateRandomColor;
import static code.ecole_directe.authentification.authenticate;
import static code.ecole_directe.emploi_du_temps.DB_emploi.getMessageAndUserIdFromDatabase;
import static code.ecole_directe.emploi_du_temps.Emploi_du_temps.getEmploiDuTemps;
import static code.encryption.crypt.KEY_CRYPT;
import static code.encryption.crypt_and_decrypt.decryptAES;

public class Update_EMP_TMP {
    public static void refresh_EMP_TMP(ButtonInteractionEvent event) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        LocalDate startDate;

        if (today.getDayOfWeek().equals(DayOfWeek.SATURDAY) && now.getHour() >= 18) {
            startDate = nextMonday.plusWeeks(1);
        } else if (today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            startDate = nextMonday.plusWeeks(1);
        } else {
            startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        LocalDate endDate = startDate.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            event.deferReply(true).queue(hook -> {
            Map<String, String> mapping = getMessageAndUserIdFromDatabase(event.getGuild().getId(), event.getChannel().getId());
            String userId = mapping.get("userId");
            String messageId = mapping.get("messageId");

            // Authentification et récupération de l'emploi du temps
                JSONObject authResult = null;
                try {
                    authResult = authenticate(userId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (authResult != null) {
                String token = authResult.getString("token");
                JSONObject data = authResult.getJSONObject("data");
                JSONArray accounts = data.getJSONArray("accounts");

                JSONObject firstAccount = accounts.getJSONObject(0);
                int eleveId = firstAccount.getInt("id");

                String dateDebut = formatter.format(startDate);
                String dateFin = formatter.format(endDate);

                // Mise à jour de l'emploi du temps
                String emploiDuTempsJson = null;
                try {
                    emploiDuTempsJson = getEmploiDuTemps(token, dateDebut, dateFin, eleveId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JSONArray emploiDuTempsData = new JSONObject(emploiDuTempsJson).getJSONArray("data");

                String filePath = "images/emploi_du_temps" + eleveId + ".png";
                try {
                    TimetableImageCreator.createTimetableImage(emploiDuTempsData, filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Mise à jour de l'embed
                updateMessage(event.getChannel(), messageId, filePath);
                hook.editOriginal("Emploi du temps update !").queueAfter(3, TimeUnit.SECONDS);
            } else {
                event.replyEmbeds(Error_EcoleDirecte.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)));
            }
            }, failure -> {
                event.replyEmbeds(Error_EcoleDirecte.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)));
            });
        } catch (Exception e) {
            e.printStackTrace();
            event.replyEmbeds(Error_Internal.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)));
        }
    }

    private static void updateMessage(MessageChannel channel, String messageId, String filePath) {
        File imageFile = new File(filePath);
        if (imageFile.exists()) {

            net.dv8tion.jda.api.interactions.components.buttons.Button RefreshButton = Button.primary("refresh_EMP_TMP", " ").withEmoji(Refresh_emoji);

            Color randomColor = generateRandomColor();

            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();
            LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
            LocalDate startDate;

            if (today.getDayOfWeek().equals(DayOfWeek.SATURDAY) && now.getHour() >= 18) {
                startDate = nextMonday.plusWeeks(1);
            } else if (today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                startDate = nextMonday.plusWeeks(1);
            } else {
                startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            }
            LocalDate endDate = startDate.plusDays(6);

            DateTimeFormatter formatEmbed = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String dateDebutEmbed = formatEmbed.format(startDate);
            String dateFinEmbed = formatEmbed.format(endDate);

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Emploi du temps du " + dateDebutEmbed + " au " + dateFinEmbed)
                    .setColor(randomColor);

            channel.retrieveMessageById(messageId).queue(message -> {
                message.editMessage("")
                        .setEmbeds(embedBuilder.build())
                        .setFiles(FileUpload.fromData(imageFile))
                        .setActionRow(RefreshButton)
                        .queue();
            }, failure -> {
                channel.sendMessage("Impossible de trouver le message à mettre à jour.").queue();
            });
        } else {
            channel.sendMessage("Impossible de créer l'image de l'emploi du temps.").queue();
        }
    }
}
