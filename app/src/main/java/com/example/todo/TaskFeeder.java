package com.example.todo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class TaskFeeder implements Runnable {

    private static final String TAG = "TaskFeeder";

    private final taskFeederCallBack taskFeederCallBack;
    private final Context context;
    private final String task;
    private final String description;
    private final String date;
    private final String time;
    private final Status status;
    private final long position;
    private final int task_status;
    private final byte[] calender_instance;
    enum Status
    {
        Insert,
        Update,
        Delete,
        DeleteAll;
    }

    public TaskFeeder(TaskFeeder.taskFeederCallBack taskFeederCallBack, Context context, String task, String description,
                      String date, String time, Status status, long position, byte[] calender_instance, int task_status) {
        this.taskFeederCallBack = taskFeederCallBack;
        this.context = context;
        this.task = task;
        this.description = description;
        this.date = date;
        this.time = time;
        this.status = status;
        this.position = position;
        this.task_status = task_status;
        this.calender_instance = calender_instance;
    }

    interface taskFeederCallBack
    {

       void feedConfirmation(boolean status,boolean divider);

    }

    @Override
    public void run() {

         switch (status)
         {

             case Insert:
                 ContentResolver contentResolver=context.getContentResolver();
                 ContentValues contentValues=new ContentValues();
                 contentValues.put(TaskTable.task_name,task);
                 contentValues.put(TaskTable.task_description,description);
                 contentValues.put(TaskTable.task_date, date);
                 contentValues.put(TaskTable.task_time,time);
                 contentValues.put(TaskTable.task_notification_information,calender_instance);
                 contentValues.put(TaskTable.status,task_status);
                 contentResolver.insert(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),contentValues);

                 if(taskFeederCallBack!=null)
                 {

                     taskFeederCallBack.feedConfirmation(true,false);

                 }

                 break;

             case Update:

                 ContentResolver contentResolver1=context.getContentResolver();
                 ContentValues contentValues1=new ContentValues();
                 contentValues1.put(TaskTable.task_name,task);
                 contentValues1.put(TaskTable.task_description,description);
                 contentValues1.put(TaskTable.task_date, date);
                 contentValues1.put(TaskTable.task_time,time);
                 contentValues1.put(TaskTable.task_notification_information,calender_instance);
                 contentValues1.put(TaskTable.status,task_status);
                 Uri uri=Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name);
                 contentResolver1.update(ContentUris.withAppendedId(uri,position),
                         contentValues1,null,null);

                 if(taskFeederCallBack!=null)
                 {

                     taskFeederCallBack.feedConfirmation(true,false);

                 }
                 break;

             case Delete:
                 ContentResolver contentResolver2=context.getContentResolver();
                 Uri uri1=Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name);
                 contentResolver2.delete(ContentUris.withAppendedId(uri1,position),null,null);
                 if(taskFeederCallBack!=null)
                 {

                     taskFeederCallBack.feedConfirmation(true,false);

                 }
                 break;

             case DeleteAll:

                 ContentResolver contentResolver3=context.getContentResolver();
                 contentResolver3.delete(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),null,null);

                 if(taskFeederCallBack!=null)
                 {

                     taskFeederCallBack.feedConfirmation(true,true);

                 }

                 break;
         }
    }
}
