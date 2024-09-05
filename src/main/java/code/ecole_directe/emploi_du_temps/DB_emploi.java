package code.ecole_directe.emploi_du_temps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static code.config.Database.DB.getConnection;

public class DB_emploi {
    public static void saveMessageAndUserIdToDatabase(String guildId, String userId, String channelId, String messageId) {
        String sql = "INSERT INTO emploi_info(user_id, guild_id, channel_id, message_id) VALUES(?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, guildId);
            pstmt.setString(3, channelId);
            pstmt.setString(4, messageId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Map<String, String> getMessageAndUserIdFromDatabase(String guildId, String channelId) {
        String sql = "SELECT user_id, message_id FROM emploi_info WHERE guild_id = ? AND channel_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, guildId);
            pstmt.setString(2, channelId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, String> result = new HashMap<>();
                result.put("userId", rs.getString("user_id"));
                result.put("messageId", rs.getString("message_id"));
                return result;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
