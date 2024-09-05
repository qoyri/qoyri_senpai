package code.ecole_directe.emploi_du_temps;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The TimetableImageCreator class is responsible for creating a timetable image from a JSON array
 * and saving it to a specified file path.
 */
public class TimetableImageCreator {

    /**
     * Creates a timetable image based on the given data and saves it to the specified file path.
     *
     * @param data     the JSONArray containing the timetable data
     * @param filePath the file path where the timetable image will be saved
     * @throws IOException if there is an error while reading or writing the image file
     */
    public static void createTimetableImage(JSONArray data, String filePath) throws IOException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<JSONObject> sortedData = data.toList().stream()
                .map(o -> new JSONObject((Map<?, ?>) o))
                .sorted(Comparator.comparing(o -> LocalDate.parse(o.getString("start_date").substring(0, 10), dateFormatter)))
                .collect(Collectors.toList());

        int width = 2840; // Largeur pour le format 16/9 en 1080p
        int height = 1440; // Hauteur pour le format 1080p
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rowHeight = height / 13; // Hauteur d'une ligne horaire
        int headerHeight = rowHeight; // Espace pour les jours de la semaine et les heures
        int leftMargin = 100; // Espace pour les heures sur le côté gauche
        int dayWidth = (width - leftMargin) / 7;

        Font hourFont = new Font("Arial", Font.PLAIN, 30); // Police pour les heures
        Font dayFont = new Font("Arial", Font.BOLD, 35); // Police pour les jours
        Font courseFont = new Font("Arial", Font.BOLD, 20); // Police pour le nom du cours

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        graphics.setFont(hourFont);
        graphics.setColor(Color.BLACK);
        // Dessiner les lignes horaires et les heures
        for (int hour = 7; hour <= 18; hour++) {
            int y = headerHeight + (hour - 7) * rowHeight;
            graphics.setColor(Color.BLACK);
            graphics.setStroke(new BasicStroke(0 == 0 ? 2 : 1)); // Épaisseur plus importante pour les heures pleines
            graphics.drawLine(leftMargin, y, width, y); // Ligne horaire
            // Texte de l'heure aligné avec la ligne
            graphics.drawString(hour + "h", 20, y);
        }

        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        Map<LocalDate, String> dayOfWeekMap = new HashMap<>();

        // On suppose que la semaine commence le lundi et se termine le dimanche
        LocalDate startOfWeek = LocalDate.parse(sortedData.get(0).getString("start_date").substring(0, 10), dateFormatter).with(java.time.DayOfWeek.MONDAY);
        for (int i = 0; i < 7; i++) {
            dayOfWeekMap.put(startOfWeek.plusDays(i), jours[i]);
        }

        // Définir la police pour les jours et dessiner les jours de la semaine
        graphics.setFont(dayFont);
        FontMetrics dayMetrics = graphics.getFontMetrics(dayFont);
        int daySpacing = leftMargin; // Position initiale pour le premier jour
        for (String jour : jours) {
            int stringWidth = dayMetrics.stringWidth(jour); // Largeur du texte du jour
            int dayX = daySpacing + (dayWidth - stringWidth) / 2; // Centrer le texte dans la colonne du jour
            graphics.drawString(jour, dayX, headerHeight - 10); // Positionner le texte des jours
            daySpacing += dayWidth; // Passer à la position du jour suivant
        }

        graphics.setFont(courseFont);
        FontMetrics courseMetrics = graphics.getFontMetrics(courseFont);

        for (JSONObject cours : sortedData) {
            LocalDate date = LocalDate.parse(cours.getString("start_date").substring(0, 10), dateFormatter);
            LocalTime startTime = LocalTime.parse(cours.getString("start_date").substring(11, 16), timeFormatter);
            LocalTime endTime = LocalTime.parse(cours.getString("end_date").substring(11, 16), timeFormatter);

            String dayOfWeek = dayOfWeekMap.get(date);
            int dayIndex = Arrays.asList(jours).indexOf(dayOfWeek);

            int startY = headerHeight + (startTime.getHour() - 7) * rowHeight + (startTime.getMinute() * rowHeight) / 60;
            int endY = headerHeight + (endTime.getHour() - 7) * rowHeight + (endTime.getMinute() * rowHeight) / 60;

            graphics.setStroke(new BasicStroke(2)); // Définir l'épaisseur de la ligne
            for (int i = 1; i < jours.length; i++) { // Commencer à 1 pour ignorer la première colonne
                int x = leftMargin + i * dayWidth;
                graphics.drawLine(x, headerHeight, x, height); // Ligne verticale pour chaque jour
            }

            // La couleur du cours et le remplissage du rectangle
            Color courseColor = Color.decode(cours.optString("color", "#FFFFFF"));
            graphics.setColor(courseColor);
            graphics.fillRoundRect(leftMargin + dayIndex * dayWidth, startY, dayWidth - 10, endY - startY, 20, 20); // Coins arrondis
            graphics.setColor(Color.BLACK);
            graphics.drawRoundRect(leftMargin + dayIndex * dayWidth, startY, dayWidth - 10, endY - startY, 20, 20); // Bordure noire

            // Centrage du texte du nom du cours
            String matiere = cours.optString("matiere", "N/A");
            int matiereTextWidth = courseMetrics.stringWidth(matiere);
            int matiereTextX = leftMargin + dayIndex * dayWidth + (dayWidth - 10 - matiereTextWidth) / 2;
            graphics.setColor(Color.BLACK);
            graphics.drawString(matiere, matiereTextX, startY + 5 + courseMetrics.getAscent());

            // Changer la police pour le nom du professeur
            Font profFont = new Font("Arial", Font.ITALIC, 18); // Police plus petite et italique pour le nom du professeur
            graphics.setFont(profFont);

            String professeur = cours.optString("prof", "N/A");
            int textWidth = courseMetrics.stringWidth(matiere);
            int profTextWidth = courseMetrics.stringWidth(professeur); // Largeur du texte du professeur
            int textX = leftMargin + dayIndex * dayWidth + (dayWidth - 10 - textWidth) / 2;
            int profTextX = leftMargin + dayIndex * dayWidth + (dayWidth - 10 - profTextWidth) / 2; // X pour centrer le texte du professeur
            graphics.setColor(Color.BLACK);
            graphics.drawString(professeur, profTextX, startY + 20 + courseMetrics.getAscent() * 2);
            graphics.setFont(courseFont);
            graphics.drawString(matiere, textX, startY + 5 + courseMetrics.getAscent());

            // Centrage du texte de l'heure du cours
            String timeString = cours.getString("start_date").substring(11, 16) + "-" + cours.getString("end_date").substring(11, 16);
            textWidth = courseMetrics.stringWidth(timeString);
            textX = leftMargin + dayIndex * dayWidth + (dayWidth - 10 - textWidth) / 2;
            graphics.drawString(timeString, textX, endY - 10);
        }

        graphics.dispose();
        ImageIO.write(image, "png", new File(filePath));
    }
}
