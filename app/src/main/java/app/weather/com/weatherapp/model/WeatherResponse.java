package app.weather.com.weatherapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WeatherResponse implements Serializable {

    @SerializedName("weather")
    List<WeatherItem> weather;

    @SerializedName("main")
    MainTemperature main;

    @SerializedName("wind")
    Wind wind;

    //The remaining response is not required hence not reading them

    public Wind getWind() {
        return wind;
    }

    public List<WeatherItem> getWeather() {
        return weather;
    }

    public MainTemperature getMain() {
        return main;
    }
}
