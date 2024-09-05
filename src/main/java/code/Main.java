package code;

import code.base.basic.Informations;
import code.ecole_directe.emploi_du_temps.Emploi_du_temps;
import code.ecole_directe.scheduler.ScheduledCheck;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static code.GuildListener.SyncID;
import static code.commandSYNC.synchronizeCommands;

public class Main {
    public static void main(String[] args) throws LoginException {
        GatewayIntent[] allIntents = GatewayIntent.values();
        JDABuilder builder = JDABuilder.createDefault(Config.TOKEN)
                .setEnabledIntents(Arrays.asList(allIntents))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new CommandHandler(), new Emploi_du_temps())
                .setActivity(Activity.listening("API de mort"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB);

        JDA jda = builder.build();
        jda.addEventListener(new ListenerAdapter() {
            public void onReady(ReadyEvent event) {
                printBotStatus(jda, allIntents);
                synchronizeCommands(jda);
            }
        });

        ScheduledCheck ScheduledCheck = new ScheduledCheck(jda);
        ScheduledCheck.start();
        Informations Informations = new Informations(jda);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onSessionDisconnect(SessionDisconnectEvent event) {
                executorService.shutdown();
            }

            @Override
            public void onGuildJoin(GuildJoinEvent event) {
                SyncID(event);
            }

            @Override
            public void onGenericGuild(GenericGuildEvent event) {
                SyncID(event);
            }

            @Override
            public void onGuildAvailable(GuildAvailableEvent event) {
                SyncID(event);
            }
        });
    }

    private static void printBotStatus(JDA jda, GatewayIntent[] intents) {
        System.out.println("\u001B[34m[INFO]\u001B[0m Bot connecté avec succès !");
        System.out.println("\u001B[34m[INFO]\u001B[0m Nom du bot : " + jda.getSelfUser().getName());
        System.out.println("\u001B[34m[INFO]\u001B[0m ID du bot : " + jda.getSelfUser().getId());
        System.out.println("\u001B[34m[INFO]\u001B[0m Statut du bot : " + jda.getPresence().getStatus());
        System.out.println("\u001B[34m[INFO]\u001B[0m Activité du bot : " + jda.getPresence().getActivity());
        System.out.println("\u001B[34m[INFO]\u001B[0m Nombre de serveurs : " + jda.getGuilds().size());
        System.out.println("\u001B[34m[INFO]\u001B[0m Serveurs :");
        for (Guild guild : jda.getGuilds()) {
            System.out.println("\u001B[35m[SERVEUR]\u001B[0m - " + guild.getName() + " (ID : " + guild.getId() + ")");
        }
        for (GatewayIntent intent : intents) {
            System.out.println("\u001B[33m[INTENT]\u001B[0m - Intent : " + intent.name() + " - " + intent.getRawValue());
        }
    }
}