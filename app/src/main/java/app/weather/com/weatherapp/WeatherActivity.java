package app.weather.com.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import app.weather.com.weatherapp.model.WeatherResponse;
import app.weather.com.weatherapp.util.PropertyUtil;
import app.weather.com.weatherapp.util.WeatherReader;
import app.weather.com.weatherapp.view.WeatherView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends AppCompatActivity {
    WeatherView weatherView;
    ArrayAdapter<CharSequence> spinnerAdapter;

    ProgressDialog progressDialog;

    /*
     * If no cities are provided in the property file, thid default one is uses
     */
    private final String [] noCity = { "No Cities" };

    List<String> cityList;
    List<String> addedCities = new LinkedList<>();

    Observable<WeatherResponse> weatherResponseObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadViews();
    }

    private void loadViews() {
        weatherView = (WeatherView) findViewById(R.id.weatherView);

        cityList = PropertyUtil.getCityList(getApplicationContext());
        progressDialog = new ProgressDialog(this);

        configureSpinner(false, null);

        configureFooter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        weatherResponseObservable.unsubscribeOn(AndroidSchedulers.mainThread());
        super.onDestroy();
    }

    private void configureFooter() {
        weatherView.setAddActionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * To add new city
                 */
                resetCitySpinner(weatherView.getAddedCity());

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                weatherView.clearAddCity();
            }
        });
    }

    private void resetCitySpinner(String newCity) {
        addedCities.add(newCity);
        configureSpinner(true, newCity);
        spinnerAdapter.notifyDataSetChanged();
    }

    private void configureSpinner(final boolean isReset, final String newCity) {
        List<String> listToShow = new ArrayList<>();
        int currPos = weatherView.getCitiesSpinner().getSelectedItemPosition();

        if (cityList.size() == 0 && addedCities.size() == 0) {
            listToShow.addAll(Arrays.asList(noCity));
            weatherView.getCitiesSpinner().setEnabled(false);
        } else {
            listToShow.addAll(addedCities);
            listToShow.addAll(cityList);
            weatherView.getCitiesSpinner().setEnabled(true);
        }

        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(listToShow);
        listToShow.clear();
        listToShow.addAll(hashSet);

        spinnerAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                listToShow);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weatherView.getCitiesSpinner().setAdapter(spinnerAdapter);
        weatherView.getCitiesSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showDialog();
                final String cityName = (String) parent.getItemAtPosition(position);

                weatherResponseObservable = WeatherReader.getWeather(cityName);
                weatherResponseObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<WeatherResponse>() {
                    @Override
                    public void call(WeatherResponse weatherResponse) {
                        weatherView.fillTable(weatherResponse);
                        progressDialog.dismiss();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        weatherView.clearTable();
                        progressDialog.dismiss();

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isReset) {
            weatherView.getCitiesSpinner().setSelection(spinnerAdapter.getPosition(newCity));
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        progressDialog.setTitle(R.string.app_name);
        progressDialog.setMessage(getString(R.string.getting_weather));
        progressDialog.show();
    }
}
