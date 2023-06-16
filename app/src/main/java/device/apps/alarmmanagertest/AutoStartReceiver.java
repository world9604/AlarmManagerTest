package device.apps.alarmmanagertest;

import static device.apps.alarmmanagertest.MainActivity.LOG_TAG;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * 일정 시간이 지난 뒤에 자동으로 실행 되는 기능 추가
 * 다음에 2가지 액션을 처리한다.
 * 1. AUTO_START_ACTION : {@link AutoStartService}가 해당 시간에 도달하여서 콜밸 할 경우
 * 2. ACTION_BOOT_COMPLETED : 디바이스가 재부팅이 되었을 경우
 * - (조건 1) {@link AutoStartService#triggerAtSeconds} 값이 존재하고
 * - (조건 2) 앱이 포그라운드가 아니라면,
 * - 앱 실행
 */
public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(AutoStartManager.isLogging) Log.d(LOG_TAG, "AlarmWorkReceiver onReceive()");

        if (context == null || intent == null) {
            if(AutoStartManager.isLogging) Log.d(LOG_TAG, "context or intent is null in AlarmWorkReceiver onReceive()");
            return;
        }

        if (AutoStartManager.AUTO_START_ACTION.equals(intent.getAction())) {
            if(AutoStartManager.isLogging) Log.d(LOG_TAG, "AlarmWorkReceiver AUTO_START_ACTION");
            startAppIfNotOnForeground(context);
        } else if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if(AutoStartManager.isLogging) Log.d(LOG_TAG, "AlarmWorkReceiver ACTION_BOOT_COMPLETED");
            Intent autoStartIntent = new Intent(context, AutoStartService.class);
            context.startService(autoStartIntent);
        }
    }

    /**
     * {@link #isAppOnForeground}에서 연산이 필요하므로, 비동기 연산이 필요
     */
    private void startAppIfNotOnForeground(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!isAppOnForeground(context)) {
                    //앱이 포그라운드가 아닐 경우에만 EmKiosk 실행
                    startAutoStartService(context);
                }
                context.stopService(new Intent(context, AutoStartService.class));
            }
        }).start();
    }

    private void startAutoStartService(Context context) {
        Intent autoStartIntent = new Intent(context.getApplicationContext(), AutoStartService.class);
        autoStartIntent.putExtra(AutoStartManager.AUTO_START_TIMEOUT, "5000");
        context.startService(autoStartIntent);
    }

    /**
     * 현재 어플리케이션이 Foreground 상태인지 체크한다.
     * 시간이 걸리는 연산이기 때문에, 워커 스레드 사용을 추천한다.
     */
    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}