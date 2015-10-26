package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.fizteh.fivt.students.sergmiller.twitterStream.GeoLocationResolver;
//import ru.fizteh.fivt.students.sergmiller.twitterStream.exceptions.GettingMyLocationException;

//import twitter4j.GeoLocation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;


/**
 * Created by sergmiller on 26.10.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class})
public class GeoLocationResolverTest extends TestCase {
    //private URL GeoIPUrl;
    private URL dummyURL;
    static final String URLAdress = "http://ipinfo.io/json";
    static final String DOLGOPRUDNYY = "Dolgoprudnyy";
    String resultLocationFromMyIPInDolgoprudnyy = "{"
            + "\"country\": \"RU\","
            + "\"loc\": \"55.9041,37.5606\","
            + "\"hostname\": \"No Hostname\","
            + "\"city\": \"Dolgoprudnyy\","
            + "\"org\": \"AS5467 Non state educational institution Educational Scientific and Experimental Center"
            + " of Moscow Institute of Physics and Technology\","
            + "\"ip\": \"93.175.2.82\","
            + "\"postal\": \"141700\","
            + "\"region\": \"Moscow Oblast\"}";

    @Before
    public void preparationForTest() throws Exception {
        dummyURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(URLAdress).thenReturn(dummyURL);
    }

    @Test
    public void testGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream =
                new ByteArrayInputStream(resultLocationFromMyIPInDolgoprudnyy.getBytes(StandardCharsets.UTF_8));
        PowerMockito.when(dummyURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        String location = geoLocationResolver.getNameOfCurrentLocation();
        assertEquals(DOLGOPRUDNYY, location);
    }
}
