package code;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.List;

import static code.base.basic.Help.*;
import static code.base.basic.Informations.info_slash;
import static code.base.ticket.ticketperso.*;
import static code.ecole_directe.DeleteCommand.delete_command;
import static code.ecole_directe.devoir.devoir_command.devoirs_slash;
import static code.ecole_directe.devoir.self_devoir.*;
import static code.ecole_directe.emploi_du_temps.Setup_EMP_TMP.Setup_Emploi_Temps;
import static code.ecole_directe.emploi_du_temps.Update_EMP_TMP.refresh_EMP_TMP;
import static code.ecole_directe.identifiant.*;
import static code.ecole_directe.notes.notes_command.notes_slash;
import static code.ecole_directe.notes.self_notes.Self_Notes_Button;

/**
 * This class handles various types of interactions and events,
 * such as slash command interactions, button interactions,
 * modal interactions, and string select interactions.
 * It extends the ListenerAdapter class to override and handle
 * the necessary methods.
 */
public class CommandHandler extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println("\u001B[35m[SLASH]\u001B[0m Commande slash reçue : " + event.getName() + " par " + event.getUser().getAsTag());
        String commandName = event.getName();

        if (commandName.equals("s-enregistrer")) {
            slash_identifiant(event);
        }
        if (commandName.equals("setup_emploi_du_temps")) {
            try {
                Setup_Emploi_Temps(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (commandName.equals("setup_notes")) {
            try {
                notes_slash(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (commandName.equals("setup_devoirs")) {
            try {
                devoirs_slash(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (commandName.equals("info")) {
            info_slash(event);
        }
        if (commandName.equals("help")) {
            Help_Slash(event);
        }
        if (commandName.equals("delete")) {
            delete_command(event);
        }
        if (commandName.equals("ticket")) {
            ticketp(event);
        }
        if (commandName.equals("devoir")) {
            devoirperso(event);
        }
        if (commandName.equals("info")) {
            info_slash(event);
        }
        if (commandName.equals("help")) {
            Help_Slash(event);
        }
        if (commandName.equals("delete")) {
            delete_command(event);
        }
        if (commandName.equals("ticket")) {
            ticketp(event);
        }
        if (commandName.equals("devoir")) {
            devoirperso(event);
        }
    }
    public void onButtonInteraction(ButtonInteractionEvent event) {
        System.out.println("\u001B[36m[BUTTON]\u001B[0m Bouton cliqué : " + event.getComponentId() + " par " + event.getUser().getAsTag());
        if (event.getComponentId().equals("register_button")) {
            button_identifiant_accept(event);
        }
        if (event.getComponentId().equals("enregistrement_bouton")) {
            button_identifiant(event);
        }
        if (event.getComponentId().equals("refresh_EMP_TMP")) {
            refresh_EMP_TMP(event);
        }
        if (event.getComponentId().startsWith("SelfNotes_")) {
            Self_Notes_Button(event);
        }
        if (event.getComponentId().startsWith("commandes_bouton")) {
            commandes_bouton(event);
        }
        if (event.getComponentId().startsWith("tos_bouton")) {
            tos_bouton(event);
        }
        if (event.getComponentId().startsWith("problem_bouton")) {
            problem_bouton(event);
        }
        if (event.getComponentId().startsWith("Respond_")) {
            ticketR(event);
        }
        if (event.getComponentId().startsWith("supportb_")) {
            ticketb(event);
        }
    }
    public void onModalInteraction(ModalInteractionEvent event) {
        System.out.println("\u001B[32m[MODAL]\u001B[0m Modal interaction : " + event.getModalId() + " par " + event.getUser().getAsTag());
        if (event.getModalId().equals("register_modal")) {
            try {
                modal_identifiant(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (event.getModalId().equals("support")) {
            support(event);
        }
        if (event.getModalId().equals("RespondSend")) {
            supportR(event);
        }
        if (event.getModalId().startsWith("supportmp_")) {
            supportmp(event);
        }
        if (event.getModalId().startsWith("devoir_modal")) {
            set_devoir(event);
        }
        if (event.getModalId().equals("support")) {
            support(event);
        }
        if (event.getModalId().equals("RespondSend")) {
            supportR(event);
        }
        if (event.getModalId().startsWith("supportmp_")) {
            supportmp(event);
        }
        if (event.getModalId().startsWith("devoir_modal")) {
            set_devoir(event);
        }
    }
    public void onStringSelectInteraction(StringSelectInteractionEvent event){
        List<String> selectedValues = event.getInteraction().getValues();
        System.out.print("\u001B[33m[LIST]\u001B[0m Liste sélectionnée : ");
        for (String value : selectedValues) {
            System.out.print(value + " ");
        }
        System.out.println("par " + event.getUser().getName());

        //if (selectedValues.contains("SLAM")) {
        //    siorole(event);
        //}
        //if (event.getInteraction().equals("specific")){
            //specific(event);
        //}
    }
}
