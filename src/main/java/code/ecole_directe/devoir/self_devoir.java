package code.ecole_directe.devoir;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.*;
import static code.base.path_for_all.PATH_IDENT;
import static code.base.path_for_all.r_version;
import static code.color.generateRandomColor.generateRandomColor;
import static code.ecole_directe.authentification.authenticate;
import static code.encryption.crypt.KEY_CRYPT;
import static code.encryption.crypt_and_decrypt.decryptAES;

public class self_devoir extends ListenerAdapter {

    public static void devoirperso(SlashCommandInteractionEvent event) {
        if (event.getName().equals("devoir")) {
            TextInput eval = TextInput.create("eval", "Est-ce une évaluation ?", TextInputStyle.SHORT)
                    .setPlaceholder("oui/non")
                    .setMinLength(3)
                    .setMaxLength(3) // or setRequiredRange(10, 100)
                    .build();

            TextInput matiere = TextInput.create("matiere", "Matière concernéé", TextInputStyle.SHORT)
                    .setPlaceholder("Fancais, Histoire...")
                    .setMinLength(2)
                    .setMaxLength(100)
                    .build();

            TextInput date = TextInput.create("date", "Date", TextInputStyle.SHORT)
                    .setPlaceholder("jj/mm/aaaa (ex: 23/02/2024")
                    .setMinLength(10)
                    .setMaxLength(10)
                    .build();

            TextInput detail = TextInput.create("detail", "Détail du devoir à faire", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("detail...")
                    .setMinLength(5)
                    .setMaxLength(1000)
                    .build();

            Modal modal = Modal.create("devoir_modal", "Ajouter un devoir")
                    .addActionRows(ActionRow.of(eval), ActionRow.of(matiere), ActionRow.of(date), ActionRow.of(detail))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    public static void set_devoir(ModalInteractionEvent event) {
        if (event.getModalId().equals("devoir_modal")) {
            List<ModalMapping> values = event.getValues();

            Boolean isEvaluation = false;

            String eval = event.getValue("eval").getAsString();
            String datemodal = event.getValue("date").getAsString();
            String title = event.getValue("matiere").getAsString();
            String detail = event.getValue("detail").getAsString();

            isEvaluation = eval.equals("oui");

            String type = isEvaluation ? "Évaluation" : "Devoir";

            // Convertir la date au format dd/mm/yyyy et obtenir le timestamp Unix
            String dateCompteur = event.getValue("date").getAsString();
            long timestamp = 0;
            try {
                SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = originalFormat.parse(dateCompteur);
                timestamp = date.getTime() / 1000; // Convertir en secondes
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            // Format de Discord pour un décompte: <t:timestamp:f>
            String countdown = "<t:" + timestamp + ":R>"; // 'R' pour le format relatif (décompte)

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(type + " pour le " + datemodal)
                    .addField("Ajouter par :", event.getUser().getName(), false)
                    .setAuthor(event.getUser().getName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl())
                    .addField("Date de remise :", countdown, false) // Ajouter le décompte// Ajoutez la date formatée au titre
                    .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179699938330939413/tumblr_12280ee60d256f9971cf9ff78143e59a_de353a20_500.gif")
                    .addField("Matière :", title, false)
                    .addField("À faire :", detail, false)
                    .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png")
                    .setColor(random); // Choisissez une couleur appropriée

            event.reply("Devoir ajouté !").setEphemeral(true).queue(response -> {
                // Envoyer l'embed dans le canal après avoir répondu à l'événement du modal
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue(message -> {
                    // Créer un fil attaché à ce message
                    message.createThreadChannel("Discussion du devoir").queue();
                });
            });
        }
    }
}