package code.color;

import java.awt.*;
import java.util.Random;

public class generateRandomColor {
    /**
     * Generates a random color.
     *
     * @return A randomly generated Color object.
     */
    public static Color generateRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
