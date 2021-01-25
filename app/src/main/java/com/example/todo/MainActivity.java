package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements Profile.CallbackProfile {
    private static final String TAG = "MainActivity";
    public static String callBackFromWhere="empty";
    public static boolean editorCaller=false;
    public static boolean startService=false;
    public static final String demo="demo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         AlarmReceiver.context=getBaseContext();


         if(data_base_status())
         {

             getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout,new Profile(this)).addToBackStack(null).commit();

         }
         else
         {
             getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout,new MainSection()).addToBackStack(null).commit();

         }


//             getBaseContext().deleteDatabase("Todo");
//             getBaseContext().deleteDatabase("Todo.db");

    }

    public final boolean data_base_status()
    {

       boolean status=true;

        SQLiteDatabase sqLiteDatabase=getBaseContext().openOrCreateDatabase("Todo",MODE_PRIVATE,null);

        Cursor cursor=sqLiteDatabase.rawQuery("select count(*) from "+ProfileTable.table_name,null);

        if(cursor!=null)
        {

           while (cursor.moveToNext())
           {

               if(cursor.getInt(0)>0)
               {
                  status=false;

               }

           }
           cursor.close();

        }

         return status;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    public void profileConfirmation(boolean confirmation) {

        Log.d(TAG, "profileConfirmation: called");

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout,new MainSection()).addToBackStack(null).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity.editorCaller=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called by "+ callBackFromWhere);

        if(callBackFromWhere.equals(ServiceConfirmation.clear_data) || callBackFromWhere.equals(ServiceConfirmation.delete_account)
        || callBackFromWhere.equals(ServiceConfirmation.delete_task) || callBackFromWhere.equals(ServiceConfirmation.touch))
        {

            MainActivity.callBackFromWhere="empty";

            Log.d(TAG, "onStop: if condition called");

        }

        else if(editorCaller || startService)
        {

            MainActivity.editorCaller=false;

            Log.d(TAG, "onStop: true block called");

        }

        else
        {

            Log.d(TAG, "onStop: can start service");

                    ContentResolver contentResolver=getBaseContext().getContentResolver();

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

    }

    public void startAlarmService(byte[] a, int task_id) throws Exception
    {

        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(a);
        ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);

        Calendar calendar=(Calendar)objectInputStream.readObject();


        Intent intent=new Intent(MainActivity.this,TaskAlarm.class);
        intent.putExtra(TaskAlarm.calender_id,calendar);
        intent.putExtra(TaskAlarm.task_id,task_id);

         MainActivity.startService=true;

        if(calendar.getTimeInMillis()>=System.currentTimeMillis())
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {

                startForegroundService(intent);
            }
            else
            {
                startService(intent);
            }
        }
        else
        {
            Log.d(TAG, "startAlarmService: time is past");
        }
    }
}
