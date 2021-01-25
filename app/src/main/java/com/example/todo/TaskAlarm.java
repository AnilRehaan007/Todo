package com.example.todo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class TaskAlarm extends IntentService {

    private static final String TAG = "TaskAlarm";
    public static final String calender_id="calender_id";
    public static final String task_id="task_id";
    public TaskAlarm() {
        super("TaskAlarm");

        setIntentRedelivery(true);
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        int id=intent.getIntExtra(task_id,0);
        Calendar calendar=(Calendar)intent.getSerializableExtra(calender_id);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {

            notification();

            AlarmReceiver.getInstance().fireAlarm(calendar,id);

            long time=(calendar.getTimeInMillis()-System.currentTimeMillis())+(20000);

            SystemClock.sleep(time);

            stopForeground(true);
        }
        else
        {

            AlarmReceiver.getInstance().fireAlarm((Calendar)intent.getSerializableExtra(calender_id),intent.getIntExtra(task_id,0));

        }


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

        Notification notification=new NotificationCompat.Builder(this,channel_id)
                .setPriority(NotificationManager.IMPORTANCE_NONE).setOngoing(true).setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("ALERT").setContentText("Task Notification").setCategory(Notification.CATEGORY_SERVICE).build();


        startForeground(1,notification);
    }
}