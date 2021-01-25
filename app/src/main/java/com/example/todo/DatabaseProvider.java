package com.example.todo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class DatabaseProvider extends ContentProvider {

    private static final String TAG = "DatabaseProvider";

    private SQLiteDatabase sqLiteDatabase;
    private static final String package_name="com.example.todo";
    public static final Uri content_uri=Uri.parse("content://"+package_name);
    private final UriMatcher uriMatcher=getUriMatcher();

    public UriMatcher getUriMatcher()
    {

       UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(package_name,ProfileTable.table_name,1);
        uriMatcher.addURI(package_name,TaskTable.table_name,2);
        uriMatcher.addURI(package_name,TaskTable.table_name + "/#",3);
        uriMatcher.addURI(package_name,ProfileTable.table_name + "/#",4);
         return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        Database database=new Database(getContext());
        sqLiteDatabase=database.getWritableDatabase();
        return sqLiteDatabase!=null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

         int matcher=uriMatcher.match(uri);
        SQLiteQueryBuilder sqLiteQueryBuilder=new SQLiteQueryBuilder();
         switch (matcher)
         {
             case 1:

              sqLiteQueryBuilder.setTables(ProfileTable.table_name);

                 return sqLiteQueryBuilder.query(sqLiteDatabase,strings,s,strings1,null,null,null);
             case 2:
                 sqLiteQueryBuilder.setTables(TaskTable.table_name);
                 String order=TaskTable.task_date;
                 return sqLiteQueryBuilder.query(sqLiteDatabase,strings,s,strings1,null,null,order);

             default:
                 throw new IllegalArgumentException("wrong uri passed");
         }

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int matcher=uriMatcher.match(uri);
        switch (matcher)
        {
            case 1:

                 sqLiteDatabase.insert(ProfileTable.table_name,null,contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                 break;

            case 2:
                sqLiteDatabase.insert(TaskTable.table_name,null,contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                break;

            default:
                throw new IllegalArgumentException("wrong uri passed");
        }

     return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int matcher=uriMatcher.match(uri);
        switch (matcher)
        {
            case 1:
                int result= sqLiteDatabase.delete(ProfileTable.table_name,s,strings);
                getContext().getContentResolver().notifyChange(uri,null);
                return result;
            case 2:
                  result= sqLiteDatabase.delete(TaskTable.table_name,s,strings);
                 getContext().getContentResolver().notifyChange(uri,null);
                return result;
            case 3:
                long uri_id= ContentUris.parseId(uri);
                String query;
                query=TaskTable.task_id+"="+uri_id;
                 if(s!=null && s.length()>0)
                 {

                     query+=" AND ("+ s + ")";

                 }

                 result=sqLiteDatabase.delete(TaskTable.table_name,query,strings);
                getContext().getContentResolver().notifyChange(uri,null);
                 return result;

            default:
                throw new IllegalArgumentException("wrong uri passed");
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        int matcher=uriMatcher.match(uri);
        switch (matcher)
        {
            case 1:

                int result= sqLiteDatabase.update(TaskTable.table_name,contentValues,s,strings);
                getContext().getContentResolver().notifyChange(uri,null);
                return result;

                case 3:

                long uri_id= ContentUris.parseId(uri);
                String query;
                query=TaskTable.task_id + " = " + uri_id;
                if(s!=null && s.length()>0)
                {

                    query+=" AND ("+ s + ")";

                }

                result=sqLiteDatabase.update(TaskTable.table_name,contentValues,query,strings);
                getContext().getContentResolver().notifyChange(uri,null);
                return result;

            case 4:
                long uriId= ContentUris.parseId(uri);
                String query1=ProfileTable.profile_id+"="+uriId;
                if(s!=null && s.length()>0)
                {

                    query1+=" AND ("+ s + ")";

                }

                result=sqLiteDatabase.update(ProfileTable.table_name,contentValues,query1,strings);
                getContext().getContentResolver().notifyChange(uri,null);
                return result;


            default:
                 throw new IllegalArgumentException("wrong uri passed");
        }

    }
}
