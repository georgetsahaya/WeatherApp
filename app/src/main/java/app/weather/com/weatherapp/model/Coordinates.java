package app.weather.com.weatherapp.model;

import java.io.Serializable;

public class Coordinates implements Serializable {
    String lon;
    String lat;

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
