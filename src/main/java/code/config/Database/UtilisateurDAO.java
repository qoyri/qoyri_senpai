package code.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static code.config.Database.DB.getConnection;

public class UtilisateurDAO {

    public static Map<String, String> recupererInfosUtilisateur(String discordId) throws SQLException {
        Map<String, String> infosUtilisateur = new HashMap<>();

        String sql = "SELECT username, password, dates, prenom, nom, classe FROM utilisateurs WHERE discord_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, discordId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                infosUtilisateur.put("username", rs.getString("username"));
                infosUtilisateur.put("password", rs.getString("password"));
                infosUtilisateur.put("dates", rs.getString("dates"));
                infosUtilisateur.put("prenom", rs.getString("prenom"));
                infosUtilisateur.put("nom", rs.getString("nom"));
                infosUtilisateur.put("classe", rs.getString("classe"));
            }
        }

        return infosUtilisateur;
    }
}
