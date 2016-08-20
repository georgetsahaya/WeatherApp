package app.weather.com.weatherapp.model;

import java.io.Serializable;

public class Wind implements Serializable {
    String speed;
    String deg;

    public String getSpeed() {
        return speed;
    }

    public String getDeg() {
        return deg;
    }
}
