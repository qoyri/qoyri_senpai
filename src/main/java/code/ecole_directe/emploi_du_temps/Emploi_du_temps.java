package code.ecole_directe.emploi_du_temps;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static code.base.path_for_all.r_version;

public class Emploi_du_temps extends ListenerAdapter {
    private static final String BASE_URL = "https://api.ecoledirecte.com/v3/";
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * Retrieves the schedule for a student within a specified date range.
     *
     * @param token     the access token for authentication
     * @param dateDebut the starting date of the schedule (format: "yyyy-MM-dd")
     * @param dateFin   the ending date of the schedule (format: "yyyy-MM-dd")
     * @param eleveId   the ID of the student
     * @return the schedule for the student as a JSON string
     * @throws IOException if an I/O error occurs during the HTTP request
     */
    public static String getEmploiDuTemps(String token, String dateDebut, String dateFin, int eleveId) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String payload = "data=" + URLEncoder.encode("{\"dateDebut\":\"" + dateDebut + "\",\"dateFin\":\"" + dateFin + "\",\"avecTrous\":false}", StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(BASE_URL + "E/" + eleveId + "/emploidutemps.awp?verbe=get&v=" + r_version)
                .addHeader("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Token", token)
                .addHeader("Origin", "sorai sensei (discord bot connected to EcoleDirecte)/ V.2.03.15, for any problem contact me directly on discord: qoyri or to support@qoyri.fr")
                .post(RequestBody.create(mediaType, payload))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }
}
