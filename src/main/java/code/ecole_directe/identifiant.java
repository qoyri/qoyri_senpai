package code.ecole_directe;

import code.config.Database.DB;
import code.config.Database.RegisterDAO;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.*;
import static code.encryption.crypt.KEY_CRYPT;
import static code.base.path_for_all.PATH_IDENT;
import static code.encryption.crypt_and_decrypt.encrypt;

public class identifiant extends ListenerAdapter {

    public static void slash_identifiant(SlashCommandInteractionEvent event) {
        if (event.getName().equals("s-enregistrer")) {
            event.reply("Conditions d'utilisation et de stockage des données\n\n" +
                            "En cliquant sur **Accepter**, vous consentez aux conditions suivantes :\n\n" +
                            "1. **Stockage des Données** : Vos identifiants et mots de passe seront stockés de manière sécurisée pour permettre l'utilisation des services de notre bot Discord.\n\n" +
                            "2. **Sécurité** : Nous utilisons des méthodes de cryptage avancées pour protéger vos données. Votre mot de passe est crypté et ne peut pas être récupéré dans sa forme originale sans notre moyen de décryptage.\n\n" +
                            "3. **Utilisation des Données** : Vos données ne seront utilisées que pour les fonctionnalités internes du bot et ne seront jamais partagées avec des tiers sans votre consentement explicite.\n\n" +
                            "4. **Accès et Suppression** : Vous avez le droit d'accéder à vos données ou de demander leur suppression à tout moment. (plus d'informations avec la commande /help)\n\n" +
                            "En acceptant, vous déclarez avoir lu et compris ces conditions et consentir à l'utilisation de vos données comme décrit ci-dessus.")
                    .addActionRow(
                            Button.of(ButtonStyle.SUCCESS, "register_button", "Accepter")
                    ).setEphemeral(true).queue();
        }
    }

    public static void button_identifiant(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("enregistrement_bouton")) {
            event.reply("Conditions d'utilisation et de stockage des données\n\n" +
                            "En cliquant sur **Accepter**, vous consentez aux conditions suivantes :\n\n" +
                            "1. **Stockage des Données** : Vos identifiants et mots de passe seront stockés de manière sécurisée pour permettre l'utilisation des services de notre bot Discord.\n\n" +
                            "2. **Sécurité** : Nous utilisons des méthodes de cryptage avancées pour protéger vos données. Votre mot de passe est crypté et ne peut pas être récupéré dans sa forme originale sans notre moyen de décryptage.\n\n" +
                            "3. **Utilisation des Données** : Vos données ne seront utilisées que pour les fonctionnalités internes du bot et ne seront jamais partagées avec des tiers sans votre consentement explicite.\n\n" +
                            "4. **Accès et Suppression** : Vous avez le droit d'accéder à vos données ou de demander leur suppression à tout moment. (plus d'informations avec la commande /help)\n\n" +
                            "En acceptant, vous déclarez avoir lu et compris ces conditions et consentir à l'utilisation de vos données comme décrit ci-dessus.")
                    .addActionRow(
                            Button.of(ButtonStyle.SUCCESS, "register_button", "Accepter")
                    ).setEphemeral(true).queue();
        }
    }

    public static void button_identifiant_accept(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("register_button")) {
            TextInput email = TextInput.create("username", "Nom d'utilisateur Ecole Directe", TextInputStyle.SHORT)
                    .setPlaceholder("Votre Nom d'utilisateur Ecole Directe")
                    .setMinLength(1)
                    .setMaxLength(100) // or setRequiredRange(10, 100)
                    .build();

            TextInput password = TextInput.create("password", "Mot de Passe Ecole Directe", TextInputStyle.SHORT)
                    .setPlaceholder("Votre Mot de Passe Ecole Directe")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();

            TextInput date = TextInput.create("dates", "votre date de naissance au format dd/mm/aaaa", TextInputStyle.SHORT)
                    .setPlaceholder("17/03/2005")
                    .setMinLength(10)
                    .setMaxLength(10)
                    .build();

            TextInput prenom = TextInput.create("prenom", "Votre prénom et nom", TextInputStyle.SHORT)
                    .setPlaceholder("Bob Dubois")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();

            TextInput classe = TextInput.create("classe", "Le nom de votre classe", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("copier coller du nom de votre classe depuis ecole directe directement")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();

            Modal modal = Modal.create("register_modal", "Identifiant Ecole Directe")
                    .addActionRows(ActionRow.of(email), ActionRow.of(password), ActionRow.of(date), ActionRow.of(prenom), ActionRow.of(classe))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    public static void modal_identifiant(ModalInteractionEvent event) throws SQLException {
        if (event.getModalId().equals("register_modal")) {
            String login = event.getValue("username").getAsString();
            String password = event.getValue("password").getAsString();
            String dates = event.getValue("dates").getAsString();
            String classe = event.getValue("classe").getAsString();

            String fullName = event.getValue("prenom").getAsString();
            String[] parts = fullName.split(" ", 2);
            String prenom = parts[0];
            String nom = parts.length > 1 ? parts[1] : "";


            String userId = event.getUser().getId();

            try {
                JSONObject authResult = authentification.authenticate(userId);

                if (authResult.optInt("code") == 200) {
                    // Chiffrer le mot de passe

                    String encryptedPassword = encrypt(password, KEY_CRYPT);

                    // Enregistrement dans la base de données
                    RegisterDAO.sauvegarderUtilisateur(userId, login, encryptedPassword, dates, prenom, nom, classe);

                    event.replyEmbeds(Compte_Cree.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(15, TimeUnit.SECONDS)));
                } else {
                    // Gérer l'échec de l'authentification
                    event.replyEmbeds(Compte_Incorrect.build()).addActionRow(reessayer).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(15, TimeUnit.SECONDS)));
                }
            } catch (Exception e) { // Attrape à la fois SQLException et IOException
                e.printStackTrace();
                event.replyEmbeds(Error_Internal.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)));
            }
        }
    }
}
