package weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PErry on 2018/1/13.
 */

public class Forecast {

    public String date;

    public Cond cond;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Cond{

        @SerializedName("txt_d")
        public String info;

    }

    public class Temperature{

        public String max;

        public String min;

    }
}
