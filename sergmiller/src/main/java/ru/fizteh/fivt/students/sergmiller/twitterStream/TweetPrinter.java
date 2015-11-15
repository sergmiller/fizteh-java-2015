package ru.fizteh.fivt.students.sergmiller.twitterStream;

import java.io.PrintStream;
import java.util.List;

/**
 * Created by sergmiller on 06.10.15.
 */
public class TweetPrinter {
    private PrintStream out;

    public TweetPrinter(final PrintStream stream) {
        out = stream;
    }

    public void print(String tweet) {
        out.println(tweet);
    }

    public void printTweets(List<String> tweets) {
        if (!tweets.isEmpty()) {
            tweets.stream().forEach(this::print);
        }
    }
}
