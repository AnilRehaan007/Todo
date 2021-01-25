package com.example.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    public static Context context;
    private static String taskName;
    private static int taskId;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: called");

        MediaPlayer mediaPlayer = MediaPlayer.create(context,R.raw.iphone_7);

        mediaPlayer.start();

        Log.d(TAG, "onReceive: name: " + taskName);
        Log.d(TAG, "onReceive: id: " + taskId);

        Toast.makeText(context,taskName,Toast.LENGTH_LONG).show();

        Intent intent1=new Intent(context,NotificationShower.class);
        intent1.putExtra(TaskTable.task_name,taskName);
        intent1.putExtra(TaskTable.task_id,taskId);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {

            context.startForegroundService(intent1);
        }
        else
        {
            context.startService(intent1);
        }

    }



   public  void fireAlarm(Calendar calendar, int task_id)
   {

       Log.d(TAG, "fireAlarm: called");

       Intent intent2=new Intent(context,AlarmReceiver.class);

       ContentResolver contentResolver=context.getContentResolver();
       String selection=TaskTable.task_id+"="+task_id;
       Cursor cursor=contentResolver.query(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),
               new String[]{TaskTable.task_name},selection,null,null);

       if(cursor!=null)
       {
          while (cursor.moveToNext())
          {

             AlarmReceiver.taskName=cursor.getString(cursor.getColumnIndex(TaskTable.task_name));
             AlarmReceiver.taskId=task_id;

              Log.d(TAG, "fireAlarm: task name: " + taskName);
              Log.d(TAG, "fireAlarm: task_id: " + taskId);

              break;

          }

          cursor.close();
       }


       AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
       PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent2, 0);
       am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pi);

       Log.d(TAG, "onTimeSet: current: " + System.currentTimeMillis());
       Log.d(TAG, "onTimeSet: set time :" + calendar.getTimeInMillis());



   }

   public void updateDatabase(int task_id)
   {

       ContentResolver contentResolver=context.getContentResolver();
       ContentValues contentValues=new ContentValues();
       contentValues.put(TaskTable.status,1);

       Uri uri=Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name);
       contentResolver.update(ContentUris.withAppendedId(uri,task_id),contentValues,null,null);

       String  selection=TaskTable.task_date+">=date('now') and "+TaskTable.task_date+"<=date('now','+1 month') and "+TaskTable.status+"=0";
       Cursor cursor=contentResolver.query(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name)
               ,new String[]{TaskTable.task_id,TaskTable.task_notification_information},selection,null,null);

       if(cursor!=null)
       {
           while (cursor.moveToNext())
           {

               int id=cursor.getInt(cursor.getColumnIndex(TaskTable.task_id));
               byte[] value=cursor.getBlob(cursor.getColumnIndex(TaskTable.task_notification_information));

               try {
                   startAlarmService(value,id);

               } catch (Exception e) {

                   Log.e(TAG, "onDestroy: unable to call show " + e.getMessage());
               }

               break;

           }

           cursor.close();
       }



   }
    public void startAlarmService(byte[] a, int task_id) throws Exception
    {

        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(a);
        ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);

        Calendar calendar=(Calendar)objectInputStream.readObject();


        Intent intent4=new Intent(context,TaskAlarm.class);
        intent4.putExtra(TaskAlarm.calender_id,calendar);
        intent4.putExtra(TaskAlarm.task_id,task_id);

        Log.d(TAG, "startAlarmService: calling id after updation " + task_id);


      if(calendar.getTimeInMillis()>=System.currentTimeMillis())
      {
          if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
          {

              context.startForegroundService(intent4);
          }
          else
          {
              context.startService(intent4);
          }

      }
      else
      {
          Log.d(TAG, "startAlarmService: time is past");
      }

    }

   public static AlarmReceiver getInstance()
   {
       return new AlarmReceiver();

   }
}
