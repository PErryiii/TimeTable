package weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PErry on 2018/1/13.
 */

public class Aqi {

    public City city;

    public class City{
        @SerializedName("aqi")
        public String cityAqi;

        @SerializedName("pm25")
        public String cityPm25;
    }

}
