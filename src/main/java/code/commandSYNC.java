package code;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class commandSYNC {
    public static void synchronizeCommands(JDA jda) {
        SlashCommandData setup_emploi = Commands.slash("setup_emploi_du_temps", "setup dans ce channel l'emploi du temps de la classe");
        SlashCommandData register = Commands.slash("s-enregistrer", "enregistrer ou modifier ses informations de connexion Ecole Directe");
        SlashCommandData notes_slash = Commands.slash("setup_notes", "setup dans ce channel pour les notes");
        SlashCommandData devoirs_slash = Commands.slash("setup_devoirs", "setup dans ce channel pour les devoirs");
        SlashCommandData info_slash = Commands.slash("info", "Connaître les informations spécifique de sorai");
        SlashCommandData help_slash = Commands.slash("help", "Centre d'aide");
        SlashCommandData ticket = Commands.slash("ticket", "Rapporter un problème ou un bug");
        SlashCommandData devoir = Commands.slash("devoir", "Ajouter un devoir");


        OptionData option = new OptionData(OptionType.STRING, "option", "Type de fichier à supprimer", true)
                .addChoice("account", "account")
                .addChoice("emploi_du_temps", "emploi_du_temps")
                .addChoice("devoirs", "devoirs")
                .addChoice("notes", "notes");

        CommandData delete = Commands.slash("delete", "Supprimer un fichier").addOptions(option);

        jda.upsertCommand(setup_emploi).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande setup_emploi_du_temps synchronisée !");
        jda.upsertCommand(register).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande s-enregistrer synchronisée !");
        jda.upsertCommand(notes_slash).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande setup_notes synchronisée !");
        jda.upsertCommand(devoirs_slash).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande setup_devoirs synchronisée !");
        jda.upsertCommand(info_slash).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande info synchronisée !");
        jda.upsertCommand(help_slash).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande help synchronisée !");
        jda.upsertCommand(delete).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande delete synchronisée !");
        jda.upsertCommand(ticket).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande ticket synchronisée !");
        jda.upsertCommand(devoir).queue();
        System.out.println("\u001B[36m[COMMANDE]\u001B[0m - commande devoir synchronisée !");
    }
}
