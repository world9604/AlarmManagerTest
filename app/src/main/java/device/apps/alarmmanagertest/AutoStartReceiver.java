package device.apps.alarmmanagertest;

import static device.apps.alarmmanagertest.MainActivity.LOG_TAG;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "AlarmWorkReceiver onReceive()");

        if (context == null || intent == null) {
            Log.d(LOG_TAG, "context or intent is null in AlarmWorkReceiver onReceive()");
            return;
        }

        if (AutoStartManager.AUTO_START_ACTION.equals(intent.getAction())) {
            Log.d(LOG_TAG, "AlarmWorkReceiver AUTO_START_ACTION");
            startAppIfNotOnForeground(context);
        } else if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(LOG_TAG, "AlarmWorkReceiver ACTION_BOOT_COMPLETED");
            Intent autoStartIntent = new Intent(context, AutoStartService.class);
            context.startService(autoStartIntent);
        }
    }

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

    //현재 어플리케이션이 Foreground 상태인지 체크한다.
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