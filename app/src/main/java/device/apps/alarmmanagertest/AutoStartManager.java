package device.apps.alarmmanagertest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.Calendar;

/**
 * 일정 시간이 지난 뒤에 자동으로 실행 되는 기능 추가
 * 다음 2가지 역할을 가진다.
 * 1. Alarm 을 설정하는 역할
 * 2. Alarm 을 취소하는 역할
 * 싱글톤으로 존재. {@link AutoStartService}에서만 사용된다.
 */
public class AutoStartManager {

    public static final boolean isLogging = false;
    public static final String LOG_TAG = "AutoStart";
    public static String AUTO_START_TIMEOUT = "AUTO_START_TIMEOUT";
    public static final String AUTO_START_ACTION = "AUTO_START_ACTION";
    private static final int ALARM_PENDING_INTENT_REQ_CODE = 0x15;
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

    /**
     * triggerAtSeconds 뒤에 호출되도록 Alarm 을 {@link AutoStartReceiver}에 등록
     * @param triggerAtSeconds AlarmManager 에 등록하기 위한 mills 값
     */
    public void setAlarm(int triggerAtSeconds) {
        if(AutoStartManager.isLogging) Log.d(LOG_TAG, "AutoStartManager setAlarm()");
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
        if(AutoStartManager.isLogging) Log.d(LOG_TAG, "AutoStartManager cancelAlarm()");
        Intent intent = new Intent(context, AutoStartReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, ALARM_PENDING_INTENT_REQ_CODE, intent,
                PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}