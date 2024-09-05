package code.ecole_directe.devoir;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static code.base.path_for_all.r_version;
import static code.ecole_directe.devoir.DB_devoir.getProcessedDevoirIds;

public class connections_devoir {

    private static final String BASE_URL = "https://api.ecoledirecte.com/v3/";
    private static final OkHttpClient client = new OkHttpClient();

    public static JSONArray getNewDevoirs(String token, int eleveId, String userId) throws IOException, SQLException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String payload = "data=" + URLEncoder.encode("{ }", StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(BASE_URL + "Eleves/" + eleveId + "/cahierdetexte.awp?verbe=get&v=" + r_version)
                .addHeader("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Token", token)
                .addHeader("Origin", "sorai sensei (discord bot connected to EcoleDirecte)/ V.2.03.15, for any problem contact me directly on discord: qoyri or to support@qoyri.fr")
                .post(RequestBody.create(mediaType, payload))
                .build();

        JSONArray processedDevoirsIds = new JSONArray(getProcessedDevoirIds(userId)); // from DB

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject responseBody = new JSONObject(response.body().string());
            JSONObject data = responseBody.getJSONObject("data");
            JSONArray newDevoirs = new JSONArray();

            for (String date : data.keySet()) {
                JSONArray devoirsForDate = data.getJSONArray(date);
                for (int i = 0; i < devoirsForDate.length(); i++) {
                    JSONObject devoir = devoirsForDate.getJSONObject(i);
                    int idDevoir = devoir.getInt("idDevoir");
                    if (!processedDevoirsIds.toString().contains(String.valueOf(idDevoir))) {
                        JSONObject devoirDetails = getDevoirDetails(token, eleveId, date, idDevoir);
                        newDevoirs.put(devoirDetails);
                    }
                }
            }
            return newDevoirs;
        }
    }

    private static JSONObject getDevoirDetails(String token, int eleveId, String date, int idDevoir) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String payload = "data=" + URLEncoder.encode("{}", StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(BASE_URL + "Eleves/" + eleveId + "/cahierdetexte/" + date + ".awp?verbe=get&v=" + r_version)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Token", token)
                .addHeader("Origin", "sorai sensei (discord bot connected to EcoleDirecte)/ V.2.03.15, for any problem contact me directly on discord: qoyri or to support@qoyri.fr")
                .post(RequestBody.create(mediaType, payload))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JSONObject responseObject = new JSONObject(responseBody);
            JSONObject data = responseObject.getJSONObject("data");

            // Récupérer la date depuis l'objet 'data'
            String dateDevoir = data.getString("date");

            JSONArray matieres = data.getJSONArray("matieres");
            for (int i = 0; i < matieres.length(); i++) {
                JSONObject matiere = matieres.getJSONObject(i);
                if (matiere.getInt("id") == idDevoir) {
                    JSONObject aFaire = matiere.getJSONObject("aFaire");
                    matiere.put("aFaire", aFaire);
                    matiere.put("dateDevoir", dateDevoir);// Ajoutez 'aFaire' à 'matiere'
                    return matiere; // Retourner l'objet 'matiere' avec les détails de 'aFaire' inclus
                }
            }
        }
        return null;
    }

}
