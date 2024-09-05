package code.ecole_directe.notes;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static code.base.path_for_all.r_version;
import static code.ecole_directe.notes.DB_notes.filterNewNotes;
import static code.ecole_directe.notes.DB_notes.getProcessedNoteIds;

public class connections_notes {

    private static final String BASE_URL = "https://api.ecoledirecte.com/v3/";
    private static final OkHttpClient client = new OkHttpClient();

    public static JSONArray getNewNotes(String token, int eleveId, String userId) throws IOException, SQLException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String payload = "data=" + URLEncoder.encode("{\"anneeScolaire\": \"\"}", StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(BASE_URL + "eleves/" + eleveId + "/notes.awp?verbe=get&v=" + r_version)
                .addHeader("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Token", token)
                .addHeader("Origin", "sorai sensei (discord bot connected to EcoleDirecte)/ V.2.03.15, for any problem contact me directly on discord: qoyri or to support@qoyri.fr")
                .post(RequestBody.create(mediaType, payload))
                .build();

        JSONArray processedNoteIds = new JSONArray(getProcessedNoteIds(userId)); // from DB

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JSONObject responseBody = new JSONObject(response.body().string());
            JSONArray notes = responseBody.getJSONObject("data").getJSONArray("notes");
            return filterNewNotes(notes, processedNoteIds);
        }
    }
}
