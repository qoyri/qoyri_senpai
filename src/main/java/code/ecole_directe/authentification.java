package code.ecole_directe;

import code.config.Database.UtilisateurDAO;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static code.base.path_for_all.r_version;
import static code.config.Database.UtilisateurDAO.recupererInfosUtilisateur;
import static code.encryption.crypt.KEY_CRYPT;
import static code.encryption.crypt_and_decrypt.decryptAES;

public class authentification {
    private static final String BASE_URL = "https://api.ecoledirecte.com/v3/";
    private static final OkHttpClient client = new OkHttpClient();

    private static final String[] MOIS = {
            "janvier", "février", "mars", "avril", "mai", "juin",
            "juillet", "août", "septembre", "octobre", "novembre", "décembre"
    };


    public static JSONObject authenticate(String discordId) throws Exception {
        Map<String, String> infosUtilisateur = recupererInfosUtilisateur(discordId);
        String username = infosUtilisateur.get("username");
        // Assurez-vous de décrypter le mot de passe ici avant de l'utiliser
        String passwordencrypt = infosUtilisateur.get("password");
        String password = decryptAES(passwordencrypt, KEY_CRYPT);



        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String payload = "data={\"identifiant\":\"" + username + "\",\"motdepasse\":\"" + password + "\"}";

        Request request = new Request.Builder()
                .url(BASE_URL + "login.awp?v=" + r_version)
                .post(RequestBody.create(mediaType, payload))
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Origin", "sorai sensei (discord bot connected to EcoleDirecte)/ V.5.03.423, for any problem contact me directly on discord: qoyri or to support@qoyri.fr")
                .build();
        //System.out.println(request);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            // Vérifie si la connexion est réussie ou si une A2F est requise
            if (jsonObject.optInt("code") == 200 && jsonObject.optString("token") != null) {
                return processSuccessfulResponse(jsonObject);
            } else if (jsonObject.optInt("code") == 250) {
                // Vous avez reçu un code 250, cela signifie que vous devez obtenir les propositions d'A2F
                String token = jsonObject.optString("token");
                // Exemple : Envoyez une nouvelle requête pour obtenir les propositions d'A2F
                JSONObject a2fResponse = obtenirPropositionsA2F(token);
                // a2fResponse contiendrait la vraie question et les propositions
                if (a2fResponse != null) {
                    return handleA2F(token, discordId, a2fResponse, username, password); // Traitez la réponse A2F avec les propositions
                } else {
                    //System.out.print("aaaa" + a2fResponse);
                    return processFailureResponse("Impossible d'obtenir les propositions d'A2F");
                }

        } else if (jsonObject.optInt("code") == 505) {
                // Identifiant ou mot de passe invalide
                return processFailureResponse("Identifiant et/ou mot de passe invalide !");
            } else {
                // Autres erreurs
                return processFailureResponse("Erreur inconnue");
            }
        } catch (JSONException e) {
            throw new IOException("Erreur lors de l'interprétation de la réponse de l'API", e);
        }
    }

    private static JSONObject processSuccessfulResponse(JSONObject jsonObject) throws JSONException {
        JSONObject authResult = new JSONObject();
        JSONObject data = jsonObject.getJSONObject("data");
        org.json.JSONArray accounts = data.getJSONArray("accounts");
        JSONObject userAccount = accounts.getJSONObject(0);
        int eleveId = userAccount.getInt("id");

        authResult.put("token", jsonObject.getString("token"));
        authResult.put("eleveId", eleveId);
        authResult.put("status", "success");
        return authResult;
    }

    private static JSONObject processFailureResponse(String message) throws JSONException {
        JSONObject authResult = new JSONObject();
        authResult.put("status", "failure");
        authResult.put("errorMessage", message);
        return authResult;
    }

    private static JSONObject obtenirPropositionsA2F(String token) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "data={}"); // Les données semblent être vides selon votre observation

        Request requestA2F = new Request.Builder()
                .url(BASE_URL + "connexion/doubleauth.awp?verbe=get&v=4.53.4")
                .post(body)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Token", token) // Utilisez le token reçu précédemment
                .build();

        try (Response responseA2F = client.newCall(requestA2F).execute()) {
            String newXToken = responseA2F.header("X-Token");
            String responseBodyA2F = responseA2F.body().string();
            JSONObject jsonObjectA2F = new JSONObject(responseBodyA2F);

            // Assurez-vous de vérifier le code de réponse et de traiter correctement les erreurs
            if (jsonObjectA2F.optInt("code") == 200) {
                jsonObjectA2F.put("newXToken", newXToken);
                return jsonObjectA2F; // Retournez la réponse contenant les propositions d'A2F
            } else {
                // Gérez les cas d'erreur
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    private static JSONObject handleA2F(String token, String discordId, JSONObject initialResponse, String username, String password) throws IOException, JSONException, SQLException {
        // Obtenez les détails de la question A2F de l'initialResponse
        List<String> propositions = new ArrayList<>();
        String xtoken = initialResponse.optString("newXToken");
        //System.out.println(xtoken);
        JSONArray propositionsJsonArray = initialResponse.getJSONObject("data").getJSONArray("propositions");
        for (int i = 0; i < propositionsJsonArray.length(); i++) {
            String propositionEncoded = propositionsJsonArray.getString(i);
            String proposition = new String(Base64.getDecoder().decode(propositionEncoded), StandardCharsets.UTF_8);
            propositions.add(proposition);
        }

        // Utilisez les informations de l'utilisateur pour déterminer la réponse
        Map<String, String> infosUtilisateur = UtilisateurDAO.recupererInfosUtilisateur(discordId);
        String reponse = determinerReponseA2F(infosUtilisateur, propositions);
        String reponseencode = new String(Base64.getEncoder().encode(reponse.getBytes()), StandardCharsets.UTF_8);

        // Préparer la réponse A2F à soumettre
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String payload = "data={\"choix\":\"" + reponseencode + "\"}";
        //System.out.println("payload   " + payload);

        // Remplacez "URL_A2F" par l'URL réelle pour soumettre la réponse A2F
        Request requestA2F = new Request.Builder()
                .url(BASE_URL + "connexion/doubleauth.awp?verbe=post&v=" + r_version)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Origin", "sorai sensei (discord bot connected to EcoleDirecte)/ V.5.03.423, for any problem contact me directly on discord: qoyri or to support@qoyri.fr")
                .addHeader("X-Token", xtoken) // Utilisez le token reçu précédemment
                .post(RequestBody.create(mediaType, payload))
                .build();
        //System.out.println("requete   " + requestA2F);

        try (Response responseA2F = client.newCall(requestA2F).execute()) {
            String xToken = responseA2F.header("X-Token");
            //System.out.println("reponseA2F   " + responseA2F);
            String responseBodyA2F = responseA2F.body().string();
            JSONObject jsonObjectA2F = new JSONObject(responseBodyA2F);
            //System.out.println("reponse   " + jsonObjectA2F);
            //System.out.println("xTokent   " + xToken);

            // Vérifiez la réussite de l'authentification A2F
            if (jsonObjectA2F.optInt("code") == 200) {
                String cn = jsonObjectA2F.getJSONObject("data").getString("cn");
                String cv = jsonObjectA2F.getJSONObject("data").getString("cv");
                jsonObjectA2F.put("cn", cn); // Ajoutez cn à l'objet JSON pour le retourner
                jsonObjectA2F.put("cv", cv); // Ajoutez cv à l'objet JSON pour le retourner
                return envoyerRequeteFinalisation(username, password, xToken, cn, cv);
            } else {
                return processFailureResponse("Échec de l'authentification A2F");
            }
        }
    }
    private static String determinerReponseA2F(Map<String, String> infosUtilisateur, List<String> propositions) {
        // Extrait le jour, le mois et l'année de la date de naissance
        String[] dateParts = infosUtilisateur.get("dates").split("/");
        String jour = dateParts[0];
        String mois = MOIS[Integer.parseInt(dateParts[1]) - 1]; // Convertit le numéro du mois en nom de mois
        String annee = dateParts[2];
        //System.out.println("proposition     " + propositions);

        // Vérifie chaque proposition
        for (String proposition : propositions) {
            if (proposition.equalsIgnoreCase(jour) || proposition.equalsIgnoreCase(mois) || proposition.equalsIgnoreCase(annee)) {
                return proposition; // Retourne la première correspondance trouvée
            }
        }
        return ""; // Retourne une chaîne vide si aucune correspondance n'est trouvée
    }


    private static JSONObject envoyerRequeteFinalisation(String identifiant, String motDePasse, String token, String cn, String cv) throws IOException {
        //System.out.print(token);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        String payload = String.format("data={\"identifiant\": \"%s\", \"motdepasse\": \"%s\", \"isReLogin\": false, \"cn\": \"%s\", \"cv\": \"%s\", \"uuid\": \"\", \"fa\": [{\"cn\": \"%s\", \"cv\": \"%s\"}]}",
                identifiant, motDePasse, cn, cv, cn, cv);

        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
                .url(BASE_URL + "login.awp?v=" + r_version)
                .post(body)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0")
                .addHeader("Origin", "sorai sensei (discord bot connected to EcoleDirecte)/ V.5.03.423, for any problem contact me directly on discord: qoyri or to support@qoyri.fr")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Token", token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            //System.out.println("reponse   " + response);
            if (!response.isSuccessful()) throw new IOException("Erreur inattendue: " + response);

            String responseBody = response.body().string();
            return new JSONObject(responseBody);
        } catch (JSONException e) {
            throw new IOException("Erreur lors de l'analyse de la réponse JSON", e);
        }
    }
}
