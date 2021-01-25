package com.example.todo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class TaskUploader implements Runnable {
    private static final String TAG = "TaskUploader";

    private final TaskCallBack taskCallBack;
    private final Context context;
    private final List<Tasks> tasks;
    private final Filter filter;

    enum Filter
    {
       All, TODAY, WEEK, MONTH;

    }

    public TaskUploader(TaskCallBack taskCallBack, Context context, List<Tasks> tasks, Filter filter) {
        this.taskCallBack = taskCallBack;
        this.context = context;
        this.tasks = tasks;
        this.filter = filter;
    }

    interface TaskCallBack {

        void TaskData(List<Tasks> tasks,Filter filter);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {

        ContentResolver contentResolver;
        Cursor cursor;
        String selection;

         switch (filter)
         {
             case All:

                 contentResolver = context.getContentResolver();
                 cursor = contentResolver.query(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),null
                         ,null, null, null);

                 if (cursor != null) {

                     while (cursor.moveToNext()) {

                         String task = cursor.getString(cursor.getColumnIndex(TaskTable.task_name));
                         String description = cursor.getString(cursor.getColumnIndex(TaskTable.task_description));
                         String date = cursor.getString(cursor.getColumnIndex(TaskTable.task_date));
                         int task_id=cursor.getInt(cursor.getColumnIndex(TaskTable.task_id));
                         String time=cursor.getString(cursor.getColumnIndex(TaskTable.task_time));
                         tasks.add(new Tasks(task,description,date,time,task_id));

                     }

                     cursor.close();

                     if (taskCallBack != null) {

                         taskCallBack.TaskData(tasks,Filter.All);

                     }
                 }

                 break;

             case TODAY:

                 contentResolver= context.getContentResolver();
                 selection=TaskTable.task_date+"=date('now')";
                   cursor = contentResolver.query(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),null
                         ,selection, null, null);

                 if (cursor != null) {

                     while (cursor.moveToNext()) {

                         String task = cursor.getString(cursor.getColumnIndex(TaskTable.task_name));
                         String description = cursor.getString(cursor.getColumnIndex(TaskTable.task_description));
                         String date = cursor.getString(cursor.getColumnIndex(TaskTable.task_date));
                         int task_id=cursor.getInt(cursor.getColumnIndex(TaskTable.task_id));
                         String time=cursor.getString(cursor.getColumnIndex(TaskTable.task_time));
                         tasks.add(new Tasks(task,description,date,time,task_id));

                     }

                     cursor.close();

                     if (taskCallBack != null) {

                         taskCallBack.TaskData(tasks,Filter.TODAY);

                     }
                 }
                 break;

             case WEEK:
                 contentResolver= context.getContentResolver();
                 selection=TaskTable.task_date+">=date('now') and "+TaskTable.task_date+"<=date('now','+1 month','-27 day')";
                 cursor = contentResolver.query(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),null
                         ,selection, null, null);

                 if (cursor != null) {

                     while (cursor.moveToNext()) {

                         String task = cursor.getString(cursor.getColumnIndex(TaskTable.task_name));
                         String description = cursor.getString(cursor.getColumnIndex(TaskTable.task_description));
                         String date = cursor.getString(cursor.getColumnIndex(TaskTable.task_date));
                         int task_id=cursor.getInt(cursor.getColumnIndex(TaskTable.task_id));
                         String time=cursor.getString(cursor.getColumnIndex(TaskTable.task_time));
                         tasks.add(new Tasks(task,description,date,time,task_id));

                     }

                     cursor.close();

                     if (taskCallBack != null) {

                         taskCallBack.TaskData(tasks,Filter.WEEK);

                     }
                 }

                 break;

             case MONTH:
                 contentResolver= context.getContentResolver();
                 selection=TaskTable.task_date+">=date('now') and "+TaskTable.task_date+"<=date('now','+1 month')";
                 cursor = contentResolver.query(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),null
                         ,selection, null, null);

                 if (cursor != null) {

                     while (cursor.moveToNext()) {

                         String task = cursor.getString(cursor.getColumnIndex(TaskTable.task_name));
                         String description = cursor.getString(cursor.getColumnIndex(TaskTable.task_description));
                         String date = cursor.getString(cursor.getColumnIndex(TaskTable.task_date));
                         int task_id=cursor.getInt(cursor.getColumnIndex(TaskTable.task_id));
                         String time=cursor.getString(cursor.getColumnIndex(TaskTable.task_time));
                         tasks.add(new Tasks(task,description,date,time,task_id));

                     }

                     cursor.close();

                     if (taskCallBack != null) {

                         taskCallBack.TaskData(tasks,Filter.MONTH);

                     }
                 }
                 break;

         }
    }
}
