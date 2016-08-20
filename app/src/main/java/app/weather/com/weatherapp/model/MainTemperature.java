package app.weather.com.weatherapp.model;

import java.io.Serializable;

public class MainTemperature implements Serializable {
    String temp;
    String pressure;
    String humidity;
    String temp_min;
    String temp_max;

    public String getTemp() {
        return temp;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }
}
