package ru.mipt.diht.students.ale3otik.moduleTests.library;

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
import ru.fizteh.fivt.students.sergmiller.twitterStream.JCommanderParser;
import ru.fizteh.fivt.students.sergmiller.twitterStream.LocationData;
import ru.fizteh.fivt.students.sergmiller.twitterStream.TweetsGetterLimeted;
import ru.fizteh.fivt.students.sergmiller.twitterStream.TwitterStreamLauncher;
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
@PrepareForTest({Thread.class})
public class TwitterStreamTest extends TestCase{
    private LocationData LondonLocation = new LocationData(
            new GeoLocation(51.5073509, -0.1277583), 23.539304731202712, "London");
    private LocationData DolgoprudniyLocation = new LocationData(
            new GeoLocation(55.947064, 37.4992755), 6.117792942260596, "Dolgoprudnyy");
    private StatusAdapter statusAdapter;
    private JCommanderParser jCommanderParser;
    private JCommander jCommander;
    private TwitterStreamLauncher twitterStreamLauncher;
    List <String> results = new ArrayList<>();
    public static List <Status> doctorWhoTweets;
    private Consumer<String> dummyConsumer = (x)->mockedPrint(x);

    @Mock
    private Status dummyStatus;

    @Mock
    private TwitterStream dummyTwitterStream;

    @BeforeClass
    public static void loadStatuses() {
        doctorWhoTweets = Twitter4jTestUtils.tweetsFromJson("/DoctorWhoInLondonTweets.json");
    }

//    @Before
//    public void setUp() {
//        ArgumentCaptor<StatusListener> statusCaptor
//                = ArgumentCaptor.forClass(StatusListener.class);
//        doNothing().when(twitterStream).addListener((StatusListener)
//                statusCaptor.capture());
//        doAnswer(invocation -> {
//            aMoscowStatuses.forEach(s -> statusCaptor.getValue().onStatus(s));
//            return null;
//        }).when(twitterStream).filter(any(FilterQuery.class));
//        Mockito.when(dummyStatus.getGeoLocation()).thenReturn(null);
//        Mockito.when(dummyStatus.getText()).thenReturn("some message");
//
//        PowerMockito.mockStatic(Thread.class);
//
//        twitterStreamLauncher = new TwitterStreamLauncher(dummyTwitterStream, dummyConsumer);
//
//
//    }

//    private void createLauncherWithArguments(boolean isGeolocationNeeded,String... args) {
//        arguments = new Arguments();
//        jcm = new JCommander(arguments);
//        jcm.parse(args);
//        if(isGeolocationNeeded){
//            arguments.setGeoLocationInfo(londonGeoLocationInfo);
//        }
//
//    }

    public void mockedPrint(String string) {
        results.add(string);
    }

//    @Test
//    public void testPrintTwitterStream() throws Exception{
//        final String[] dummyArgs = {"-q", "doctorWho", "-p", "London", "-s"};
//        JCommanderParser doctorWhoQuery = new JCommanderParser();
//        JCommander jCommander = new JCommander(doctorWhoQuery, dummyArgs);
//        twitterStreamLauncher.printTwitterStream(doctorWhoQuery, LondonLocation);
//
//        //assertThat();
//    }

}
