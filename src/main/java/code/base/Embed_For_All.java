package code.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

import static code.base.Emoji_For_All.*;
import static code.color.generateRandomColor.generateRandomColor;


public class Embed_For_All {
    public static Color random = generateRandomColor();
    public static Button s_enregistrer = Button.primary("enregistrement_bouton","m'enregistrer");
    public static Button reessayer = Button.primary("enregistrement_bouton","réessayer");
    public static Button COMMANDES = Button.secondary("commandes_bouton"," ").withEmoji(commandes_emoji);
    public static Button TOS = Button.secondary("tos_bouton"," ").withEmoji(tos_emoji);
    public static Button PROBLEM = Button.secondary("problem_bouton"," ").withEmoji(problem_emoji);




    public static EmbedBuilder Compte_Introuvable = new EmbedBuilder()
            .setTitle("Comptes Introuvable")
            .setDescription("Aucun identifiant n'a été trouvé. \nIl s'emblerait donc que vous n'avez pas enregistré vos identifiants Ecole Directe.\n\nVoulez-vous vous enregistrer ?")
            .setColor(random)
            .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179722060491137046/yui-anime.gif")
            .setFooter("Code 506");

    public static EmbedBuilder Compte_Cree = new EmbedBuilder()
            .setTitle("Inscription réussie !")
            .setDescription("\uD83C\uDF89 Félicitations ! Vous êtes désormais inscrit(e) et prêt(e) à utiliser toutes les fonctionnalités de soraï.\n\n" +
                    "\uD83D\uDD12 Vos informations sont sécurisées et chiffrées pour garantir la confidentialité de vos données.\n\n" +
                    "✨ Profitez dès maintenant des services offerts par soraï pour une expérience personnalisée et pratique.\n\n" +
                    "N'hésitez pas à utiliser la commande `/help` pour explorer les différentes fonctionnalités disponibles.\n")
            .setColor(random)
            .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179901938003873824/waiting-excited.gif")
            .setFooter("Code 200");

    public static EmbedBuilder Compte_Incorrect = new EmbedBuilder()
            .setTitle("Erreur lors de l'inscription.")
            .setDescription("❌ Nous n'avons pas pu vérifier vos identifiants. Assurez-vous que votre nom d'utilisateur et votre mot de passe sont corrects.\n\n" +
                    "\uD83D\uDD0D Vérifiez que vous n'avez pas fait de faute de frappe ou utilisé des espaces inutiles.\n\n" +
                    "\uD83D\uDD11 Si vous avez oublié vos identifiants, veuillez les récupérer sur le site d'EcoleDirecte ou contacter votre établissement scolaire.\n\n" +
                    "Pour toute autre assistance, veuillez faire un ticket avec la commande /ticket.\n")
            .setColor(random)
            .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179901106759929996/da1ds21-eea7107d-cab0-4d94-a0e7-af9da0f5bad4.gif")
            .setFooter("Code 505");

    public static EmbedBuilder Error_EcoleDirecte = new EmbedBuilder()
            .setTitle("Erreur avec Ecole Directe")
            .setDescription("Nous avons rencontré un problème lors de la connexion à l'API d'ÉcoleDirecte. Cette situation peut être due à plusieurs facteurs, \n" +
                    "et nous nous efforçons de résoudre le problème le plus rapidement possible.\n" +
                    "Si le problème persiste trop longtemps n'hésitez pas à faire un ticket avec la commande /ticket\n")
            .setColor(Color.red)
            .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179901511090843678/kon-anime.gif")
            .setFooter("Code 500");

    public static EmbedBuilder Error_Internal = new EmbedBuilder()
            .setTitle("Erreur interne")
            .setDescription("Nous avons rencontré un problème interne. Cette situation peut être due à plusieurs facteurs, \n" +
                    "et nous nous efforçons de résoudre le problème le plus rapidement possible.\n\n" +
                    "Si le problème persiste trop longtemps n'hésitez pas à faire un ticket avec la commande /ticket\n")
            .setColor(Color.red)
            .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179901511090843678/kon-anime.gif")
            .setFooter("Code 509");

    public static EmbedBuilder REQUIRED_PERMISSIONS = new EmbedBuilder()
            .setTitle("ACTION NON AUTORISÉE")
            .setDescription("Vous ne possèdez pas les permissions requisent pour l'utilisation de cette commande \n" +
                    "Veuillez réessayez lorsque vous aurez les permissions ou avec un membres agréé pour cela.\n\n" +
                    "Si le problème persiste trop longtemps et que vous possèdez les permissions n'hésitez pas à faire un ticket avec la commande /ticket\n")
            .setColor(Color.red)
            .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1179901511090843678/kon-anime.gif")
            .setFooter("Code 509");
}
