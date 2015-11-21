package ru.fizteh.fivt.students.sergmiller.twitterStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by sergmiller on 16.11.15.
 */
public class TwitterStreamRunner {
    /**
     * Main function of TwitterStream.
     *
     * @param args is input parameters
     */
    public static void main(final String[] args) {
        JCommanderParser jCommanderParser = new JCommanderParser();

        try {
            JCommander jCommanderSettings = new JCommander(jCommanderParser, args);
            if (jCommanderParser.isHelp()
                    || (!jCommanderParser.isStream()
                    && jCommanderParser.getQuery().size() == 0)) {
                throw new ParameterException("");
            }
        } catch (ParameterException | ClassCastException e) {
            JCommanderParser jCommanderDefault = new JCommanderParser();
            JCommander jCommanderHelper = new JCommander(jCommanderDefault, new String[]{""});
            jCommanderHelper.setProgramName("TwitterStream");
            List<String> report = new ArrayList<String>();
            report.add(printHelpMan(jCommanderHelper));
            TweetPrinter tweetPrinter = new TweetPrinter(System.out);
            tweetPrinter.printTweets(report);
            return;
        }

        String printLocation = "World";
        if (jCommanderParser.getLocation() != "") {
            printLocation = jCommanderParser.getLocation();
        }

        String printedQuery = String.join(", ", jCommanderParser.getQuery());
        if (printedQuery.equals("")) {
            printedQuery = "-";
        }
        System.out.println("Твиты по запросу "
                        + printedQuery
                        + " для " + printLocation
                        + TweetFormater.tweetsSeparator()
        );

        final List<String> allTweets;
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        jCommanderParser.setGeoLocation(geoLocationResolver
                .resolveLocation(jCommanderParser.getLocation()));
        if (jCommanderParser.getLocation() != "" && jCommanderParser.getGeoLocation() == null) {
            System.err.println("Не могу определить регион=(\n" + "Поиск по World:");
        }

        if (jCommanderParser.isStream()) {
            TwitterStreamLauncher twitterStreamLauncher = new TwitterStreamLauncher(
                    TwitterStreamFactory.getSingleton(),
                    TwitterStreamLauncher.getOutConsumer(),
                    jCommanderParser
            );

            twitterStreamLauncher.getTwitterStream();
            exitWithCtrlD();
        } else {
            TweetsGetterLimeted tweetsGetterLimeted = new TweetsGetterLimeted();
            allTweets = tweetsGetterLimeted.getTwitterLimited(jCommanderParser,
                    TwitterFactory.getSingleton());
            TweetPrinter tweetPrinter = new TweetPrinter(System.out);
            tweetPrinter.printTweets(allTweets);
        }
    }

    /**
     * Print general info about TwitterStream.
     *
     * @param jCommanderSettings is JC params
     */
    public static String printHelpMan(final JCommander jCommanderSettings) {
        StringBuilder stringBuilder = new StringBuilder();
        jCommanderSettings.usage(stringBuilder);
        return stringBuilder.toString();
    }

    private static void exitWithCtrlD() {
        Scanner scan = new Scanner(System.in);
        try {
            while (scan.hasNext()) {
                int someNeverUsedStatement = 0;
            }
        } finally {
            scan.close();
            System.exit(0);
        }
    }

    public static void printIntoStdout(final String string) {
        System.out.println(string);
    }
}
