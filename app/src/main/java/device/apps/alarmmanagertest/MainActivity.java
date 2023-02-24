package device.apps.alarmmanagertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "taein";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.button);

        //관리자 패스워드 입력 이벤트
        btn.setOnClickListener(v -> {
            Log.d(LOG_TAG, "입력 이벤트");
            Intent intent = new Intent(getApplicationContext(), AutoStartService.class);
            intent.putExtra(AutoStartManager.AUTO_START_TIMEOUT, readTriggerAtSeconds());
            startService(intent);
        });
    }

    //EmKiosk.json에서 autoRunAfterTimeout값을 읽어온다.
    private String readTriggerAtSeconds() {
        //String autoRunAfterTimeout = "Disable";

        //5초
        //String autoRunAfterTimeout = "5000";

        //3분
        //String autoRunAfterTimeout = "180000";

        //1분
        String autoRunAfterTimeout = "60000";

        //5분
        //String autoRunAfterTimeout = "300000";

        //10분
        //String autoRunAfterTimeout = "600000";

        //String autoRunAfterTimeout = "0";

        //String autoRunAfterTimeout = "ㄴㄹㄴ";

        //String autoRunAfterTimeout = "-1";
        return autoRunAfterTimeout;
    }

}