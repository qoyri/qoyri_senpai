package code.ecole_directe;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.io.File;
import java.awt.Color;
import java.util.concurrent.TimeUnit;

import static code.base.Embed_For_All.REQUIRED_PERMISSIONS;
import static code.base.path_for_all.*;

public class DeleteCommand extends ListenerAdapter {

    public static void delete_command(SlashCommandInteractionEvent event) {
        if (event.getName().equals("delete")) {
            String userId = event.getUser().getId();
            String guildId = event.getGuild().getId();
            String option = event.getOption("option").getAsString();

            switch (option) {
                case "account":
                    sendResponse(event, deleteAccount(userId), "Compte");
                    break;
                case "emploi_du_temps":
                    if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                        sendResponse(event, deleteFile(guildId, "emploi_du_temps_user_mapping.json"), "Emploi du temps");
                    }
                    else {
                        event.replyEmbeds(REQUIRED_PERMISSIONS.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(30, TimeUnit.SECONDS)));
                    }
                    break;
                case "devoirs":
                    if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                        sendResponse(event, deleteFile(guildId, "devoirs_info.json"), "Devoirs");
                    }
                    else {
                        event.replyEmbeds(REQUIRED_PERMISSIONS.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(30, TimeUnit.SECONDS)));
                    }
                    break;
                case "notes":
                    if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                        sendResponse(event, deleteFile(guildId, "notes_info.json"), "Notes");
                    }
                    else {
                        event.replyEmbeds(REQUIRED_PERMISSIONS.build()).setEphemeral(true).queue((message -> message.deleteOriginal().queueAfter(30, TimeUnit.SECONDS)));
                    }
                    break;
            }
        }
    }

    private static boolean deleteAccount(String userId) {
        File file = new File(PATH_IDENT + userId + ".json");
        return file.delete();
    }

    private static boolean deleteFile(String guildId, String fileName) {
        File file = new File(PATH + guildId + "\\" + fileName);
        return file.delete();
    }

    private static void sendResponse(SlashCommandInteractionEvent event, boolean success, String fileType) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(success ? Color.GREEN : Color.RED);
        embed.setTitle("Suppression du fichier");
        embed.setDescription(success ?
                "Le fichier " + fileType + " a été supprimé avec succès." :
                "Erreur lors de la suppression du fichier " + fileType + ".\n" +
                "si le problème persiste faites un ticket avec la commande /ticket");
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
