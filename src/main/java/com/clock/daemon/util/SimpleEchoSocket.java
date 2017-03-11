package com.clock.daemon.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.clock.daemon.MainActivity;
import com.clock.daemon.R;
import com.clock.daemon.service.WhiteService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;
/**
 * Basic Echo Client Socket
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleEchoSocket {

    private int FOREGROUND_ID = 2001;
    private MainActivity service;
    public Session session1;
    private Timer timer;

    public SimpleEchoSocket(MainActivity context){
        this.service = context;
        this.timer = new Timer();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("close");
        this.session1 = null;
        this.timer.cancel();
        service.getUrl();
    }

    @OnWebSocketError
    public void onError(Session session, Throwable exp){
        System.out.println("error");
        this.session1 = null;
        this.timer.cancel();
        service.getUrl();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session1 = session;
        System.out.println("connect");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("" + FOREGROUND_ID);
                try {
                    session1.getRemote().sendString("" + FOREGROUND_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.scheduleAtFixedRate(task,60*1000, 60*1000);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        String title="title";
        String content = msg;
        try{
            JSONObject json = new JSONObject(msg);
            title = json.getString("title");
            content = json.getString("message");
        }catch (Exception exp){
            System.out.println(exp.toString());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentInfo("message");
        builder.setWhen(System.currentTimeMillis());

        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setAutoCancel(true);

        Intent activityIntent = new Intent(service, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        NotificationManager mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(++FOREGROUND_ID, notification);
    }
}
