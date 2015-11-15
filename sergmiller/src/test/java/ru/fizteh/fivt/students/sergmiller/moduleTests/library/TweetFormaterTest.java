//package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.twitterStream.TweetFormater;

/**
 * Created by sergmiller on 20.10.15.
 */

public class TweetFormaterTest extends TestCase {
    @Test
    public void testHighlightUserName() throws Exception {
        TweetFormater tweetFormater = new TweetFormater();
        assertEquals(tweetFormater.highlightUserName("UserName"), "@\u001B[34mUserName\u001B[0m: ");
    }

    @Test
    public void testTweetsSeparator() throws Exception {
        TweetFormater tweetFormater = new TweetFormater();
        assertEquals(tweetFormater.tweetsSeparator(), "\n----------------------------------------"
                + "----------------------------------------");
    }
//
//    @Before
//    public void preparationForTest() throws Exception {
//        tweetExample = Twitter4jTestUtils.tweetsFromJson("/DoctorWhoTweet.json").get(0);
//        //System.out.println(tweetExample.toString());
//        //tweetExample =  Twitter4jTestUtils.tweetsFromJson("/DoctorWhoTweet.json");
//    }
///*
//    @Test
//    public void testPrintTweetInStreamMode() throws Exception {
//        TweetPrinter tweetPrinter = new TweetPrinter();
//        JCommanderParser jCommanderParser = new JCommanderParser();
//        //tweetPrinter.printTweet(tweetExample);
//    }*/
}

