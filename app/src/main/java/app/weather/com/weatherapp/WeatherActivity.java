package app.weather.com.weatherapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import app.weather.com.weatherapp.model.WeatherResponse;
import app.weather.com.weatherapp.util.WeatherReader;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends AppCompatActivity {
    TextView tCity;
    TextView tUpdatedTime;
    TextView tWeather;
    TextView tTemperature;
    TextView tWind;

    Spinner citiesSpinner;

    private String [] noCity = { "Sydney", "Melbourne", "Wollongong" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tCity = (TextView) findViewById(R.id.city);
        tUpdatedTime = (TextView) findViewById(R.id.updatedTime);
        tWeather = (TextView) findViewById(R.id.weather);
        tTemperature = (TextView) findViewById(R.id.temperature);
        tWind = (TextView) findViewById(R.id.wind);

        citiesSpinner = (Spinner) findViewById(R.id.citiSpinner);
        configureSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void configureSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                noCity);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(spinnerAdapter);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String cityName = (String) parent.getItemAtPosition(position);

                tCity.setText(cityName);

                WeatherReader.getWeather(cityName).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<WeatherResponse>() {
                    @Override
                    public void call(WeatherResponse weatherResponse) {
                        tCity.setText(cityName);
                        tUpdatedTime.setText("");
                        tTemperature.setText(weatherResponse.getMain().getTemp());
                        tWeather.setText(weatherResponse.getWeather().get(0).getDescription());
                        tWind.setText(getKmphFromMps(weatherResponse.getWind().getSpeed()));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        tCity.setText("Error");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getKmphFromMps(String mps) {
        String toRet;

        try {
            double kmph = Float.valueOf(mps) * 3.6;

            return String.format("%.2f", kmph) + " km/H";
        } catch (ArithmeticException e) {
            return "0 Km/H";
        }
    }
}
