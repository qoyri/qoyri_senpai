package code.config.Database;

import code.config.Database.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterDAO {

    public static void sauvegarderUtilisateur(String discordId, String username, String password, String dates, String prenom, String nom, String classe) throws SQLException {
        String sql = "INSERT INTO utilisateurs (discord_id, username, password, dates, prenom, nom, classe) VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (discord_id) DO UPDATE SET username = excluded.username, password = excluded.password, dates = excluded.dates, prenom = excluded.prenom, nom = excluded.nom, classe = excluded.classe;";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, discordId);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, dates);
            pstmt.setString(5, prenom);
            pstmt.setString(6, nom);
            pstmt.setString(7, classe);

            pstmt.executeUpdate();
        }
    }
}
