package ru.artyomov.dmitry.weatheryola.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.artyomov.dmitry.weatheryola.common.Common;
import ru.artyomov.dmitry.weatheryola.model.WeatherData;

public class WeatherDatabase extends SQLiteOpenHelper {

    private static final String TAG = WeatherDatabase.class.getSimpleName();
    private static final String TABLE_NAME = WeatherDatabase.class.getName();

    public WeatherDatabase(Context context) {
        super(context, Common.WeatherDatabaseTable.DB_NAME, null, Common.WeatherDatabaseTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Common.WeatherDatabaseTable.CREATE_TABLE_QUERY);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Common.WeatherDatabaseTable.DROP_QUERY);
        this.onCreate(db);
        db.close();
    }

    public boolean isTableNotEmpty(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + Common.WeatherDatabaseTable.TABLE_NAME, null);
        Boolean rowExists;

        if (mCursor.moveToFirst()) {
            mCursor.close();
            rowExists = true;
            db.close();
        } else {
            mCursor.close();
            rowExists = false;
            db.close();
        }
        db.close();
        return rowExists;
    }

    public void addDataInDB(String icon, String temp, String time, String humidity, String wind, String pressure,
                            String sunrise, String sunset){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Common.WeatherDatabaseTable.ICON,icon);
        values.put(Common.WeatherDatabaseTable.TEMP,temp);
        values.put(Common.WeatherDatabaseTable.HUMIDITY,humidity);
        values.put(Common.WeatherDatabaseTable.WIND,wind);
        values.put(Common.WeatherDatabaseTable.PRESSURE,pressure);
        values.put(Common.WeatherDatabaseTable.SUNRISE,sunrise);
        values.put(Common.WeatherDatabaseTable.SUNSET,sunset);
        values.put(Common.WeatherDatabaseTable.TIME,time);
        try {
            db.insert(Common.WeatherDatabaseTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        db.close();
    }


    public void updateData(String keyId, String icon, String temp, String time, String humidity, String wind, String pressure, String sunrise, String sunset){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Common.WeatherDatabaseTable.ICON,icon);
        values.put(Common.WeatherDatabaseTable.TEMP,temp);
        values.put(Common.WeatherDatabaseTable.HUMIDITY,humidity);
        values.put(Common.WeatherDatabaseTable.WIND,wind);
        values.put(Common.WeatherDatabaseTable.PRESSURE,pressure);
        values.put(Common.WeatherDatabaseTable.SUNRISE,sunrise);
        values.put(Common.WeatherDatabaseTable.SUNSET,sunset);
        values.put(Common.WeatherDatabaseTable.TIME,time);

        db.update(Common.WeatherDatabaseTable.TABLE_NAME, values, Common.WeatherDatabaseTable.KEY_ID + " = ?",
                new String[]{String.valueOf(keyId)});

        db.close();
    }

    public WeatherData getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Common.WeatherDatabaseTable.GET_ITEMS_QUERY, null);
        WeatherData weatherData = new WeatherData();
        if (cursor.moveToFirst()) {
            do {
                weatherData.setIcon(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.ICON)));
                weatherData.setTemp(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.TEMP)));
                weatherData.setHumidity(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.HUMIDITY)));
                weatherData.setTime(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.TIME)));
                weatherData.setWind(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.WIND)));
                weatherData.setSunrise(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.SUNRISE)));
                weatherData.setPressure(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.PRESSURE)));
                weatherData.setSunset(cursor.getString(cursor.getColumnIndex(Common.WeatherDatabaseTable.SUNSET)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return weatherData;
    }

}
