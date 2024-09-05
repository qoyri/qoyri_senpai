package code.base.basic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.concurrent.TimeUnit;

import static code.base.path_for_all.PATH_IDENT;
import static code.color.generateRandomColor.generateRandomColor;

public class Informations extends ListenerAdapter {

    private static JDA jda;

    public Informations(JDA jda) {
        Informations.jda = jda;
    }

    public static void info_slash(SlashCommandInteractionEvent event) {
        Color randomColor = generateRandomColor();

        //int nbServeurs = jda.getGuilds().size();
        //int nbUtilisateursEcoleDirecte = getNombreUtilisateursEcoleDirecte();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Informations techniques du bot")
                .setDescription("Voici les caractéristiques techniques du bot :")
                .addBlankField(false)
                .setThumbnail("https://cdn.discordapp.com/attachments/1140349536875847700/1181570231852879892/568.gif")
                .setColor(randomColor)
                .addField("Plateforme", System.getProperty("os.name"), true)
                .addField("Processeur", System.getProperty("os.arch"), true)
                .addField("Nombre de coeurs disponibles", String.valueOf(Runtime.getRuntime().availableProcessors()), true)
                .addBlankField(false)
                .addField("Mémoire utilisée (Mo)", String.valueOf(Runtime.getRuntime().totalMemory() / (1024 * 1024)), true)
                .addField("Java version", System.getProperty("java.version"), true)
                .addBlankField(false)
                //.addField("serveurs", String.valueOf(nbServeurs), true)
                //.addField("Comptes EcoleDirecte", String.valueOf(nbUtilisateursEcoleDirecte), true)
                //.addBlankField(false)
                .setImage("https://cdn.discordapp.com/attachments/1140349536875847700/1181368394537246730/ban.png")
                .setFooter("Bot créé avec ❤️ par qoyri.");

        event.replyEmbeds(embed.build()).queue((message -> message.deleteOriginal().queueAfter(30, TimeUnit.SECONDS)));

    }

    private static int getNombreUtilisateursEcoleDirecte() {
        try {
            Path path = Path.of(PATH_IDENT);
            return (int) Files.list(path).filter(Files::isRegularFile).count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
