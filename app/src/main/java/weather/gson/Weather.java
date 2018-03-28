package weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PErry on 2018/1/13.
 */

public class Weather {

    public String status;

    public Basic basic;

    public Aqi aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;


}
