package ru.artyomov.dmitry.weatheryola.ui;

import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Retrofit;
import ru.artyomov.dmitry.weatheryola.common.Common;
import ru.artyomov.dmitry.weatheryola.database.WeatherDatabase;
import ru.artyomov.dmitry.weatheryola.model.WeatherData;
import ru.artyomov.dmitry.weatheryola.model.WeatherModel;
import ru.artyomov.dmitry.weatheryola.R;
import ru.artyomov.dmitry.weatheryola.retrofit.IWeatherService;
import ru.artyomov.dmitry.weatheryola.retrofit.RetrofitClient;


public class MainActivity extends AppCompatActivity {

    ImageView imgWeather;
    TextView txtCityName, txtHumidity, txtSunrise, txtSunSet, txtPressure, txtTemperature, txtDateTime, txtWind, txtCoord;
    LinearLayout weatherPanel;
    ProgressBar loading;

    RelativeLayout relativeLayout;

    CompositeDisposable compositeDisposable;
    IWeatherService mService;
    WeatherDatabase mWeatherDatabase;
    final Handler handler  = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initRetrofit();
        mWeatherDatabase = new WeatherDatabase(this);
        sheduleUpdateData();
    }

    private void initRetrofit(){
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IWeatherService.class);
    }

    private void initViews(){
        imgWeather = findViewById(R.id.img_weather);
        txtCityName = findViewById(R.id.txt_city_name);
        txtHumidity = findViewById(R.id.txt_humidity);
        txtSunrise = findViewById(R.id.txt_sunrise);
        txtSunSet = findViewById(R.id.txt_sunset);
        txtPressure = findViewById(R.id.txt_pressure);
        txtTemperature = findViewById(R.id.txt_temperature);
        txtDateTime = findViewById(R.id.txt_datetime);
        txtWind = findViewById(R.id.txt_wind);
        txtCoord = findViewById(R.id.txt_geo_coord);

        weatherPanel = findViewById(R.id.weather_panel);
        relativeLayout = findViewById(R.id.root_layout);
        loading = findViewById(R.id.loading);
    }

    private void updateTxtViews( String temp, String time, String humidity, String pressure, String sunrise, String sunset, String wind ){
        txtCityName.setText(getString(R.string.city_name));
        txtTemperature.setText(temp);
        txtDateTime.setText(time);
        txtPressure.setText(pressure);
        txtHumidity.setText(humidity);
        txtSunrise.setText(sunrise);
        txtSunSet.setText(sunset);
        txtCoord.setText(String.format("[%.2f ; %.2f]",Common.LATITUDE,Common.LONGITUDE));
        txtWind.setText(wind);
    }

    private void saveDataToDB( String temp, String icon, String time, String humidity, String pressure, String sunrise, String sunset, String wind ){
        if (mWeatherDatabase.isTableNotEmpty()){
            mWeatherDatabase.updateData("1",icon,temp,time,humidity,wind,pressure,sunrise,sunset);
        }else{
            mWeatherDatabase.addDataInDB(icon,temp,time,humidity,wind,pressure,sunrise,sunset);
        }
    }

    private void sheduleUpdateData(){
        handler.postDelayed(new Runnable() {
            public void run() {
                getWeatherData();
                handler.postDelayed(this, Common.HANDLER_DELAY);
            }
        }, 0);
    }

    private void getWeatherData(){
        if (Common.isNetworkAvailable(this)){
            getLiveData();
        }else {
            getLocalData();
            noConnectionMessage();
        }
    }

    private void getLocalData(){
        Log.d(MainActivity.class.getSimpleName(), "!!! getLocalData !!!");
        if (!mWeatherDatabase.isTableNotEmpty()){
            return;
        }
        WeatherData weatherData = mWeatherDatabase.getData();
        imgWeather.setImageResource(Common.getIconIdByName(weatherData.getIcon()));
        updateTxtViews(weatherData.getTemp(),weatherData.getTime(),weatherData.getHumidity(),
                weatherData.getPressure(),weatherData.getSunrise(),weatherData.getSunset(), weatherData.getWind());
        weatherPanel.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    private void getLiveData(){
        Log.d(MainActivity.class.getSimpleName(), "!!! getLiveData !!!");
        compositeDisposable.add(mService.getWeatherByLatLon( String.valueOf(Common.LATITUDE), String.valueOf(Common.LONGITUDE), Common.APP_ID, "metric" )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherModel>(){
                   @Override
                   public void accept(WeatherModel weatherResult) throws Exception {
                       Picasso.get().load(new StringBuilder(Common.IMG_URL)
                               .append(weatherResult.getWeather().get(0).getIcon())
                               .append(".png").toString()).into(imgWeather);
                       String temp = new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                                .append("°C").toString();
                       String time = Common.convertUnixToStrDate(weatherResult.getDt());
                       String pressure = String.format(" %d мм.рт.ст",Common.convertHpaToMmhg(weatherResult.getMain().getPressure()));
                       String humidity = new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity()))
                               .append(" %").toString();
                       String sunrise = Common.convertUnixToStrHour(weatherResult.getSys().getSunrise());
                       String sunset = Common.convertUnixToStrHour(weatherResult.getSys().getSunset());
                       String wind  = String.format("%s; %.2f м/с",Common.convertDegToDirection(weatherResult.getWind().getDeg()),weatherResult.getWind().getSpeed());
                       updateTxtViews(temp,time,humidity,pressure,sunrise,sunset,wind);
                       saveDataToDB(temp,weatherResult.getWeather().get(0).getIcon(),time,humidity,pressure, sunrise, sunset,wind );

                       weatherPanel.setVisibility(View.VISIBLE);
                       loading.setVisibility(View.GONE);

                   }
                }, new Consumer<Throwable>() {
                   @Override
                   public void accept(Throwable throwable) throws Exception {
                       Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                   }
                })
        );

    }

    private void noConnectionMessage(){
        Toast.makeText(MainActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
    }
}



