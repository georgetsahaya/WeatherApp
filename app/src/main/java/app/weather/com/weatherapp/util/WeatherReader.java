package app.weather.com.weatherapp.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.weather.com.weatherapp.model.WeatherResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

public class WeatherReader {
    /*
     * Reading the weather info from the openweathermap site with the API key
     */
    public static final String URL1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String URL2 = "&APPID=f3cc1b22c0232ac17cdaaa7dced8322e&units=metric";

    /*
     * Socket timeout
     */
    private static final int TIMEOUT = 5000;

    private static final OkHttpClient client = new OkHttpClient();
    private static Gson gson = new Gson();

    public static Observable<WeatherResponse> getWeather(final String city) {
        return Observable.create(new Observable.OnSubscribe<WeatherResponse>() {
            @Override
            public void call(Subscriber<? super WeatherResponse> subscriber) {

                try {
                    HttpURLConnection con = null ;
                    InputStream is = null;

                    con = (HttpURLConnection) ( new URL(URL1 + city + URL2)).openConnection();
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setConnectTimeout(TIMEOUT);
                    con.setReadTimeout(TIMEOUT);
                    con.connect();

                    StringBuffer buffer = new StringBuffer();
                    is = con.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    while ( (line = br.readLine()) != null ) {
                        buffer.append(line);
                    }

                    is.close();
                    con.disconnect();
                    WeatherResponse weatherResponse = gson.fromJson(buffer.toString(), WeatherResponse.class);

                    subscriber.onNext(weatherResponse);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    throw new RuntimeException("IOException:" + e);
                } catch (JsonSyntaxException je) {
                    throw new RuntimeException("JsonSyntaxException:" + je);
                }
            }
        });
    }
}
