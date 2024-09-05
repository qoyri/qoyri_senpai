package code.ecole_directe.emploi_du_temps;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.*;
import static code.base.Emoji_For_All.Refresh_emoji;
import static code.color.generateRandomColor.generateRandomColor;
import static code.config.Database.UtilisateurDAO.recupererInfosUtilisateur;
import static code.ecole_directe.authentification.authenticate;
import static code.ecole_directe.emploi_du_temps.DB_emploi.saveMessageAndUserIdToDatabase;
import static code.ecole_directe.emploi_du_temps.Emploi_du_temps.*;

public class Setup_EMP_TMP extends ListenerAdapter {

    public static void Setup_Emploi_Temps(SlashCommandInteractionEvent event) throws SQLException {
        String userId = event.getUser().getId();
        String guildId = event.getGuild().getId();
        String channelId = event.getChannel().getId();
        Map<String, String> userCredentials = recupererInfosUtilisateur(userId);

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
        DateTimeFormatter formatEmbed = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (event.getName().equals("setup_emploi_du_temps") && event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            if (userCredentials.isEmpty()) {
                event.replyEmbeds(Compte_Introuvable.build()).setActionRow(s_enregistrer).queue((message -> {
                    message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                }));
            } else {
                try {
                    event.deferReply(true).queue(hook -> {

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

                        String dateDebutEmbed = formatEmbed.format(startDate);
                        String dateFinEmbed = formatEmbed.format(endDate);

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

                        Color randomColor = generateRandomColor();

                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setTitle("Emploi du temps du " + dateDebutEmbed + " au " + dateFinEmbed)
                                .setColor(randomColor);

                        net.dv8tion.jda.api.interactions.components.buttons.Button RefreshButton = Button.primary("refresh_EMP_TMP", " ").withEmoji(Refresh_emoji);

                        File imageFile = new File(filePath);
                        event.getChannel()
                                .sendFiles(FileUpload.fromData(imageFile))
                                .addEmbeds(embedBuilder.build())
                                .setActionRow(RefreshButton)
                                .queue(message_embed -> {
                                    saveMessageAndUserIdToDatabase(guildId, userId, channelId, message_embed.getId());
                                });
                        hook.editOriginal("Emploi du temps setup !").queueAfter(3, TimeUnit.SECONDS);
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
        }
    }
}
