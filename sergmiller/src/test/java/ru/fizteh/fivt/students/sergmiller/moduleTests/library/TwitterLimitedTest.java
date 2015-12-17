package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import com.beust.jcommander.JCommander;

import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Assert.*;

import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ru.fizteh.fivt.students.sergmiller.twitterStream.*;
import twitter4j.Status;
import twitter4j.*;
import twitter4j.Twitter4jTestUtils;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by sergmiller on 03.11.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({TimeResolver.class, TwitterStreamRunner.class})
//@RunWith(MockitoJUnitRunner.class)
public class TwitterLimitedTest extends TestCase{
    private LocationData LondonLocation = new LocationData(
            new GeoLocation(51.5073509, -0.1277583), 23.539304731202712, "London");
    private LocationData DolgoprudnyyLocation = new LocationData(
            new GeoLocation(55.947064, 37.4992755), 6.117792942260596, "Dolgoprudnyy");
    private static List<Status> doctorWhoTweets;


    //@Mock
    //private QueryResult queryResultDoctorWho;

    @Mock
    Twitter twitter;

//    @Mock
//    GeoLocationResolver geoLocationResolver;

    @Mock
    twitter4j.TwitterStream twitterStream;

//    @Mock
//    LocalDateTime localDateTime;

    @BeforeClass
    public static void loadStatuses() {
        doctorWhoTweets = Twitter4jTestUtils.tweetsFromJson("/DoctorWhoInLondonTweets.json");
    }

    @Before
    public void setUp() throws Exception {
//        when(geoLocationResolver.getGeoLocation("London"))
//               .thenReturn(LondonLocation);
//
        QueryResult queryResultDoctorWho = mock(QueryResult.class);
        when(queryResultDoctorWho.getTweets()).thenReturn(doctorWhoTweets);

        QueryResult emptyQueryResult = mock(QueryResult.class);
        when(emptyQueryResult.getTweets()).thenReturn(new ArrayList<Status>());

        when(twitter.search(argThat(hasProperty("query", equalTo("doctorWho")))))
                .thenReturn(queryResultDoctorWho);

        when(twitter.search(argThat(hasProperty("query", equalTo("empty")))))
                .thenReturn(emptyQueryResult);

        PowerMockito.mockStatic(TimeResolver.class);
        PowerMockito.when(TimeResolver.getTime(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn("Только что");

        PowerMockito.mockStatic(TwitterStreamRunner.class);
    }

    @Test
    public void testPrintTwitterLimitedAllTweets() throws Exception {
        final String[] dummyArgs = {"-q", "doctorWho", "-p", "London"};
        JCommanderParser doctorWhoQuery = new JCommanderParser();
        JCommander jCommander = new JCommander(doctorWhoQuery, dummyArgs);
        doctorWhoQuery.setGeoLocation(LondonLocation);
        TweetsGetterLimeted tweetsGetterLimeted = new TweetsGetterLimeted();
        List<String> currentTweets = tweetsGetterLimeted.getTwitterLimited(
                        doctorWhoQuery, twitter);

        assertEquals(5, currentTweets.size());

//        for (int i = 0;i < 5;++i) {
//            System.out.println(currentTweets.get(i));
//        }
        assertThat(currentTweets, hasItem("[Только что] @\u001B[34mJay_Cree\u001B[0m: #Doctorwho #thedoctor"
                + " #london #excellondon #doctorwhofestival #drwhofestival #drwho"
                + " #british #english… https://t.co/gWjxU63YTm (0 ретвитов)\n"
                + "--------------------------------------------------------------------------------"));

        assertThat(currentTweets, hasItem("[Только что] @\u001B[34mDoctorWhoVic\u001B[0m:"
                + " ретвитнул @\u001B[34mAnnCarters\u001B[0m:"
                + "  Early designs for the #robotofsherwood robots."
                + " #dwfestuk #doctorwho #MFX @ ExCeL London https://t.co/YRJ5Dqlazj\n"
                + "--------------------------------------------------------------------------------"));
    }

    @Test
    public void testPrintTwitterLimitedTweetsWithoutRetweets() throws Exception {
        final String[] dummyArgs = {"-q", "doctorWho", "-p", "London", "--hideRetweets"};
        JCommanderParser doctorWhoQuery = new JCommanderParser();
        JCommander jCommander = new JCommander(doctorWhoQuery, dummyArgs);
        doctorWhoQuery.setGeoLocation(LondonLocation);
        TweetsGetterLimeted tweetsGetterLimeted = new TweetsGetterLimeted();
        List<String> currentTweets = tweetsGetterLimeted.getTwitterLimited(
                doctorWhoQuery, twitter);

        assertEquals(3, currentTweets.size());

//        for (int i = 0;i < 3;++i) {
//            System.out.println(currentTweets.get(i));
//        }

        assertThat(currentTweets, hasItem("[Только что] @\u001B[34malexryans\u001B[0m:"
                + " Final day in London at the #DoctorWho Festival"
                + " - looking forward to seeing all the cast and writers!… "
                + "https://t.co/BExJ7mYvGd (0 ретвитов)\n"
                + "--------------------------------------------------------------------------------"));

        assertThat(currentTweets, not(hasItem("[Только что] @\u001B[34mDoctorWhoVic\u001B[0m:"
                + " ретвитнул @\u001B[34mAnnCarters\u001B[0m:"
                + "  Early designs for the #robotofsherwood robots."
                + " #dwfestuk #doctorwho #MFX @ ExCeL London https://t.co/YRJ5Dqlazj\n"
                + "--------------------------------------------------------------------------------")));
    }

    @Test
    public void testPrintTwitterLimitedEmptyAnswerEmptyLocation() throws Exception {
        final String[] dummyArgs = {"-q", "empty"};
        JCommanderParser emptyQuery = new JCommanderParser();
        JCommander jCommander = new JCommander(emptyQuery, dummyArgs);
        emptyQuery.setGeoLocation(null);
        TweetsGetterLimeted tweetsGetterLimeted = new TweetsGetterLimeted();
        List<String> currentTweets = tweetsGetterLimeted.getTwitterLimited(
                emptyQuery, twitter);

        assertEquals(1, currentTweets.size());

        assertThat(currentTweets, hasItem("\nПо запросу empty ничего не найдено=(\n\n"
                + "Рекомендации:\n\n"
                + "Убедитесь, что все слова написаны без ошибок.\n"
                + "Попробуйте использовать другие ключевые слова.\n"
                + "Попробуйте использовать более популярные ключевые слова."));
    }

    @Test
    public void testPrintTwitterLimitedEmptyAnswerWithFailedLocation() throws Exception {
        final String[] dummyArgs = {"-q", "empty", "-p", "Gallifrey"};
        JCommanderParser emptyQuery = new JCommanderParser();
        JCommander jCommander = new JCommander(emptyQuery, dummyArgs);
        emptyQuery.setGeoLocation(null);
        TweetsGetterLimeted tweetsGetterLimeted = new TweetsGetterLimeted();
        List<String> currentTweets = tweetsGetterLimeted.getTwitterLimited(
                emptyQuery, twitter);

        assertEquals(1, currentTweets.size());

        PowerMockito.verifyStatic();
        assertThat(currentTweets,hasItem("\nПо запросу empty для World ничего не найдено=(\n\n"
                + "Рекомендации:\n\n"
                + "Убедитесь, что все слова написаны без ошибок.\n"
                + "Попробуйте использовать другие ключевые слова.\n"
                + "Попробуйте использовать более популярные ключевые слова."));
    }

    @Test
    public void testPrintTwitterLimitedEmptyAnswerWithLocation() throws Exception {
        final String[] dummyArgs = {"-q", "empty", "-p", "Dolgoprudnyy"};
        JCommanderParser emptyQuery = new JCommanderParser();
        JCommander jCommander = new JCommander(emptyQuery, dummyArgs);
        emptyQuery.setGeoLocation(DolgoprudnyyLocation);
        TweetsGetterLimeted tweetsGetterLimeted = new TweetsGetterLimeted();
        List<String> currentTweets = tweetsGetterLimeted.getTwitterLimited(
                emptyQuery, twitter);

        assertEquals(1, currentTweets.size());

        PowerMockito.verifyStatic();
        assertThat(currentTweets,hasItem("\nПо запросу empty для Dolgoprudnyy ничего не найдено=(\n\n"
                + "Рекомендации:\n\n"
                + "Убедитесь, что все слова написаны без ошибок.\n"
                + "Попробуйте использовать другие ключевые слова.\n"
                + "Попробуйте использовать более популярные ключевые слова."));
    }
}
