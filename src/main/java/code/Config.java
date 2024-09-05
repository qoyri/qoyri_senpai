package code;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

import static code.base.path_for_all.PATH_TOKEN;

/**
 * The Config class represents the configuration settings for the application.
 */
public class Config {
    public static String TOKEN;
    private static final String AUTH_FILE_PATH = PATH_TOKEN;

    static {
        try {
            TOKEN = "SECRET";
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to load config.json");
        }
    }
}
