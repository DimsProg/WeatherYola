package ru.artyomov.dmitry.weatheryola.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

import ru.artyomov.dmitry.weatheryola.model.WeatherModel;

public interface IWeatherService {
    @GET("weather")
    Observable<WeatherModel> getWeatherByLatLon(@Query("lat") String lat, @Query("lon") String lon,
                                                @Query("appid") String appid, @Query("units") String unit);
}
