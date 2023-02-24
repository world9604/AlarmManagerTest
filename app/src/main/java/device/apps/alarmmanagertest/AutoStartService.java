package device.apps.alarmmanagertest;

import static device.apps.alarmmanagertest.MainActivity.LOG_TAG;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class AutoStartService extends Service {

    private int triggerAtSeconds;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "AutoStartService onCreate()");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "AutoStartService onDestroy()");
        AutoStartManager.getInstance(this).cancelAlarm();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOG_TAG, "AutoStartService onTaskRemoved()");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "AutoStartService onStartCommand()");
        String triggerAtSecondsStr = intent.getStringExtra(AutoStartManager.AUTO_START_TIMEOUT);
        if(stopIfDisableAutoStartOption(triggerAtSecondsStr)) return START_STICKY;
        this.triggerAtSeconds = toSecondsWithHandleException(triggerAtSecondsStr);
        AutoStartManager.getInstance(this).setAlarm(triggerAtSeconds);
        return START_STICKY;
    }

    private boolean stopIfDisableAutoStartOption(String triggerAtSecondsStr) {
        if (isDisableAutoStartOption(triggerAtSecondsStr)) {
            stopSelf();
            return true;
        }
        return false;
    }

    private boolean isDisableAutoStartOption(String triggerAtSecondsStr) {
        final String DISABLE_OPTION = "disable";
        if (DISABLE_OPTION.equalsIgnoreCase(triggerAtSecondsStr)) {
            Log.d(LOG_TAG, "autoRunAfterTimeout is Disable");
            return true;
        }
        return false;
    }

    public int toSecondsWithHandleException(String autoRunAfterTimeout) {
        try {
            return toSecondsFrom(autoRunAfterTimeout);
        } catch (Exception e) {
            Log.e(LOG_TAG,"Exception occurred : autoRunAfterTimeout 값은 분 단위로 변경할 수 없습니다.", e.getCause());
            return -1;
        }
    }

    private int toSecondsFrom(String autoRunAfterTimeout) throws Exception {
        final int MILLIS_UNIT = 1000;
        return Integer.parseInt(autoRunAfterTimeout) / MILLIS_UNIT;
    }
}