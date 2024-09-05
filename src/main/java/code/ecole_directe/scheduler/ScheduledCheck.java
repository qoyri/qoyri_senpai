package code.ecole_directe.scheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import code.config.Database.DAOGuetInfo;
import code.ecole_directe.authentification;
import code.ecole_directe.devoir.DB_devoir;
import code.ecole_directe.devoir.devoir_command;
import code.ecole_directe.notes.DB_notes;
import code.ecole_directe.notes.notes_command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import static code.ecole_directe.devoir.connections_devoir.getNewDevoirs;
import static code.ecole_directe.notes.connections_notes.getNewNotes;


public class ScheduledCheck {

    private final JDA jda;

    public ScheduledCheck(JDA jda) {
        this.jda = jda;
    }

    public void start() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::checkForUpdates, 0, 2, TimeUnit.MINUTES);
    }

    private void checkForUpdates() {
        try {
            List<Map<String, String>> notesChannels = DAOGuetInfo.getAllNoteChannels();
            List<Map<String, String>> devoirChannels = DAOGuetInfo.getAllDevoirChannels();

            for (Map<String, String> channelInfo : notesChannels) {
                String guildId = channelInfo.get("guildId");
                String channelId = channelInfo.get("channelId");
                checkAndUpdate(guildId, channelId, true); // for notes.
            }

            for (Map<String, String> channelInfo : devoirChannels) {
                String guildId = channelInfo.get("guildId");
                String channelId = channelInfo.get("channelId");
                checkAndUpdate(guildId, channelId, false); // for devoirs.
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndUpdate(String guildId, String channelId, boolean isNotes) {
        try {
            // provide appropriate method from your DAO/Database access class to get user info using guildId and channelId
            Map<String, String> info = isNotes ? DAOGuetInfo.getNotesInfoForChannel(guildId, channelId) : DAOGuetInfo.getDevoirsInfoForChannel(guildId, channelId);

            String userId = info.get("userId");
            TextChannel channel = jda.getTextChannelById(channelId);

            JSONObject authResult = authentification.authenticate(userId);
            if (authResult.optInt("code") == 200 && authResult.optString("token") != null) {
                String token = authResult.getString("token");
                JSONObject data = authResult.getJSONObject("data");
                JSONArray accounts = data.getJSONArray("accounts");

                JSONObject firstAccount = accounts.getJSONObject(0);
                int eleveId = firstAccount.getInt("id");

                if (isNotes) {
                    updateNotes(token, eleveId, userId, channel);
                } else {
                    updateDevoirs(token, eleveId, userId, channel);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNotes(String token, int eleveId, String userId, TextChannel channel) throws IOException, SQLException {
        JSONArray newNotes = getNewNotes(token, eleveId, userId);
        String channelId = channel.getId();
        String guildId = channel.getGuild().getId();
        for (int i = 0; i < newNotes.length(); i++) {
            JSONObject note = newNotes.getJSONObject(i);
            notes_command.createAndSendNoteEmbed(channel, note);
            DB_notes.saveProcessedNoteId(userId, guildId, channelId, note.getInt("id")); // to DB
        }
    }

    private void updateDevoirs(String token, int eleveId, String userId, TextChannel channel) throws IOException, SQLException {
        JSONArray newDevoirs = getNewDevoirs(token, eleveId, userId);
        String channelId = channel.getId();
        String guildId = channel.getGuild().getId();
        for (int i = 0; i < newDevoirs.length(); i++) {
            JSONObject devoir = newDevoirs.getJSONObject(i);
            devoir_command.createAndSendDevoirsEmbed(channel, devoir);
            DB_devoir.saveProcessedDevoirId(userId, guildId, channelId, devoir.getInt("id")); // to DB
        }
    }
}