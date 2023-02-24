package device.apps.alarmmanagertest;

import static device.apps.alarmmanagertest.MainActivity.LOG_TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import java.util.Calendar;


public class AutoStartManager {

    public static String AUTO_START_TIMEOUT = "AUTO_START_TIMEOUT";
    public static String AUTO_START_ACTION = "AUTO_START_ACTION";
    private static int ALARM_PENDING_INTENT_REQ_CODE = 0x15;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Context context;
    private static AutoStartManager instance;

    private AutoStartManager(Context context) {
        this.context = context;
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }

    public static AutoStartManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AutoStartManager.class) {
                if (instance == null) {
                    instance = new AutoStartManager(context);
                }
            }
        }
        return instance;
    }

    public void setAlarm(int triggerAtSeconds) {
        Log.d(LOG_TAG, "AutoStartManager setAlarm()");
        Intent intent = new Intent(context, AutoStartReceiver.class).setAction(AUTO_START_ACTION);
        // (version 31 and above) FLAG_IMMUTABLE or FLAG_MUTABLE
        pendingIntent = PendingIntent.getBroadcast(context, ALARM_PENDING_INTENT_REQ_CODE, intent,
                PendingIntent.FLAG_IMMUTABLE);
        Calendar cal = setTime(triggerAtSeconds);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
    }

    @NonNull
    private Calendar setTime(int delaySeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, delaySeconds);
        return cal;
    }

    public void cancelAlarm() {
        Log.d(LOG_TAG, "AutoStartManager cancelAlarm()");
        Intent intent = new Intent(context, AutoStartReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, ALARM_PENDING_INTENT_REQ_CODE, intent,
                PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}
