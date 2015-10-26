package ru.fizteh.fivt.students.sergmiller.twitterStream;

import twitter4j.GeoLocation;
/**
 * Created by sergmiller on 26.10.15.
 */
final class LocationData {
    private final GeoLocation geoLocation;
    private final Double radius;
    LocationData(final GeoLocation newGeoLocation, final Double newRadius) {
        this.geoLocation = newGeoLocation;
        this.radius = newRadius;
    }
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }
    public Double getRadius() {
        return radius;
    }

}
