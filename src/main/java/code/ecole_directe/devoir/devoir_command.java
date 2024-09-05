package code.ecole_directe.devoir;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;

import static code.base.Embed_For_All.*;
import static code.color.generateRandomColor.generateRandomColor;
import static code.config.Database.UtilisateurDAO.recupererInfosUtilisateur;
import static code.ecole_directe.authentification.authenticate;
import static code.ecole_directe.devoir.connections_devoir.getNewDevoirs;


/**
 * The devoirs_command class is responsible for retrieving and updating the user's devoirs from the EcoleDirecte API.
 * It extends the ListenerAdapter class provided by the JDA library, allowing it to intercept and handle Discord slash command events.
 */
public class devoir_command extends ListenerAdapter {

    /**
     * Retrieves and updates the user's devoirs.
     *
     * @param event The SlashCommandInteractionEvent object representing the interaction event.
     */
    public static void devoirs_slash(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getName().equals("setup_devoirs") && event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            String userId = event.getUser().getId();
            String guildId = event.getGuild().getId();
            String channelId = event.getChannel().getId();
            Map<String, String> userCredentials = recupererInfosUtilisateur(userId);

            if (!userCredentials.isEmpty()) {
                event.deferReply(true).queue(hook -> {
                    try {

                        JSONObject authResult = authenticate(userId);
                        String token = authResult.getString("token");
                        JSONObject data = authResult.getJSONObject("data");
                        JSONArray accounts = data.getJSONArray("accounts");

                        JSONObject firstAccount = accounts.getJSONObject(0);
                        int eleveId = firstAccount.getInt("id");


                        JSONArray newDevoirs = getNewDevoirs(token, eleveId, userId);

                        for (int i = 0; i < newDevoirs.length(); i++) {
                            JSONObject devoirs = newDevoirs.getJSONObject(i);
                            createAndSendDevoirsEmbed(event.getChannel().asTextChannel(), devoirs);
                            DB_devoir.saveProcessedDevoirId(userId, guildId, channelId, devoirs.getInt("id")); // possibly save processed note id
                        }
                        hook.editOriginal("Le salon pour les devoirs a été initialisé !").queueAfter(3, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        hook.editOriginalEmbeds(Error_Internal.build()).queueAfter(3, TimeUnit.SECONDS);
                    }
                }, failure -> {
                    event.replyEmbeds(Error_EcoleDirecte.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)));
                });
            } else {
                    event.replyEmbeds(Compte_Introuvable.build()).setActionRow(s_enregistrer).queue((message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)));
            }
        }
        else {
            event.replyEmbeds(REQUIRED_PERMISSIONS.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(30, TimeUnit.SECONDS)));
        }
    }

    /**
     * Creates and sends a devoirs embed in the specified text channel.
     *
     * @param channel The text channel in which the devoirs embed will be sent.
     * @param devoirs    The JSON object containing the devoir information.
     */
    public static void createAndSendDevoirsEmbed(TextChannel channel, JSONObject devoirs) {
        String title = devoirs.getString("matiere");
        boolean isEvaluation = devoirs.getBoolean("interrogation");
        String type = isEvaluation ? "Évaluation" : "Devoir";
        String dateOriginal = devoirs.getString("dateDevoir");
        String dateFormatted = "";

        // Convertir la date au format dd/mm/yyyy
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = originalFormat.parse(dateOriginal);
            dateFormatted = targetFormat.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            dateFormatted = dateOriginal; // Utilisez la date originale en cas d'échec du formatage
        }

        int idNote = devoirs.getInt("id");

        String encodedContent = devoirs.getJSONObject("aFaire").getString("contenu");
        byte[] decodedBytes = Base64.getDecoder().decode(encodedContent);
        String decodedContent = new String(decodedBytes);

        // Convertir la date au format dd/mm/yyyy et obtenir le timestamp Unix
        String dateCompteur = devoirs.getString("dateDevoir");
        long timestamp = 0;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = originalFormat.parse(dateCompteur);
            timestamp = date.getTime() / 1000; // Convertir en secondes
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        // Format de Discord pour un décompte: <t:timestamp:f>
        String countdown = "<t:" + timestamp + ":R>"; // 'R' pour le format relatif (décompte)


        String decodedHtml = Jsoup.parse(decodedContent).text();

        Color random = generateRandomColor();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(type + " pour le " + dateFormatted)
                .addField("Date de remise :", countdown, false) // Ajouter le décompte// Ajoutez la date formatée au titre
                .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179699938330939413/tumblr_12280ee60d256f9971cf9ff78143e59a_de353a20_500.gif")
                .addField("Matière :", title, false)
                .addField("À faire :", decodedHtml, false)
                .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png")
                .setColor(random); // Choisissez une couleur appropriée

        channel.sendMessageEmbeds(embedBuilder.build()).queue(message -> {
            // Créer un fil attaché à ce message
            message.createThreadChannel("Discussion du devoir") // Nom et durée de vie du fil
                    .queue();
        });
    }
}
