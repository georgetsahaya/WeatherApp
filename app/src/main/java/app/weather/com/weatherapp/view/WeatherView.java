package app.weather.com.weatherapp.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.weather.com.weatherapp.R;
import app.weather.com.weatherapp.model.WeatherResponse;

public class WeatherView extends LinearLayout {
    /*
     * The weather API provides the values in meter/sec. To convert it into Km/H, the value needs
     * to be multiplied by 3.6
     */
    private final float KMPH_CONVERSION_FACTOR = 3.6f;

    /*
     * The date format gives "Thursday 11:00 AM" like format
     */
    private final String DATE_FORMAT = "EEEE h:mm a";

    private final String EMPTY_STRING = "";

    TextView tCity;
    TextView tUpdatedTime;
    TextView tWeather;
    TextView tTemperature;
    TextView tWind;

    TextView eNewCity;
    LinearLayout tAdd;

    Spinner citiesSpinner;

    public WeatherView(Context context) {
        super(context);

        loadViews(context);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadViews(context);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        loadViews(context);
    }

    private void loadViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_weather, this);

        tAdd = (LinearLayout) view.findViewById(R.id.tAdd);
        eNewCity = (EditText) view.findViewById(R.id.cityNew);

        tCity = (TextView) view.findViewById(R.id.city);
        tUpdatedTime = (TextView) view.findViewById(R.id.updatedTime);
        tWeather = (TextView) view.findViewById(R.id.weather);
        tTemperature = (TextView) view.findViewById(R.id.temperature);
        tWind = (TextView) view.findViewById(R.id.wind);

        citiesSpinner = (Spinner) view.findViewById(R.id.citiSpinner);
    }

    public void setAddActionListener(OnClickListener onClickListener) {
        tAdd.setOnClickListener(onClickListener);
    }

    public String getAddedCity() {
        return eNewCity.getText().toString();
    }

    public void clearAddCity() {
        eNewCity.setText(EMPTY_STRING);
    }

    public Spinner getCitiesSpinner() {
        return citiesSpinner;
    }

    public void fillTable(WeatherResponse weatherResponse) {
        tCity.setText(weatherResponse.getCityName());
        tUpdatedTime.setText(getTimeStamp());
        tTemperature.setText(weatherResponse.getMain().getTemp() + "\u2103");
        tWeather.setText(weatherResponse.getWeather().get(0).getDescription());
        tWind.setText(getKmphFromMps(weatherResponse.getWind().getSpeed()));
    }

    public void clearTable() {
        tCity.setText(EMPTY_STRING);
        tUpdatedTime.setText(EMPTY_STRING);
        tTemperature.setText(EMPTY_STRING);
        tWeather.setText(EMPTY_STRING);
        tWind.setText(EMPTY_STRING);
    }

    private String getKmphFromMps(String mps) {
        try {
            double kmph = Float.valueOf(mps) * KMPH_CONVERSION_FACTOR;

            return String.format("%.2f", kmph) + " km/H";
        } catch (ArithmeticException e) {
            return "0 Km/H";
        }
    }

    private String getTimeStamp() {
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            return df.format(new Date());
        } catch (Exception e) {
            return "";
        }
    }
}
