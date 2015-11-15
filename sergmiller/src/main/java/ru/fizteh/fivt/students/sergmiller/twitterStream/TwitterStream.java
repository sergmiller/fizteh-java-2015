package ru.fizteh.fivt.students.sergmiller.twitterStream;

import com.beust.jcommander.ParameterException;
import org.json.JSONException;
import ru.fizteh.fivt.students.sergmiller.twitterStream.exceptions.GettingMyLocationException;
import twitter4j.*;
import twitter4j.StatusListener;
import com.beust.jcommander.JCommander;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.io.IOException;
import java.util.List;

/**
 * Created by sergmiller on 15.09.15.
 */
public final class TwitterStream {
    /**
     * Javadoc comment.
     */
    public TwitterStream() {
    }

    public static final int MILISECONDS_IN_SECONDS = 1000;

    public static final int MAX_QUANTITY_OF_TRIES = 2;


/*
    public static  Location getCurLocation(twitter4j.TwitterStream twStream,
     JCommanderParser jCommanderParser) {
        Place curPlace =
    }*/

    /**
     * Stream mod.
     *
     * @param jCommanderParser is class with query's info
     */
    public void printTwitterStream(
            final JCommanderParser jCommanderParser) {
        String curLocationRequest = "";
        LocationData locationData = new LocationData(null, null, null);
        try {
            GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
            if (!jCommanderParser.getLocation().equals("")) {
                if (jCommanderParser.getLocation().equals(NEARBY)) {
                    curLocationRequest = geoLocationResolver.getNameOfCurrentLocation();
                } else {
                    curLocationRequest = jCommanderParser.getLocation();
                }
                locationData = geoLocationResolver
                        .getGeoLocation(curLocationRequest);
            }
        } catch (IOException | JSONException | GettingMyLocationException e) {
            e.getMessage();
            System.err.println("Не могу определить регион=(\n" + "Поиск по World:");
            //  curLocationRequest = "World";
        }


        twitter4j.TwitterStream twStream = twitter4j
                .TwitterStreamFactory.getSingleton();

        //   Location curLocation = getCurLocation(twStream, jCommanderParser);
        //  Query query = new

        final double locationLatitude = locationData.getGeoLocation().getLatitude();
        final double locationLongitude = locationData.getGeoLocation().getLongitude();
        final double locationRadius = locationData.getRadius();
        StatusListener listener = new StatusAdapter() {
            @Override
            public void onStatus(final Status status) {
                if (jCommanderParser.isHideRetweets() && status.isRetweet()) {
                    return;
                }

                if (!jCommanderParser.getLocation().equals("")) {
                    final double curTweetLatitude;
                    final double curTweetLongitude;
                    if (status.getGeoLocation() != null) {
                        curTweetLatitude = status.getGeoLocation().getLatitude();
                        curTweetLongitude = status.getGeoLocation().getLongitude();
                    } else { /*
                        if (status.getUser().getLocation() != null) {
                            try {
                                GeoLocation curTweetLocation = GeoLocationResolver.getGeoLocation(
                                        status.getUser().getLocation()).getKey();
                                curTweetLatitude = curTweetLocation.getLatitude();
                                curTweetLongitude = curTweetLocation.getLongitude();
                            } catch (IOException | org.json.JSONException e) {
                                return;
                            }
                        } else {*/
                        return;
                        //}
                    }

                    if (GeoLocationResolver.getSphereDist(
                            locationLatitude,
                            locationLongitude,
                            curTweetLatitude,
                            curTweetLongitude) > locationRadius) {
                        return;
                    }
                }

                System.out.print(TweetFormater.formatTweet(status, jCommanderParser));

                try {
                    Thread.sleep(MILISECONDS_IN_SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        String[] queries = jCommanderParser
                .getQuery().toArray(
                        new String[jCommanderParser.getQuery().size()]);
        twStream.addListener(listener);
        if (jCommanderParser.getQuery().size() != 0) {
            twStream.filter(new FilterQuery().track(queries));
        } else {
            twStream.sample();
        }
    }

    static final String RADIUS_UNIT = "km";
    static final String NEARBY = "nearby";

    /**
     * Mod with print limited quantity of text.
     *
     * @param jCommanderParser is class with query's info
     * @throws TwitterException is kind of exception
     */
    public List<String> getTwitterLimited(
            final JCommanderParser jCommanderParser, final LocationData currentlocation, Twitter twitter) {
        int currentQuantityOfTries = 0;
        List<String> allTweets = new ArrayList<>();
        while (currentQuantityOfTries < MAX_QUANTITY_OF_TRIES) {
            try {
                String joinedQuery = "";
                if (!jCommanderParser.getQuery().isEmpty()) {
                    joinedQuery = String.join(" ", jCommanderParser.getQuery());
                }
                Query query = new Query(joinedQuery);

                GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
                if (currentlocation != null) {
                    query.geoCode(currentlocation.getGeoLocation(), currentlocation.getRadius(), RADIUS_UNIT);
//                        System.out.println("Location is " + curLocationRequest
//                                + ", latitude :"
//                                + locationData.getGeoLocation().getLatitude()
//                                + " longitude :"
//                                + locationData.getGeoLocation().getLongitude()
//                                + ", radius(km): "
//                                + locationData.getRadius()
//                                + TweetPrinter.tweetsSeparator());
                }


                query.setCount(jCommanderParser.getLimit());

                QueryResult request;
                int quantityOfPrintedTweets = 0;
                Boolean flagLimit = false;

                do {
                    request = twitter.search(query);

                    List<Status> tweets = request.getTweets();

//                    System.out.print("************tweets***************\n");
//                    for (int i = 0;i < tweets.size();++i) {
//                    System.out.println(tweets.get(i));
//                     }

//                  write("/Users/sergmiller/Documents/javaproj/JavaGit/fizteh-java-2015/sergmiller"
//                            + "/DoctorWhoInLondonTweets.json", new JSONObject(request).toString());

                    for (Status status : tweets) {
                        if (!jCommanderParser.isHideRetweets()
                                || !status.isRetweet()) {
                            allTweets.add(TweetFormater.formatTweet(status, jCommanderParser));
                            ++quantityOfPrintedTweets;
                            if (quantityOfPrintedTweets == jCommanderParser.getLimit()) {
                                flagLimit = true;
                                break;
                            }
                        }
                    }
                    query = request.nextQuery();
                } while (query != null && !flagLimit);

                if (allTweets.isEmpty()) {
                    System.err.println("\nПо запросу "
                                    + String.join(", "
                                    + jCommanderParser.getQuery())
                                    + " для "
                                    + currentlocation.getName()
                                    + " ничего не найдено=(\n\n"
                                    + "Рекомендации:\n\n"
                                    + "Убедитесь, что все слова"
                                    + " написаны без ошибок.\n"
                                    + "Попробуйте использовать "
                                    + "другие ключевые слова.\n"
                                    + "Попробуйте использовать "
                                    + "более популярные ключевые слова."
                    );
                } else {
                    Collections.reverse(allTweets);
                }

                currentQuantityOfTries = MAX_QUANTITY_OF_TRIES;
            } catch (TwitterException twExp) {
                ++currentQuantityOfTries;
                if (currentQuantityOfTries == MAX_QUANTITY_OF_TRIES) {
                    System.err.println(twExp.getMessage()
                            + "\nЧто-то пошло не так=(\n"
                            + "Проверьте наличие соединения.");
                }
            }
        }
        return allTweets;
    }

    /**
     * Print general info about TwitterStream.
     *
     * @param jCommanderSettings is JC params
     */
    public static void printHelpMan(final JCommander jCommanderSettings) {
        jCommanderSettings.usage();
    }

    static void exitWithCtrlD() {
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

    /**
     * Main function of TwitterStream.
     *
     * @param args is input parameters
     * @throws TwitterException some exception
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
            printHelpMan(jCommanderHelper);
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
        LocationData currentLocation = geoLocationResolver.resolveLocation(jCommanderParser.getLocation());
        if (jCommanderParser.getLocation() != "" && currentLocation == null) {
            System.err.println("Не могу определить регион=(\n" + "Поиск по World:");
        }
        TwitterStream twitterStream = new TwitterStream();

        if (jCommanderParser.isStream()) {
            twitterStream.printTwitterStream(jCommanderParser);
            exitWithCtrlD();
        } else {
            allTweets = twitterStream.getTwitterLimited(jCommanderParser,
                    currentLocation,
                    TwitterFactory.getSingleton());
            TweetPrinter tweetPrinter = new TweetPrinter(System.out);
            tweetPrinter.printTweets(allTweets);
        }


        //System.out.println(LocalTime.now() + " " + LocalTime.now().toSecondOfDay());
    }

    public static void write(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if (!file.exists()) {
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст у файл
                out.print(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch (IOException e) {
            e.getStackTrace();
            //throw new RuntimeException(e);
        }
    }
}




