package device.apps.alarmmanagertest;

import static device.apps.alarmmanagertest.MainActivity.LOG_TAG;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * 일정 시간이 지난 뒤에 자동으로 실행 되는 기능 추가
 * 전달받은 {@link #triggerAtSeconds}만큼 뒤에
 * 콜백 되도록 AutoStartManager에게 등록하는 역할
 */
public class AutoStartService extends Service {

    private int triggerAtSeconds;
    public static final String DISABLE_OPTION = "disable";

    @Override
    public void onCreate() {
        if(AutoStartManager.isLogging) Log.d(AutoStartManager.LOG_TAG, "AutoStartService onCreate()");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(AutoStartManager.isLogging) Log.d(AutoStartManager.LOG_TAG, "AutoStartService onDestroy()");
        AutoStartManager.getInstance(this).cancelAlarm();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if(AutoStartManager.isLogging) Log.d(AutoStartManager.LOG_TAG, "AutoStartService onTaskRemoved()");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * intent로 넘어온 extra값(자동실행을 위한 시간값(단위:mills))을 이용해서 다음 작업을 한다.
     * 1. {@link AutoStartManager#AUTO_START_TIMEOUT}가 "Disable" 인지 체크하고,
     * 2. String -> int 타입으로 변경하고
     * 3. {@link AutoStartManager#AUTO_START_TIMEOUT} 값으로 Alarm 을 등록한다.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(AutoStartManager.isLogging) Log.d(AutoStartManager.LOG_TAG, "AutoStartService onStartCommand()");
        final String triggerAtSecondsStr = intent.getStringExtra(AutoStartManager.AUTO_START_TIMEOUT);
        if(AutoStartManager.isLogging) Log.d(AutoStartManager.LOG_TAG, "triggerAtSecondsStr in AutoStartService : " + triggerAtSecondsStr);
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
        if (triggerAtSecondsStr == null) {
            if(AutoStartManager.isLogging) Log.d(AutoStartManager.LOG_TAG, "autoRunAfterTimeout is null");
            return true;
        }

        if (DISABLE_OPTION.equalsIgnoreCase(triggerAtSecondsStr)) {
            if(AutoStartManager.isLogging) Log.d(AutoStartManager.LOG_TAG, "autoRunAfterTimeout is Disable");
            return true;
        }

        return false;
    }

    public int toSecondsWithHandleException(String autoRunAfterTimeout) {
        try {
            return toSecondsFrom(autoRunAfterTimeout);
        } catch (Exception e) {
            Log.w(AutoStartManager.LOG_TAG,"Exception in AutoStartService occurred : autoRunAfterTimeout 값은 초 단위로 변경할 수 없습니다.", e.getCause());
            return -1;
        }
    }

    private int toSecondsFrom(String autoRunAfterTimeout) throws Exception {
        final int MILLIS_UNIT = 1000;
        return Integer.parseInt(autoRunAfterTimeout) / MILLIS_UNIT;
    }
}