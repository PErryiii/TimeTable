package weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import weather.service.AutoUpdateService;

/**
 * Created by PErry on 2018/3/27.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}
