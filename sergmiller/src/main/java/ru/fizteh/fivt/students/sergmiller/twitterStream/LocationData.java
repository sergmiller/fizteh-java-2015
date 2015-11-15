package ru.fizteh.fivt.students.sergmiller.twitterStream;

import twitter4j.GeoLocation;
/**
 * Created by sergmiller on 26.10.15.
 */
public class LocationData {
    private final GeoLocation geoLocation;
    private final Double radius;
    private final String name;
    public LocationData(final GeoLocation newGeoLocation, final Double newRadius, final String newName) {
        this.geoLocation = newGeoLocation;
        this.radius = newRadius;
        this.name = newName;
    }
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }
    public Double getRadius() {
        return radius;
    }
    public String getName() {
        return name;
    }
}
