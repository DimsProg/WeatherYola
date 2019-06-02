package ru.artyomov.dmitry.weatheryola.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.artyomov.dmitry.weatheryola.R;

public class Common {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String IMG_URL = "https://openweathermap.org/img/w/";
    public static final String APP_ID = "4beef1f1f096ef55b2c087e0f7a35433";
    public static final double LATITUDE = 56.64;
    public static final double LONGITUDE = 47.89;

    public static final int HANDLER_DELAY = 1000*60*1;//интервал получения погоды раз в минуту


    public static String convertUnixToStrDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm EEE dd MM yyyy");
        String convertedDate = simpleDateFormat.format(date);
        return  convertedDate;
    }

    public static String convertUnixToStrHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String convertedDate = simpleDateFormat.format(date);
        return  convertedDate;
    }

    public static String convertDegToDirection( double deg ) {
        final String[] directions = {"↑ С", "↗ СВ", "→ В", "↘ ЮВ", "↓ Ю", "↙ ЮЗ", "← З", "↖ СЗ"};
        int directIndex = (int) Math.round(deg/45)%8;
        return directions[directIndex];
    }

    public static int convertHpaToMmhg(double hpa){
        return (int) (hpa / 1.333);
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static int getIconIdByName( String iconName ){
        int iconId = R.drawable.icon_01d;
        switch (iconName){
            case "01d":
                iconId = R.drawable.icon_01d;
                break;
            case "02d":
                iconId =  R.drawable.icon_02d;
                break;
            case "03d":
                iconId =  R.drawable.icon_03d;
                break;
            case "04d":
                iconId =  R.drawable.icon_04d;
                break;
            case "04n":
                iconId =  R.drawable.icon_04n;
                break;
            case "10d":
                iconId =  R.drawable.icon_10d;
                break;
            case "11d":
                iconId =  R.drawable.icon_11d;
                break;
            case "13d":
                iconId =  R.drawable.icon_13d;
                break;
            case "01n":
                iconId =  R.drawable.icon_01n;
                break;
            case "02n":
                iconId =  R.drawable.icon_02n;
                break;
            case "03n":
                iconId =  R.drawable.icon_03n;
                break;
            case "10n":
                iconId =  R.drawable.icon_10n;
                break;
            case "11n":
                iconId =  R.drawable.icon_11n;
                break;
            case "13n":
                iconId =  R.drawable.icon_13n;
                break;
        }
        return iconId;
    }

    public static final class WeatherDatabaseTable {

        public static final String DB_NAME = "weather";
        public static final String TABLE_NAME = "weathertable";
        public static final int DB_VERSION = +1;

        public static final String DROP_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String GET_ITEMS_QUERY = "SELECT * FROM " + TABLE_NAME;

        public static final String KEY_ID = "keyid";
        public static final String ICON = "icon";
        public static final String TEMP = "temp";
        public static final String HUMIDITY = "humidity";
        public static final String PRESSURE = "pressure";
        public static final String WIND = "wind";
        public static final String COUNTRY = "country";
        public static final String SUNRISE = "sunrise";
        public static final String SUNSET = "sunset";
        public static final String TIME = "time";

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + ICON + " TEXT,"
                + TEMP + " TEXT,"
                + HUMIDITY + " TEXT,"
                + PRESSURE + " TEXT,"
                + WIND + " TEXT,"
                + COUNTRY + " TEXT,"
                + SUNRISE + " TEXT,"
                + SUNSET + " TEXT,"
                + TIME + " TEXT" + ")";
    }
}
