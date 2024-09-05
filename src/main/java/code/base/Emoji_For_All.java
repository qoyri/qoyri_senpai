package code.base;

import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 * The Emoji_For_All class provides static constants for common emojis.
 * These emojis include a refresh emoji and a self-report emoji.
 * They are represented as strings and converted to Emoji objects.
 * The emoji ids are stored as strings and converted to long values before creating the Emoji objects.
 */
public class Emoji_For_All {

    public static String Refresh_emojiId = "1178711778297925653";
    public static Emoji Refresh_emoji = Emoji.fromCustom("refresh", Long.parseLong(Refresh_emojiId), true);


    public static String Self_emojiId = "1179337398035943444";
    public static Emoji Self_emoji = Emoji.fromCustom("self_report", Long.parseLong(Self_emojiId), false);

    public static String Ticket_emojiId = "1179727900107358218";
    public static Emoji Ticket_emoji = Emoji.fromCustom("ticket", Long.parseLong(Ticket_emojiId), true);
    public static String commandes_emojiId = "1181598245626069022";
    public static Emoji commandes_emoji = Emoji.fromCustom("command", Long.parseLong(commandes_emojiId), true);
    public static String tos_emojiId = "1181599625036173334";
    public static Emoji tos_emoji = Emoji.fromCustom("TOS", Long.parseLong(tos_emojiId), false);
    public static String problem_emojiId = "1181600797805527071";
    public static Emoji problem_emoji = Emoji.fromCustom("problem", Long.parseLong(problem_emojiId), true);

}
