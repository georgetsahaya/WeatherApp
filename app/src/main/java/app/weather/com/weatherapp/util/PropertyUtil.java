package app.weather.com.weatherapp.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtil {
    private static final int cityLimit = 100;
    private static final String CITY_CONFIG_FILE = "cities.properties";
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
