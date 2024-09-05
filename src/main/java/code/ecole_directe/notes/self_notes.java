package code.ecole_directe.notes;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.*;
import static code.base.path_for_all.PATH_IDENT;
import static code.base.path_for_all.r_version;
import static code.color.generateRandomColor.generateRandomColor;
import static code.ecole_directe.authentification.authenticate;
import static code.encryption.crypt.KEY_CRYPT;
import static code.encryption.crypt_and_decrypt.decryptAES;

public class self_notes extends ListenerAdapter {

    private static final String BASE_URL = "https://api.ecoledirecte.com/v3/";
    private static final OkHttpClient client = new OkHttpClient();

    public static void Self_Notes_Button(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("SelfNotes_")) {
            String noteId = event.getComponentId().substring("SelfNotes_".length());
            String userId = event.getUser().getId();

            // Acknowledge the interaction and indicate a deferred reply
            event.deferReply(true).queue(hook -> {
                try {
                    Path userFilePath = Paths.get(PATH_IDENT, userId + ".json");
                    if (!Files.exists(userFilePath)) {
                        hook.editOriginalEmbeds(Compte_Introuvable.build()).setActionRow(s_enregistrer).queue((message -> message.delete().queueAfter(10, TimeUnit.SECONDS)));
                        return;
                    }

                    String specificNoteJson = getSpecificNote(userId, noteId);
                    if (specificNoteJson.equals("Note non trouvée.")) {
                        hook.editOriginal("Note spécifique non trouvée.").queue();
                        return;
                    }

                    JSONObject specificNoteData = new JSONObject(specificNoteJson);
                    String matiere = specificNoteData.getString("libelleMatiere");
                    String exo = specificNoteData.getString("devoir");
                    String valeurNote = specificNoteData.getString("valeur");
                    String noteSur = specificNoteData.getString("noteSur");
                    String moyenneClasse = specificNoteData.getString("moyenneClasse");
                    String minClasse = specificNoteData.getString("minClasse");
                    String maxClasse = specificNoteData.getString("maxClasse");
                    String coeff = specificNoteData.getString("coef");
                    String commentaire = specificNoteData.optString("commentaire", "Pas de commentaire");
                    String date = specificNoteData.getString("date");

                    Color random = generateRandomColor();

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Détails de votre note pour **" + exo + "**")
                            .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179699919259439134/anime-found.gif")
                            .addField("Matière :", matiere, false)
                            .addField("Votre note", valeurNote + "/" + noteSur, false)
                            .addField("Moyenne de la classe", moyenneClasse + "/" + noteSur, false)
                            .addField("Note minimale", minClasse + "/" + noteSur, true)
                            .addField("Note maximale", maxClasse + "/" + noteSur, true)
                            .addField("Date :", date, false)
                            .addField("Coefficient", coeff, false)
                            .setColor(random)
                            .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png")
                            .setFooter("Personal note");
                    if (!commentaire.isEmpty()) {
                        embedBuilder.addField("Commentaire", commentaire, false);
                    }

// Mettez à jour le message initial avec les informations de la note
                    hook.editOriginalEmbeds(embedBuilder.build()).queue();


                } catch (Exception e) {
                    e.printStackTrace();
                    hook.editOriginal("Erreur lors de la récupération de la note.").queue();
                }
            }, failure -> {
                event.replyEmbeds(Error_EcoleDirecte.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)));
            });
        }
    }

    public static String getSpecificNote(String discordId, String noteId) throws Exception {
        JSONObject authResult = authenticate(discordId);
        if (authResult == null) {
            throw new IOException("Échec de l'authentification");
        }

        //System.out.print(authResult);
        JSONObject data = authResult.getJSONObject("data");
        JSONArray accounts = data.getJSONArray("accounts");

        String token = authResult.getString("token");
        JSONObject firstAccount = accounts.getJSONObject(0);
        int eleveId = firstAccount.getInt("id");
        //System.out.print(eleveId);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String payload = "data=" + URLEncoder.encode("{\"token\":\"" + token + "\"}", StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(BASE_URL + "eleves/" + eleveId + "/notes.awp?verbe=get&v=" + r_version)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Token", token)
                .post(RequestBody.create(mediaType, payload))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JSONObject responseObject = new JSONObject(responseBody);
            JSONArray notesArray = responseObject.getJSONObject("data").getJSONArray("notes");

            for (int i = 0; i < notesArray.length(); i++) {
                JSONObject noteObject = notesArray.getJSONObject(i);
                int noteObjectId = noteObject.getInt("id");
                if (String.valueOf(noteObjectId).equals(noteId)) {
                    return noteObject.toString();
                }
            }

            return "Note non trouvée.";
        }
    }
}