package com.example.todo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

public class ProfileDataUploader implements Runnable {
    private static final String TAG = "ProfileDataUploader";

    private final ProfileDataCallBack profileDataCallBack;
    private final Context context;
    private String user_name;
    private byte[] user_image;
    private final ProfileStatus profileStatus;
    private final Bundle bundle;

    enum ProfileStatus
    {
        All,Update,Edition;
    }

    public ProfileDataUploader(ProfileDataCallBack profileDataCallBack, Context context, ProfileStatus profileStatus, Bundle bundle) {
        this.profileDataCallBack = profileDataCallBack;
        this.context = context;
        this.profileStatus=profileStatus;
        this.bundle=bundle;
    }

    interface ProfileDataCallBack
    {

       void profileData(String name,byte[] image_uri,ProfileStatus profileStatus);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {

        switch (profileStatus)
        {

            case All:

                ContentResolver contentResolver=context.getContentResolver();
                Cursor cursor=contentResolver.query(Uri.withAppendedPath(DatabaseProvider.content_uri,ProfileTable.table_name)
                        ,new String[]{ProfileTable.profile_name,ProfileTable.profile_image},null,null);

                if(cursor!=null)
                {

                    while (cursor.moveToNext())
                    {

                        user_name=cursor.getString(cursor.getColumnIndex(ProfileTable.profile_name));
                        user_image=cursor.getBlob(cursor.getColumnIndex(ProfileTable.profile_image));

                    }

                    cursor.close();
                }

                if(profileDataCallBack!=null)
                {

                    profileDataCallBack.profileData(user_name,user_image,null);

                }

                break;

            case Edition:
                ContentResolver contentResolver2=context.getContentResolver();
                Cursor cursor2=contentResolver2.query(Uri.withAppendedPath(DatabaseProvider.content_uri,ProfileTable.table_name)
                        ,new String[]{ProfileTable.profile_name,ProfileTable.profile_image},null,null);

                if(cursor2!=null)
                {

                    while (cursor2.moveToNext())
                    {

                        user_name=cursor2.getString(cursor2.getColumnIndex(ProfileTable.profile_name));
                        user_image=cursor2.getBlob(cursor2.getColumnIndex(ProfileTable.profile_image));

                    }

                    cursor2.close();
                }

                if(profileDataCallBack!=null)
                {

                    profileDataCallBack.profileData(user_name,user_image,ProfileStatus.Edition);

                }

                break;

            case Update:

                String user_name=bundle.getString(ProfileTable.profile_name);
                byte[] user_image=bundle.getByteArray(ProfileTable.profile_image);

                ContentValues contentValues=new ContentValues();
                contentValues.put(ProfileTable.profile_name,user_name);
                contentValues.put(ProfileTable.profile_image,user_image);

                ContentResolver contentResolver1=context.getContentResolver();
                Uri uri=Uri.withAppendedPath(DatabaseProvider.content_uri,ProfileTable.table_name);
                contentResolver1.update(ContentUris.withAppendedId(uri,1),contentValues,null,null);

                Cursor cursor1=contentResolver1.query(Uri.withAppendedPath(DatabaseProvider.content_uri,ProfileTable.table_name)
                        ,new String[]{ProfileTable.profile_name,ProfileTable.profile_image},null,null);

                if(cursor1!=null)
                {

                    while (cursor1.moveToNext())
                    {

                        user_name=cursor1.getString(cursor1.getColumnIndex(ProfileTable.profile_name));
                        user_image=cursor1.getBlob(cursor1.getColumnIndex(ProfileTable.profile_image));

                    }

                    cursor1.close();
                }

                if(profileDataCallBack!=null)
                {

                    profileDataCallBack.profileData(user_name,user_image,null);

                }

                break;

        }

    }
}
