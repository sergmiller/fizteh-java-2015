//package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.twitterStream.TweetPrinter;

/**
 * Created by sergmiller on 20.10.15.
 */
public class TweetPrinterTest extends TestCase{
    @Test
    public void testHighlightUserName() throws Exception {
        TweetPrinter tweetPrinter = new TweetPrinter();
        assertEquals(tweetPrinter.highlightUserName("UserName"), "@\u001B[34mUserName\u001B[0m: ");
    }
    @Test
    public void testTweetsSeparator() throws Exception {
        TweetPrinter tweetPrinter = new TweetPrinter();
        assertEquals(tweetPrinter.tweetsSeparator(), "\n----------------------------------------"
        + "----------------------------------------");
    }
}

