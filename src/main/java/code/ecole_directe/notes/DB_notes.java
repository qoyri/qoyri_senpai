package code.ecole_directe.notes;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
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

public class DB_notes {

    /**
     * Loads or creates a JSON object containing notes information from the specified file.
     *
     * @param path      The file to load or create the notes information from.
     * @param userId    The user ID associated with the notes information.
     * @param channelId The channel ID associated with the notes information.
     * @return A JSON object containing the notes information. If the file exists, it will load the existing data from the file.
     * If the file does not exist, it will create a new JSON object with the provided user ID, channel ID, and an empty array of processed note IDs.
     */
    public static JSONObject loadOrCreateNotesInfo(Path path, String userId, String channelId) {
        if (Files.exists(path)) {
            // Charger les données existantes
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                return new JSONObject(new JSONTokener(reader));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Créer un nouveau JSONObject si le fichier n'existe pas
        JSONObject notesInfo = new JSONObject();
        notesInfo.put("userId", userId);
        notesInfo.put("channelId", channelId);
        notesInfo.put("processedNoteIds", new JSONArray());
        return notesInfo;
    }

    /**
     * Saves the JSON object containing notes information to the specified file.
     *
     * @param file      The file to save the notes information to.
     * @param notesInfo The JSON object containing the notes information to be saved.
     */
    public static void saveNotesInfo(File file, JSONObject notesInfo) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(notesInfo.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters the given notes array to remove any notes that have already been processed.
     *
     * @param notes            the array of notes to be filtered
     * @param processedNoteIds the array of processed note IDs
     * @return a new JSONArray object containing the notes that have not yet been processed
     */
    public static JSONArray filterNewNotes(JSONArray notes, JSONArray processedNoteIds) {
        JSONArray newNotes = new JSONArray();
        List<Object> processedNoteIdList = processedNoteIds.toList();
        for (int i = 0; i < notes.length(); i++) {
            JSONObject note = notes.getJSONObject(i);
            if (!processedNoteIdList.contains(note.getInt("id"))) {
                newNotes.put(note);
            }
        }
        return newNotes;
    }



    public static void saveProcessedNoteId(String userId, String guildId, String channelId, int noteId) throws SQLException {
        String selectSql = "SELECT * FROM notes_info WHERE user_id = ? AND guild_id = ?;";
        String insertSql = "INSERT INTO notes_info (user_id, guild_id, channel_id, processed_note_ids) VALUES (?, ?, ?, ARRAY[?]);";
        String updateSql = "UPDATE notes_info SET processed_note_ids = array_append(processed_note_ids, ?) WHERE user_id = ? AND guild_id = ?;";

        try (Connection conn = getConnection();

             PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
             PreparedStatement insertPstmt = conn.prepareStatement(insertSql);
             PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

            selectPstmt.setString(1, userId);
            selectPstmt.setString(2, guildId);
            ResultSet rs = selectPstmt.executeQuery();

            if (rs.next()) {
                // record exists, update it
                updatePstmt.setInt(1, noteId);
                updatePstmt.setString(2, userId);
                updatePstmt.setString(3, guildId);
                updatePstmt.executeUpdate();
            } else {
                // record doesn't exist, insert
                insertPstmt.setString(1, userId);
                insertPstmt.setString(2, guildId);
                insertPstmt.setString(3, channelId);
                insertPstmt.setInt(4, noteId);
                insertPstmt.executeUpdate();
            }
        }
    }

    public static ArrayList<Integer> getProcessedNoteIds(String userId) throws SQLException
    {
        ArrayList<Integer> processedNoteIds = new ArrayList<>();
        String sql = "SELECT processed_note_ids FROM notes_info WHERE user_id = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // convert Array to ArrayList
                Integer[] intArray = (Integer[]) rs.getArray("processed_note_ids").getArray();
                Collections.addAll(processedNoteIds, intArray);
            }
        }
        return processedNoteIds;
    }
}
