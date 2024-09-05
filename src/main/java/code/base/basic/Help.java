package code.base.basic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.*;
import static code.color.generateRandomColor.generateRandomColor;


public class Help {
    public static void Help_Slash(SlashCommandInteractionEvent event) {
        Color randomColor = generateRandomColor();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("> Centre d'aides")
                .setDescription(" \n ** ** \n" +
                                "### <a:command:1181598245626069022>   ** |   COMMANDES**\n" +
                                "### <:TOS:1181599625036173334>   ** |   CONDITIONS D'UTILISATION**\n" +
                                "### <a:problem:1181600797805527071>   ** |   REPORT PROBLEM/BUG**" +
                                " \n ** ** \n")
                .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1181600178403291177/anime-guitar.gif")
                .setColor(randomColor)
                .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png");

        event.replyEmbeds(embedBuilder.build()).setActionRow(COMMANDES, TOS, PROBLEM).queue();
    }

    public static void commandes_bouton(ButtonInteractionEvent event) {
        Color randomColor = generateRandomColor();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Liste des commandes :")
                .addField("/s-enregister", "```Sert à enregister vos identifiants ÉcoleDirecte et à interagir avec le bot```", false)
                .addField("/setup_emploi_du_temps", "```Sert à définir le salon dans lequel l'emploi du temps sera afficher```", false)
                .addField("/setup_notes", "```Sert à définir le salon dans lequel les nouvelles notes apparaitront```", false)
                .addField("/setup_devoirs", "```Sert à définir le salon dans lequel les nouveaux devoirs apparaitront```", false)
                .addField("/ticket", "```Sert à faire un ticket pour rapporter un problème ou un bug lié au bot```", false)
                .addField("/info", "```Connaître les informations spécifiques de sorai```", false)
                .addField("/delete", "```Supprimer un type de fichier (nos propres identifiants EcoleDirecte ou fichier de synchronisation pour les devoirs, notes, emploi du temps)```", false)
                .setColor(randomColor)
                .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png");

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(45, TimeUnit.SECONDS)));
    }

    public static void tos_bouton(ButtonInteractionEvent event) {
        Color randomColor = generateRandomColor();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Condition d'Utilisation :")
                .setDescription(" ** ** \n " +
                        "Conditions d'utilisation et de stockage des données\n\n" +
                        "Voici les Conditions d'Utilisation du bot <@1150120525004283974> :\n\n" +
                        "1. **Stockage des Données** : Vos identifiants et mots de passe seront stockés de manière sécurisée pour permettre l'utilisation des services de notre bot Discord.\n\n" +
                        "2. **Sécurité** : Nous utilisons des méthodes de cryptage avancées pour protéger vos données. Votre mot de passe est crypté et ne peut pas être récupéré dans sa forme originale sans notre moyen de décryptage.\n\n" +
                        "3. **Utilisation des Données** : Vos données ne seront utilisées que pour les fonctionnalités internes du bot et ne seront jamais partagées avec des tiers sans votre consentement explicite.\n\n" +
                        "4. **Accès et Suppression** : Vous avez le droit d'accéder à vos données ou de demander leur suppression à tout moment. (plus d'informations avec la commande /help)\n\n" +
                        "En acceptant, vous déclarez avoir lu et compris ces conditions et consentir à l'utilisation de vos données comme décrit ci-dessus.")
                .setColor(randomColor)
                .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179901938003873824/waiting-excited.gif")
                .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png");

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(45, TimeUnit.SECONDS)));
    }

    public static void problem_bouton(ButtonInteractionEvent event) {
        Color randomColor = generateRandomColor();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("En cas de problème avec le bot")
                .setDescription(" ** ** \n Si jamais vous rencontrez un problème avec le bot, vous pouvez utiliser la commande **/ticket** et remplir le formulaire avec si possible, le code d'erreur que vous avez eu pour pouvoir régler le soucis le plus rapidement !")
                .setColor(randomColor)
                .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179901106759929996/da1ds21-eea7107d-cab0-4d94-a0e7-af9da0f5bad4.gif")
                .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png");

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(45, TimeUnit.SECONDS)));
    }
}
