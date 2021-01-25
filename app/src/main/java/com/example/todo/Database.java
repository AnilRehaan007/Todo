package com.example.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private static final String TAG = "Database";
    public static final String data_base="Todo";
    public static final int version=1;

    public Database(@Nullable Context context) {
        super(context,data_base,null,version);
         this.getWritableDatabase();
        Log.d(TAG, "Database: called");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table "+ProfileTable.table_name+
                "("+ProfileTable.profile_id+" integer primary key,"+ProfileTable.profile_name+" text,"+ProfileTable.profile_image+" blob)");

        sqLiteDatabase.execSQL("create table "+TaskTable.table_name+"("+TaskTable.task_id+" integer primary key,"+TaskTable.task_name+" text," +
                ""+TaskTable.task_description+" text,"+TaskTable.task_date+" " +
                "date,"+TaskTable.task_time+" time,"+TaskTable.task_notification_information+" blob,"+TaskTable.status+" blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {



    }



}
