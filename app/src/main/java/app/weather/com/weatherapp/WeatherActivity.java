package app.weather.com.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import app.weather.com.weatherapp.model.WeatherResponse;
import app.weather.com.weatherapp.util.InputDialog;
import app.weather.com.weatherapp.util.PropertyUtil;
import app.weather.com.weatherapp.util.WeatherReader;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends AppCompatActivity {
    TextView tCity;
    TextView tUpdatedTime;
    TextView tWeather;
    TextView tTemperature;
    TextView tWind;

    TextView eNewCity;
    LinearLayout tAdd;

    Spinner citiesSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;

    ProgressDialog progressDialog;

    private String [] noCity = { "noCities" };

    List<String> cityList;
    List<String> addedCities = new LinkedList<>();

    Observable<WeatherResponse> weatherResponseObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tAdd = (LinearLayout) findViewById(R.id.tAdd);
        eNewCity = (EditText) findViewById(R.id.cityNew);
        configureFooter();

        tCity = (TextView) findViewById(R.id.city);
        tUpdatedTime = (TextView) findViewById(R.id.updatedTime);
        tWeather = (TextView) findViewById(R.id.weather);
        tTemperature = (TextView) findViewById(R.id.temperature);
        tWind = (TextView) findViewById(R.id.wind);

        citiesSpinner = (Spinner) findViewById(R.id.citiSpinner);
        cityList = PropertyUtil.getCityList(getApplicationContext());
        configureSpinner();
    }

    @Override
    protected void onDestroy() {
        weatherResponseObservable.unsubscribeOn(AndroidSchedulers.mainThread());
        super.onDestroy();
    }

    private void configureFooter() {
        tAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCitySpinner(eNewCity.getText().toString());

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                eNewCity.setText("");
            }
        });
    }

    private void resetCitySpinner(String newCity) {
        addedCities.add(newCity);
        configureSpinner();
        spinnerAdapter.notifyDataSetChanged();
    }

    private void configureSpinner() {
        List<String> listToShow = new ArrayList<>();

        if (cityList.size() == 0 && addedCities.size() == 0) {
            listToShow.addAll(Arrays.asList(noCity));
            citiesSpinner.setEnabled(false);
        } else {
            listToShow.addAll(addedCities);
            listToShow.addAll(cityList);
            citiesSpinner.setEnabled(true);
        }

        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(listToShow);
        listToShow.clear();
        listToShow.addAll(hashSet);

        spinnerAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                listToShow);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(spinnerAdapter);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showDialog();
                final String cityName = (String) parent.getItemAtPosition(position);

                tCity.setText(cityName);

                weatherResponseObservable = WeatherReader.getWeather(cityName);
                weatherResponseObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<WeatherResponse>() {
                    @Override
                    public void call(WeatherResponse weatherResponse) {
                        fillTable(weatherResponse);
                        progressDialog.dismiss();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        clearTable();
                        progressDialog.dismiss();

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fillTable(WeatherResponse weatherResponse) {
        tCity.setText(weatherResponse.getCityName());
        tUpdatedTime.setText(getTimeStamp());
        tTemperature.setText(weatherResponse.getMain().getTemp() + "\u2103");
        tWeather.setText(weatherResponse.getWeather().get(0).getDescription());
        tWind.setText(getKmphFromMps(weatherResponse.getWind().getSpeed()));
    }

    private void clearTable() {
        tCity.setText("");
        tUpdatedTime.setText("");
        tTemperature.setText("");
        tWeather.setText("");
        tWind.setText("");
    }

    private void showDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.app_name);
        progressDialog.setMessage("Getting weather data ...");
        progressDialog.show();
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

    private String getTimeStamp() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("EEEE h:mm a");
            return df.format(new Date());
        } catch (Exception e) {
            return "";
        }
    }
}
