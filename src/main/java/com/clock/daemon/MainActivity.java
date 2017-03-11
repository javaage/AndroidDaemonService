package com.clock.daemon;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.clock.daemon.service.BackgroundService;
import com.clock.daemon.service.GrayService;
import com.clock.daemon.service.WhiteService;
import com.clock.daemon.util.AsyncTextViewLoader;
import com.clock.daemon.util.SimpleEchoSocket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtSocket;
    private final static String TAG = MainActivity.class.getSimpleName();
    /**
     * 黑色唤醒广播的action
     */
    private final static String BLACK_WAKE_ACTION = "com.wake.black";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSocket = (TextView)findViewById(R.id.txtSocket);

        findViewById(R.id.btn_white).setOnClickListener(this);
        findViewById(R.id.btn_gray).setOnClickListener(this);
        findViewById(R.id.btn_black).setOnClickListener(this);
        findViewById(R.id.btn_background_service).setOnClickListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        txtSocket.setText(WhiteService.strUrl);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_white) { //系统正常的前台Service，白色保活手段
            Intent whiteIntent = new Intent(getApplicationContext(), WhiteService.class);
            startService(whiteIntent);
        } else if (viewId == R.id.btn_gray) {//利用系统漏洞，灰色保活手段（API < 18 和 API >= 18 两种情况）
            Intent grayIntent = new Intent(getApplicationContext(), GrayService.class);
            startService(grayIntent);
        } else if (viewId == R.id.btn_black) { //拉帮结派，黑色保活手段，利用广播唤醒队友
            Intent blackIntent = new Intent();
            blackIntent.setAction(BLACK_WAKE_ACTION);
            sendBroadcast(blackIntent);
            /*AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent operation = PendingIntent.getBroadcast(this, 123, blackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), operation);*/
        } else if (viewId == R.id.btn_background_service) {//普通的后台进程
            Intent bgIntent = new Intent(getApplicationContext(), BackgroundService.class);
            startService(bgIntent);
        }
    }
}
