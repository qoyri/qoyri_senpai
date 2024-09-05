package code.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static code.config.Database.DB.getConnection;

public class DAOGuetInfo {
    public static List<Map<String, String>> getAllNoteChannels() throws SQLException {
        String sql = "SELECT guild_id, channel_id FROM notes_info";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            List<Map<String, String>> noteChannels = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> info = new HashMap<>();
                info.put("guildId", rs.getString("guild_id"));
                info.put("channelId", rs.getString("channel_id"));
                noteChannels.add(info);
            }

            return noteChannels;
        }
    }

    public static List<Map<String, String>> getAllDevoirChannels() throws SQLException {
        String sql = "SELECT guild_id, channel_id FROM devoirs_info";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            List<Map<String, String>> devoirChannels = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> info = new HashMap<>();
                info.put("guildId", rs.getString("guild_id"));
                info.put("channelId", rs.getString("channel_id"));
                devoirChannels.add(info);
            }

            return devoirChannels;
        }
    }

    public static Map<String, String> getNotesInfoForChannel(String guildId, String channelId) throws SQLException {
        String sql = "SELECT user_id FROM notes_info WHERE guild_id = ? AND channel_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, guildId);
            pstmt.setString(2, channelId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, String> info = new HashMap<>();
                info.put("userId", rs.getString("user_id"));
                return info;
            } else {
                return new HashMap<>();
            }
        }
    }

    public static Map<String, String> getDevoirsInfoForChannel(String guildId, String channelId) throws SQLException {
        String sql = "SELECT user_id FROM devoirs_info WHERE guild_id = ? AND channel_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, guildId);
            pstmt.setString(2, channelId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, String> info = new HashMap<>();
                info.put("userId", rs.getString("user_id"));
                return info;
            } else {
                return new HashMap<>();
            }
        }
    }
}
