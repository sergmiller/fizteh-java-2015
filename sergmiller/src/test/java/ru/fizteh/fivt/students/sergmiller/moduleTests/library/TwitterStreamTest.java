import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.fizteh.fivt.students.sergmiller.twitterStream.*;
import sun.text.resources.cs.JavaTimeSupplementary_cs;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * Created by sergmiller on 15.11.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Thread.class, TweetFormater.class})
public class TwitterStreamTest extends TestCase{
    private LocationData LondonLocation = new LocationData(
            new GeoLocation(51.5073509, -0.1277583), 23.539304731202712, "London");
    private LocationData DolgoprudnyyLocation = new LocationData(
            new GeoLocation(55.947064, 37.4992755), 6.117792942260596, "Dolgoprudnyy");
    private StatusAdapter statusAdapter;
    private JCommanderParser jCommanderParser;
    private JCommander jCommander;
    private TwitterStreamLauncher twitterStreamLauncher;
    List <String> results = new ArrayList<>();
    public static List <Status> doctorWhoTweets;
    //private Consumer<String> dummyConsumer = (x)->mockedPrint(x);

    @Mock
    private Status dummyStatus;

    @Mock
    private TwitterStream dummyTwitterStream;

    @Mock
    private Consumer<String> dummyConsumer;

    @Before
    public void setUp() {
        when(dummyStatus.getGeoLocation()).thenReturn(null);
        when(dummyStatus.getText()).thenReturn("message");

        PowerMockito.mockStatic(Thread.class);

        PowerMockito.mockStatic(TweetFormater.class);

    }

    private void initWithArgs(boolean withGeolocation,String... args) {
        jCommanderParser = new JCommanderParser();
        jCommander = new JCommander(jCommanderParser, args);
        if(withGeolocation){
            jCommanderParser.setGeoLocation(LondonLocation);
        }
        twitterStreamLauncher = new TwitterStreamLauncher(dummyTwitterStream, dummyConsumer, jCommanderParser);
    }

    private void mockTweetFormater(){
        String text = dummyStatus.getText();
        PowerMockito.when(TweetFormater
                .formatTweet(dummyStatus, jCommanderParser))
                .thenReturn(text);
    }

    @Test
    public void testStatusListenerNotRetweetNullRequestLocation() throws Exception {
        when(dummyStatus.isRetweet()).thenReturn(false);

        initWithArgs(false, "-q", "query", "-s", "--hideRetweets");
        statusAdapter = twitterStreamLauncher.getListener();
        mockTweetFormater();

        statusAdapter.onStatus(dummyStatus);
        verify(dummyConsumer).accept(dummyStatus.getText());

        initWithArgs(false, "-q", "query", "-s");
        statusAdapter = twitterStreamLauncher.getListener();
        mockTweetFormater();

        statusAdapter.onStatus(dummyStatus);
        verify(dummyConsumer, times(2)).accept(dummyStatus.getText());
    }

    @Test
    public void testStatusListenerRetweetNullRequestLocation() throws Exception {
        when(dummyStatus.isRetweet()).thenReturn(true);

        initWithArgs(false,"-q", "query", "-s");
        statusAdapter = twitterStreamLauncher.getListener();
        mockTweetFormater();

        statusAdapter.onStatus(dummyStatus);
        verify(dummyConsumer, times(1)).accept(dummyStatus.getText());

        initWithArgs(false, "-q", "query", "-s", "--hideRetweets");
        statusAdapter = twitterStreamLauncher.getListener();
        mockTweetFormater();

        statusAdapter.onStatus(dummyStatus);
        verify(dummyConsumer, times(1)).accept(anyString());
    }

    @Test
    public void testStatusListenerNullStatusLocation() throws Exception {
        when(dummyStatus.isRetweet()).thenReturn(false);
        when(dummyStatus.getGeoLocation()).thenReturn(null);

        initWithArgs(true, "-q", "query", "-s");
        statusAdapter = twitterStreamLauncher.getListener();
        mockTweetFormater();

        statusAdapter.onStatus(dummyStatus);
        verify(dummyConsumer, times(0)).accept(anyString());
    }

    @Test
    public void testStatusListenerDifferentLocations() throws Exception {
        when(dummyStatus.isRetweet()).thenReturn(false);
        when(dummyStatus.getGeoLocation()).thenReturn(DolgoprudnyyLocation.getGeoLocation());

        initWithArgs(true, "-q", "some query", "-s");
        statusAdapter = twitterStreamLauncher.getListener();
        mockTweetFormater();

        statusAdapter.onStatus(dummyStatus);
        verifyZeroInteractions(dummyConsumer);
    }

    @Test
    public void testStatusListenerSuccessLocation() throws Exception {
        when(dummyStatus.isRetweet()).thenReturn(false);
        when(dummyStatus.getGeoLocation()).thenReturn(LondonLocation.getGeoLocation());

        initWithArgs(true, "-q", "some query", "-s");
        statusAdapter = twitterStreamLauncher.getListener();
        mockTweetFormater();

        statusAdapter.onStatus(dummyStatus);
        verify(dummyConsumer, times(1)).accept(anyString());
    }

    @Test
    public void testTwitterStreamWork() throws Exception {
        initWithArgs(false, "-q", "some query", "-s");
        twitterStreamLauncher.getTwitterStream();
        verify(dummyTwitterStream).filter(any(FilterQuery.class));

        initWithArgs(false, "-s");

        twitterStreamLauncher.getTwitterStream();
        verify(dummyTwitterStream).sample();

        initWithArgs(false,"-s");

       // PowerMockito.doThrow(new InterruptedException()).when(Thread.class).sleep(anyInt());
       // PowerMockito.when(new Thread).sleep(1000).thenThrow(new InterruptedException());
        // twitterStreamLauncher.getTwitterStream();
    }
}
