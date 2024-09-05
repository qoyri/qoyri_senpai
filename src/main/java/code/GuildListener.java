package code;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static code.base.path_for_all.PATH;

public class GuildListener extends ListenerAdapter {

    public static void SyncID(net.dv8tion.jda.api.events.guild.GuildJoinEvent event) {
        Guild guild = event.getGuild();
        updateGuildData(guild);
    }

    public static void SyncID(net.dv8tion.jda.api.events.guild.GenericGuildEvent event) {
        Guild guild = event.getGuild();
        updateGuildData(guild);
    }

    /**
     * Updates the guild data with the provided Guild object.
     * This method creates a directory for the server if it doesn't already exist.
     * It then obtains the JSON file path for the server and the notes file path.
     * The method saves the information of the text channels in the channelInfo map.
     * It creates a JSON object containing the server and channel information.
     * The channel information is stored in a JSON array.
     * Finally, the method writes the JSON data to the server file.
     *
     * @param guild The Guild object representing the server.
     */
    private static void updateGuildData(Guild guild) {
        String guildId = guild.getId();

        // Créez un répertoire pour le serveur s'il n'existe pas déjà
        Path guildDirectory = Path.of(PATH, guildId);
        if (!Files.exists(guildDirectory)) {
            try {
                Files.createDirectories(guildDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Obtenez le chemin du fichier JSON du serveur
        Path guildDataFile = Path.of(guildDirectory.toString(), "guild_data.json");
        Path guildNotesFile = Path.of(guildDirectory.toString(), "notes_info.json");

        // Enregistrez les informations des salons dans la map channelInfo
        Map<String, String> channelInfo = new HashMap<>();
        for (net.dv8tion.jda.api.entities.channel.Channel channel : guild.getTextChannels()) {
            channelInfo.put(channel.getId(), channel.getName());
        }

        // Créez un objet JSON avec les informations du serveur et des salons
        JSONObject guildData = new JSONObject();
        guildData.put("guild_id", guild.getId());
        guildData.put("guild_name", guild.getName());

        JSONArray channelsArray = new JSONArray();
        for (Map.Entry<String, String> entry : channelInfo.entrySet()) {
            JSONObject channelObject = new JSONObject();
            channelObject.put("channel_id", entry.getKey());
            channelObject.put("channel_name", entry.getValue());
            channelsArray.add(channelObject);
        }
        guildData.put("channels", channelsArray);

        // Écrivez les données JSON dans le fichier du serveur
        try (FileWriter file = new FileWriter(guildDataFile.toString())) {
            file.write(guildData.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}