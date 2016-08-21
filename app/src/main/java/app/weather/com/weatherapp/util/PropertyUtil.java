package app.weather.com.weatherapp.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtil {
    /*
     * Number of city names read from the config file
     */
    private static final int cityLimit = 100;

    /*
     * configuration file where the city names are provided
     */
    private static final String CITY_CONFIG_FILE = "cities.properties";

    /*
     * The prefix of the city names. It goes like city1=Sydney, etc.
     */
    private static final String CITY_NAME_PREFIX = "city";

    public static List<String> getCityList(Context context) {
        List<String> toRet = new ArrayList<>();
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;

        try {
            inputStream = assetManager.open(CITY_CONFIG_FILE);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            return toRet;
        }

        //Reading the city names from the config file
        for (int i = 1;i < cityLimit;i++) {
            String cityName = (String) properties.get(CITY_NAME_PREFIX + String.valueOf(i));
            if (cityName != null) {
                toRet.add(cityName);
            } else {
                break;
            }
        }

        return toRet;
    }
}
