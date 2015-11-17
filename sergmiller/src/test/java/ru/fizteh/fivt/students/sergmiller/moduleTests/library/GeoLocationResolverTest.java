package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import junit.framework.TestCase;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.fizteh.fivt.students.sergmiller.twitterStream.GeoLocationResolver;
import ru.fizteh.fivt.students.sergmiller.twitterStream.LocationData;
import ru.fizteh.fivt.students.sergmiller.twitterStream.exceptions.GettingMyLocationException;
import twitter4j.GeoLocation;
import twitter4j.TwitterStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by sergmiller on 26.10.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, GeoLocationResolver.class})
public class GeoLocationResolverTest extends TestCase {
    private URL dummyGeoDataURL;
    private URL dummyMyLocationURL;
    private final String URLIPinfoAdress = "http://ipinfo.io/json";
    private final String URLGoogleAPIAdress = "http://maps.googleapis.com/maps/api/geocode/json?address=";
    private final String DOLGOPRUDNYY = "Dolgoprudnyy";
    private final String LONDON = "London";
    private final String GALLIFREY = "Gallifrey";

    @Before
    public void setUp() throws Exception {
        dummyGeoDataURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(URLGoogleAPIAdress
                + LONDON
                + "&sensor=false").thenReturn(dummyGeoDataURL);

        PowerMockito.whenNew(URL.class).withArguments(URLGoogleAPIAdress
                + GALLIFREY
                + "&sensor=false").thenReturn(dummyGeoDataURL);

        dummyMyLocationURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(URLIPinfoAdress).thenReturn(dummyMyLocationURL);
    }


    @Test
    public void testResolveEmptyLocation() throws Exception {
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();

        assertEquals(null, geoLocationResolver.resolveLocation(""));
    }

    @Test
    public void testGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream = TwitterStream.class.getResourceAsStream("/DolgoprudnyyIPinfo.json");
        PowerMockito.when(dummyMyLocationURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        String location = geoLocationResolver.getNameOfCurrentLocation();

        assertEquals(DOLGOPRUDNYY, location);
    }

    @Test
    public void testGetGeoLocation() throws Exception {
        InputStream inputStream =
                TwitterStream.class.getResourceAsStream("/LondonGoogleAPIData.json");
        PowerMockito.when(dummyGeoDataURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        LocationData location = geoLocationResolver.resolveLocation(LONDON);

        assertEquals(51.5073509, location.getGeoLocation().getLatitude());
        assertEquals(-0.1277583, location.getGeoLocation().getLongitude());
        assertEquals( 23.539304731202712, location.getRadius());
        assertEquals("London", location.getName());
    }

    @Test(expected = JSONException.class)
    public void testGetGeoLocationFailed() throws Exception {
        InputStream inputStream =
                TwitterStream.class.getResourceAsStream("/Gallifrey.json");
        PowerMockito.when(dummyGeoDataURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        geoLocationResolver.getGeoLocation(GALLIFREY);
    }

    @Test(expected = GettingMyLocationException.class)
    public void testGetNameOfCurrentLocationFailed() throws Exception {
        InputStream inputStream =
                TwitterStream.class.getResourceAsStream("/Gallifrey.json");
        PowerMockito.when(dummyMyLocationURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        geoLocationResolver.getNameOfCurrentLocation();
    }

    @Test
    public void testResolveLocationFailed() throws Exception {
        InputStream inputStream = TwitterStream.class.getResourceAsStream("/Gallifrey.json");
        PowerMockito.when(dummyMyLocationURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        LocationData location = geoLocationResolver.resolveLocation("Gallifrey");

        assertEquals(null, location);
    }

}
