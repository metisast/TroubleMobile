package com.example.eugene.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.icu.util.TimeUnit;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by eugene on 11/3/16.
 */

public class MyService extends Service{

    final String LOG_TAG = "myLogs";

    private NotificationManager notificationManager;

    private String roomTitle = "";
    private String id = "0";

    /* Check to connect */
    private Socket socket;
    {
        try{
            socket = IO.socket("http://89.219.23.94:14602");
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
    }

    public void  onCreate(){
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");

        socket.on("task:show", ShowTasks);
        socket.connect();
    }

    public int onStartCommand(Intent intent, int flag, int startId){
        Log.d(LOG_TAG, "onStartCommand");

        return super.onStartCommand(intent, flag, startId);
    }

    private Emitter.Listener ShowTasks = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                Log.d("socket", jsonObject.getString("room_title"));
                roomTitle = jsonObject.getString("room_title");
                id = jsonObject.getString("id");

                createNotification(roomTitle, Integer.valueOf(id));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");

        /* Disconnected */
        socket.disconnect();
        //socket.off("new message", onNewMessage);
    }

    /* Create notification */
    public void createNotification(String roomTitle, Integer id){
        Log.d(LOG_TAG, "createNotification");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Кто-то потер лампу!");
        builder.setContentText("Нужна помощь в " + roomTitle);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBild");
        return null;
    }
}
