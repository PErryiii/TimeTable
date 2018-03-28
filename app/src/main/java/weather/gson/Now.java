package weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PErry on 2018/1/13.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    public Cond cond;

    public class Cond{
        @SerializedName("txt")
        public String cityInfo;
    }
}
