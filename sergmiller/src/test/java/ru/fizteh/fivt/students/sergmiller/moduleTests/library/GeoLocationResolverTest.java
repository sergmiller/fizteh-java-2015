//package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.fizteh.fivt.students.sergmiller.twitterStream.GeoLocationResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by sergmiller on 26.10.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, GeoLocationResolver.class})
public class GeoLocationResolverTest extends TestCase {
    //private URL GeoIPUrl;
    private URL dummyURL;
    private final String URLAdress = "http://ipinfo.io/json";
    private final String DOLGOPRUDNYY = "Dolgoprudnyy";
    private final String resultLocationFromMyIPInDolgoprudnyy = "{"
            + "\"country\":\"RU\","
            + "\"loc\":\"55.9041,37.5606\","
            + "\"hostname\":\"No Hostname\","
            + "\"city\":\"Dolgoprudnyy\","
            + "\"org\":\"AS5467 Non state educational institution Educational Scientific and Experimental Center"
            + " of Moscow Institute of Physics and Technology\","
            + "\"ip\":\"93.175.2.82\","
            + "\"postal\":\"141700\","
            + "\"region\":\"Moscow Oblast\"}";

    @Before
    public void preparationForTest() throws Exception {
        //GeoIPUrl = PowerMockito.mock(URL.class);
        //PowerMockito.whenNew(URL.class).withArguments("http://maps.googleapis.com/maps/api/geocode/json?address="
        // + MOSCOW +"&sensor=false").thenReturn(GeoIPUrl);

        dummyURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(URLAdress).thenReturn(dummyURL);
    }

    @Test
    public void testGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream =
                new ByteArrayInputStream(resultLocationFromMyIPInDolgoprudnyy.getBytes(StandardCharsets.UTF_8));
        PowerMockito.when(dummyURL.openStream()).thenReturn(inputStream);
        String location = new GeoLocationResolver().getNameOfCurrentLocation();
        assertEquals(location, DOLGOPRUDNYY);
    }
}
