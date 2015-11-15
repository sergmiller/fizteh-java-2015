package ru.fizteh.fivt.students.sergmiller.twitterStream;

import org.json.JSONObject;
import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by sergmiller on 04.11.15.
 */
public class TweetFormater {
    public static final int SEPARATOR_LENGTH = 80;
    /**
     * Get blue letter.
     */
    static final String ANSI_RESET = "\u001B[0m";
    /**
     * Return white color of word.
     */
    static final String ANSI_BLUE = "\u001B[34m";

    public static String highlightUserName(final String userName) {
        return "@" + ANSI_BLUE + userName + ANSI_RESET + ": ";
    }

    public static String tweetsSeparator() {
        StringBuilder separator = new StringBuilder("\n");
        for (int i = 0; i < SEPARATOR_LENGTH; ++i) {
            separator.append("-");
        }
        return separator.toString();
    }

    /**
     * Print current tweet.
     *
     * @param status           is info about tweet
     * @param jCommanderParsed is JC params
     *@return formatted tweet
     */
    public static String formatTweet(final Status status,
                                     final JCommanderParser jCommanderParsed) {

        JSONObject json = new JSONObject();
//        System.out.print("******************TWEET_DATA******************\n"
//                + status.toString()
//                + "\n**************TWEET_DATA**********************\n");

        StringBuilder statusLine = new StringBuilder();
        if (!jCommanderParsed.isStream()) {
            statusLine.append("[" + TimeResolver.getTime(status.getCreatedAt().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime(), LocalDateTime.now()) + "] ");
        }

        statusLine.append(
                highlightUserName(status.getUser().getScreenName()));
        if (!status.isRetweet()) {
            statusLine.append(
                    status.getText()
                            + " ("
                            + status.getRetweetCount()
                            + " "
                            + DeclensionResolver.getDeclensionForm(
                            DeclensionResolver.Word.RETWEET, status.getRetweetCount())
                            + ")"
            );
        } else {
            statusLine.append("ретвитнул ");
            String[] parsedText = status.getText().split(" ");

            statusLine.append(
                    highlightUserName(parsedText[1].split("@|:")[1]));

            for (int i = 2; i < parsedText.length; ++i) {
                statusLine.append(" " + parsedText[i]);
            }
        }
        statusLine.append(tweetsSeparator());
        return statusLine.toString();
    }
}
