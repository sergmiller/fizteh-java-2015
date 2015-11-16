//package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

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
import twitter4j.GeoLocation;
import twitter4j.TwitterStream;

import java.io.InputStream;


import java.net.URL;
import java.time.LocalDate;

/**
 * Created by sergmiller on 26.10.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, GeoLocationResolver.class})
public class GeoLocationResolverTest extends TestCase {
    private URL dummyGeoDataURL;
    private URL dummyMyLocationURL;
    private LocationData LondonLocation = new LocationData(
            new GeoLocation(51.5073509, -0.1277583), 23.539304731202712, "London");
    private LocationData DolgoprudniyLocation = new LocationData(
            new GeoLocation(55.947064, 37.4992755), 6.117792942260596, "Dolgoprudnyy");
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
    public void testlocationResolver() throws Exception {
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        assertEquals(null, geoLocationResolver.resolveLocation(""));
    }

    @Test
    public void testGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream = TwitterStream.class.getResourceAsStream("/DolgoprudnyyIPinfo.json");
        //InputStream inputStream = new ByteArrayInputStream((org.apache.commons.io.IOUtils.toString(TwitterStream.class.getResourceAsStream("/DolgoprudnyyIPinfo.json"))).getBytes(StandardCharsets.UTF_8));
        //DolgoprudnyyIPResponce = org.apache.commons.io.IOUtils.toString(inputStream);
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
        LocationData location = geoLocationResolver.getGeoLocation(LONDON);

        assertEquals(51.5073509, location.getGeoLocation().getLatitude());
        assertEquals(-0.1277583, location.getGeoLocation().getLongitude());
        assertEquals( 23.539304731202712, location.getRadius());
    }

    @Test(expected = JSONException.class)
    public void testGetGeoLocationFailed() throws Exception {
        InputStream inputStream =
                TwitterStream.class.getResourceAsStream("/GallifreyGoogleAPIData.json");
        PowerMockito.when(dummyGeoDataURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver geoLocationResolver = new GeoLocationResolver();
        LocationData location = geoLocationResolver.getGeoLocation(GALLIFREY);
    }
}
