package code.ecole_directe.notes;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONObject;
import org.json.JSONArray;

import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.*;
import static code.base.Emoji_For_All.Self_emoji;
import static code.color.generateRandomColor.generateRandomColor;
import static code.config.Database.UtilisateurDAO.recupererInfosUtilisateur;
import static code.ecole_directe.authentification.authenticate;
import static code.ecole_directe.notes.connections_notes.getNewNotes;

/**
 * The notes_command class is responsible for retrieving and updating the user's notes from the EcoleDirecte API.
 * It extends the ListenerAdapter class provided by the JDA library, allowing it to intercept and handle Discord slash command events.
 */
public class notes_command extends ListenerAdapter {

    /**
     * Retrieves and updates the user's notes.
     *
     * @param event The SlashCommandInteractionEvent object representing the interaction event.
     */
    public static void notes_slash(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getName().equals("setup_notes") && event.getMember().hasPermission(Permission.MANAGE_CHANNEL))  {
            String userId = event.getUser().getId();
            String guildId = event.getGuild().getId();
            String channelid = event.getChannel().getId();
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


                        JSONArray newNotes = getNewNotes(token, eleveId, userId);

                        for (int i = 0; i < newNotes.length(); i++) {
                            JSONObject note = newNotes.getJSONObject(i);
                            createAndSendNoteEmbed(event.getChannel().asTextChannel(), note);
                            DB_notes.saveProcessedNoteId(userId, guildId, channelid, note.getInt("id")); // possibly save processed note id
                        }

                        hook.editOriginal("Le salon pour les notes a été initialisé !").queueAfter(3, TimeUnit.SECONDS);
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
     * Creates and sends a note embed in the specified text channel.
     *
     * @param channel The text channel in which the note embed will be sent.
     * @param note    The JSON object containing the note information.
     */
    public static void createAndSendNoteEmbed(TextChannel channel, JSONObject note) {
        String title = note.getString("libelleMatiere");
        String exo = note.getString("devoir");
        String coeff = note.getString("coef");
        String sur = note.getString("noteSur");
        int idNote = note.getInt("id");
        String content = String.format("Moyenne de la classe: **%s/%s**\n\nNote Min: **%s/%s**, Note Max: **%s/%s**\n ",
                note.getString("moyenneClasse"),
                note.getString("noteSur"),
                note.getString("minClasse"),
                note.getString("noteSur"),
                note.getString("maxClasse"),
                note.getString("noteSur"));

        Button getNotes = Button.primary("SelfNotes_" + idNote,"Ma note").withEmoji(Self_emoji);

        Color random = generateRandomColor();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(exo)
                .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179360477298573312/exam-bad-marks.gif?ex=65798009&is=65670b09&hm=a797f2fccac77e6ec7d7df285ef34431fd07f7b6e9292229bf6986169831a3c9&")
                .addField("Matière :", title, false)
                .setDescription(content)
                .addField("coeff.", coeff, false)
                .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png")
                .setColor(random); // Choisissez une couleur appropriée

        channel.sendMessageEmbeds(embedBuilder.build()).setActionRow(getNotes).queue();
    }

}
