package code.base.ticket;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.*;
import java.util.List;

public class ticketperso extends ListenerAdapter {
    public static void ticketp(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ticket")) {
            TextInput email = TextInput.create("code", "code d'erreur", TextInputStyle.SHORT)
                    .setPlaceholder("code d'erreur")
                    .setMinLength(0)
                    .setMaxLength(5) // or setRequiredRange(10, 100)
                    .build();

            TextInput body = TextInput.create("soucis", "Description du soucis", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("description...")
                    .setMinLength(25)
                    .setMaxLength(1000)
                    .build();

            Modal modal = Modal.create("support", "Support 🛠️")
                    .addActionRows(ActionRow.of(email), ActionRow.of(body))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    public static void support(ModalInteractionEvent event) {
        User qoyri = event.getJDA().getUserById("776016158737432626");
        if (event.getModalId().equals("support")) {
            List<ModalMapping> values = event.getValues();

            String code = event.getValue("code").getAsString();
            String body = event.getValue("soucis").getAsString();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Support 🧑‍💻");
            embed.setColor(Color.blue);
            embed.addField("code", code + " ID : " + event.getMember().getId(), false);
            embed.addField("soucis", body, false);

            qoyri.openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(embed.build()).queue(message -> {
                    Button supportButton = Button.primary("Respond_" + event.getMember().getId(), "Répondre");
                    message.editMessageEmbeds(embed.build()).setActionRow(supportButton).queue();
                });
            });

            event.reply("Merci ! 💖").setEphemeral(true).queue();
        }
    }

    public static void ticketR(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("Respond_")) {
            String userid = event.getComponentId().substring("Respond_".length());
            TextInput ID = TextInput.create("id", "ID de la personne", TextInputStyle.SHORT)
                    .setValue(userid)
                    .setRequired(true)
                    .build();

            TextInput Pseudo = TextInput.create("pseudo", "pseudo", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("le pseudo")
                    .setMinLength(3)
                    .setMaxLength(100)
                    .build();

            TextInput Resp = TextInput.create("resp", "réponse", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("la réponse")
                    .setMinLength(25)
                    .setMaxLength(1000)
                    .build();

            Modal modal = Modal.create("RespondSend", "Réponse 🛠️")
                    .addActionRows(ActionRow.of(ID),ActionRow.of(Pseudo), ActionRow.of(Resp))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    public static void supportR(ModalInteractionEvent event) {
        String UserID = event.getValue("id").getAsString();
        User UserR = event.getJDA().getUserById(UserID);
        if (event.getModalId().equals("RespondSend")) {

            String pseudo = event.getValue("pseudo").getAsString();
            String resp = event.getValue("resp").getAsString();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Support Respond 🧑‍💻");
            embed.setColor(Color.blue);
            embed.addField("Pseudo", pseudo, false);
            embed.addField("Objet", resp, false);

            UserR.openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(embed.build()).queue(message -> {
                    Button supportButton = Button.primary("supportb_" + UserID, "Respond");
                    message.editMessageEmbeds(embed.build()).setActionRow(supportButton).queue();
                });
            });

            event.reply("Merci ! 💖").setEphemeral(true).queue();
        }
    }

    public static void ticketb(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("supportb_")) {
            String userid = event.getComponentId().substring("supportb_".length());

            TextInput email = TextInput.create("code", "Code", TextInputStyle.SHORT)
                    .setPlaceholder("code")
                    .setMinLength(0)
                    .setMaxLength(5) // or setRequiredRange(10, 100)
                    .build();

            TextInput body = TextInput.create("body", "Description", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Description...")
                    .setMinLength(25)
                    .setMaxLength(1000)
                    .build();

            Modal modal = Modal.create("supportmp_" + userid, "Support 🛠️")
                    .addActionRows(ActionRow.of(email), ActionRow.of(body))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    public static void supportmp(ModalInteractionEvent event) {
        User qoyri = event.getJDA().getUserById("776016158737432626");
        if (event.getModalId().startsWith("supportmp_")) {
            String userid = event.getModalId().substring("supportmp_".length());
            List<ModalMapping> values = event.getValues();

            String code = event.getValue("code").getAsString();
            String body = event.getValue("body").getAsString();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Support 🧑‍💻");
            embed.setColor(Color.blue);
            embed.addField("code :", code + "userID :" + userid, false);
            embed.addField("Objet", body, false);

            qoyri.openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(embed.build()).queue(message -> {
                    Button supportButton = Button.primary("Respond_" + userid, "Répondre");
                    message.editMessageEmbeds(embed.build()).setActionRow(supportButton).queue();
                });
            });

            event.reply("Merci ! 💖").setEphemeral(true).queue();
        }
    }
}
