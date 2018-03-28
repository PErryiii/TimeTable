package weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PErry on 2018/1/13.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public Carwash carwash;

    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String comfortInfo;
    }

    public class Carwash{
        @SerializedName("txt")
        public String carwashInfo;
    }

    public class Sport{
        @SerializedName("txt")
        public String sportInfo;
    }
}
