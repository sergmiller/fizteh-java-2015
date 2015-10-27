//package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.fizteh.fivt.students.sergmiller.twitterStream.GeoLocationResolver;
import ru.fizteh.fivt.students.sergmiller.twitterStream.LocationData;
//import twitter4j.GeoLocation;
import twitter4j.TwitterStream;
//import static ru.fizteh.fivt.students.sergmiller.twitterStream.GeoLocationResolver.getNameOfCurrentLocation;

//import twitter4j.GeoLocation;

//import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
//import java.nio.charset.StandardCharsets;


/**
 * Created by sergmiller on 26.10.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, GeoLocationResolver.class})
public class GeoLocationResolverTest extends TestCase {
    private URL dummyGeoDataURL;
    private URL dummyMyLocationURL;
    static final String URLIPinfoAdress = "http://ipinfo.io/json";
    //static final String URLGoogleAPIAdress = "http://maps.googleapis.com/maps/api/geocode/json";
    static final String DOLGOPRUDNYY = "Dolgoprudnyy";
    static final String LONDON = "London";
    static final Double LondonLatitude = 51.5073509;
    static final Double LondonLongitude = -0.1277583;
    static final Double LondonRadius = 23.539304731202712;

    @Before
    public void preparationForTest() throws Exception {
        dummyGeoDataURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments("http://maps.googleapis.com/maps/api/geocode/json?address=" + LONDON +"&sensor=false").thenReturn(dummyGeoDataURL);
        dummyMyLocationURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(URLIPinfoAdress).thenReturn(dummyMyLocationURL);
    }

    @Test
    public void testGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream =
                TwitterStream.class.getResourceAsStream("/DolgoprudnyyIPinfo.json");
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

        assertEquals(LondonLatitude, location.getGeoLocation().getLatitude());
        assertEquals(LondonLongitude, location.getGeoLocation().getLongitude());
        assertEquals(LondonRadius, location.getRadius());
    }
}
