package weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PErry on 2018/1/13.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weather_id;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
