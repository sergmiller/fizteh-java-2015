package ru.fizteh.fivt.students.sergmiller.twitterStream;

import twitter4j.*;
import twitter4j.StatusListener;

import java.util.function.Consumer;

/**
 * Created by sergmiller on 15.09.15.
 */
public final class TwitterStreamLauncher {
    /**
     * Javadoc comment.
     */

    public static final int MILISECONDS_IN_SECONDS = 1000;


    private Consumer<String> consumer;
    private twitter4j.TwitterStream twitterStream;
    private JCommanderParser jCommanderParser;
    private LocationData currentLocation;

    public TwitterStreamLauncher(final twitter4j.TwitterStream newTwitterStream,
                                 final Consumer<String> newConsumer,
                                 final JCommanderParser newJCommanderParser,
                                 final LocationData newLocation) {
        twitterStream = newTwitterStream;
        consumer = newConsumer;
        jCommanderParser = newJCommanderParser;
        currentLocation = newLocation;
    }

    /**
     * Stream mod.
     */
    public void printTwitterStream() {
        StatusListener listener = getListener();
        String[] queries = jCommanderParser
                .getQuery().toArray(
                        new String[jCommanderParser.getQuery().size()]);
        twitterStream.addListener(listener);
        if (jCommanderParser.getQuery().size() != 0) {
            twitterStream.filter(new FilterQuery().track(queries));
        } else {
            twitterStream.sample();
        }
    }

    public  StatusAdapter getListener() {
        return new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                if (jCommanderParser.isHideRetweets() && status.isRetweet()) {
                    return;
                }

                if (!jCommanderParser.getLocation().equals("")) {
                    final double curTweetLatitude;
                    final double curTweetLongitude;
                    if (status.getGeoLocation() != null) {
                        curTweetLatitude = status.getGeoLocation().getLatitude();
                        curTweetLongitude = status.getGeoLocation().getLongitude();
                    } else {
                        return;
                    }

                    if (GeoLocationResolver.getSphereDist(
                            currentLocation.getGeoLocation().getLatitude(),
                            currentLocation.getGeoLocation().getLongitude(),
                            curTweetLatitude,
                            curTweetLongitude) > currentLocation.getRadius()) {
                        return;
                    }
                }

                print(TweetFormater.formatTweet(status, jCommanderParser));

                //System.out.print(TweetFormater.formatTweet(status, jCommanderParser));

                try {
                    Thread.sleep(MILISECONDS_IN_SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        };
    }

    public void print(String str) {
        consumer.accept(str);
    }

    public static Consumer<String> getOutConsumer() {
        return (x) -> printIntoStdout(x);
    }

    public static void printIntoStdout(final String string) {
        System.out.println(string);
    }
}




