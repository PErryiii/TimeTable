package tableEdit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.timetable_1.Notification;

public class RingReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if ("tableEdit.RING".equals(intent.getAction())){
            //获取courseId
            int courseId = intent.getIntExtra("courseId", 0);
            //启动闹钟Activity
            Log.d("myTest","通知执行了");
            Intent i = new Intent(context, Notification.class);
            i.putExtra("courseId", courseId);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            //设置下周再响一次
            AlarmManager manager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int aWeek = 60 * 60 * 1000 * 24 * 7;    //一周的毫秒数
            long triggerAtTime = System.currentTimeMillis() + aWeek;
            Intent intent1 = new Intent(context, Notification.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, courseId,
                    intent1, 0);
            manager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent);
        }
    }
}
