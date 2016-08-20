package app.weather.com.weatherapp.model;

import java.io.Serializable;

public class WeatherItem implements Serializable {
    String id;
    String main;
    String description;
    String icon;

    public String getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
