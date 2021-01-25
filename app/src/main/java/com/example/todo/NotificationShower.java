package com.example.todo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


public class NotificationShower extends IntentService {

    private static final String TAG = "NotificationShower";

    private String task_name;

    public NotificationShower() {
        super("NotificationShower");

        setIntentRedelivery(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(Intent intent) {

        task_name=intent.getStringExtra(TaskTable.task_name);
        int id=intent.getIntExtra(TaskTable.task_id,0);

        notification();

        AlarmReceiver.getInstance().updateDatabase(id);

          SystemClock.sleep(10000);

//

        stopForeground(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void notification()
    {
        String channel_id="1";
        String channel_name="TaskAlarm";

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {

            NotificationChannel notificationChannel=new NotificationChannel(channel_id,channel_name, NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.BLUE);

            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);


        }


        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,
                new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification=new NotificationCompat.Builder(this,channel_id).setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_NONE).setOngoing(true).setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("TASK NAME").setContentText(task_name).setCategory(Notification.CATEGORY_SERVICE).build();


        startForeground(1,notification);

    }

}