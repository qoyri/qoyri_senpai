package code.ecole_directe.devoir;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static code.config.Database.DB.getConnection;

public class DB_devoir {

    public static JSONObject loadOrCreateDevoirsInfo(Path path, String userId, String channelId) {
        if (Files.exists(path)) {
            // Charger les données existantes
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                return new JSONObject(new JSONTokener(reader));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Créer un nouveau JSONObject si le fichier n'existe pas
        JSONObject devoirsInfo = new JSONObject();
        devoirsInfo.put("userId", userId);
        devoirsInfo.put("channelId", channelId);
        devoirsInfo.put("processedDevoirIds", new JSONArray());
        return devoirsInfo;
    }

    public static void saveDevoirsInfo(File file, JSONObject devoirsInfo) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(devoirsInfo.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray filterNewDevoirs(JSONArray devoirs, JSONArray processedDevoirsIds) {
        JSONArray newDevoirs = new JSONArray();
        List<Object> processedDevoirsIdList = processedDevoirsIds.toList();
        for (int i = 0; i < devoirs.length(); i++) {
            JSONObject devoir = devoirs.getJSONObject(i);
            if (!processedDevoirsIdList.contains(devoir.getInt("id"))) {
                newDevoirs.put(devoir);
            }
        }
        return newDevoirs;
    }




    public static void saveProcessedDevoirId(String userId, String guildId, String channelId, int devoirId) throws SQLException {
        String selectSql = "SELECT * FROM devoirs_info WHERE user_id = ? AND guild_id = ?;";
        String insertSql = "INSERT INTO devoirs_info (user_id, guild_id, channel_id, processed_devoir_ids) VALUES (?, ?, ?, ARRAY[?]);";
        String updateSql = "UPDATE devoirs_info SET processed_devoir_ids = array_append(processed_devoir_ids, ?) WHERE user_id = ? AND guild_id = ?;";

        try (Connection conn = getConnection();

             PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
             PreparedStatement insertPstmt = conn.prepareStatement(insertSql);
             PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

            selectPstmt.setString(1, userId);
            selectPstmt.setString(2, guildId);
            ResultSet rs = selectPstmt.executeQuery();

            if (rs.next()) {
                // record exists, update it
                updatePstmt.setInt(1, devoirId);
                updatePstmt.setString(2, userId);
                updatePstmt.setString(3, guildId);
                updatePstmt.executeUpdate();
            } else {
                // record doesn't exist, insert
                insertPstmt.setString(1, userId);
                insertPstmt.setString(2, guildId);
                insertPstmt.setString(3, channelId);
                insertPstmt.setInt(4, devoirId);
                insertPstmt.executeUpdate();
            }
        }
    }

    public static ArrayList<Integer> getProcessedDevoirIds(String userId) throws SQLException
    {
        ArrayList<Integer> processedDevoirIds = new ArrayList<>();
        String sql = "SELECT processed_devoir_ids FROM devoirs_info WHERE user_id = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // convert Array to ArrayList
                Integer[] intArray = (Integer[]) rs.getArray("processed_devoir_ids").getArray();
                Collections.addAll(processedDevoirIds, intArray);
            }
        }
        return processedDevoirIds;
    }
}
