package com.example.todo;

import android.Manifest;
import android.app.Activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends Fragment {

    private static final String TAG = "Profile";
    private ImageView imageView;
    private final CallbackProfile callbackProfile;
    private boolean uploaded_image=false;
    private byte[] image_url;
    private String userName;
    private final int callGallery=2;

    interface CallbackProfile
    {
        void profileConfirmation(boolean confirmation);
    }

    public Profile(CallbackProfile callbackProfile) {

     this.callbackProfile=callbackProfile;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView sign_up=view.findViewById(R.id.sign_up);
        ImageView camera=view.findViewById(R.id.camera);
        EditText input=view.findViewById(R.id.input);
        ImageView go=view.findViewById(R.id.go);
        imageView=view.findViewById(R.id.user_selected_image);

        Animation top,bottom;

        top= AnimationUtils.loadAnimation(getContext(),R.anim.top_mover);
        bottom=AnimationUtils.loadAnimation(getContext(),R.anim.bottom_mover);

        sign_up.setAnimation(top);
        camera.setAnimation(top);
        input.setAnimation(top);
        imageView.setAnimation(top);
        go.setAnimation(bottom);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int permission= ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

                if(permission!= PackageManager.PERMISSION_GRANTED)
                {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


                }
                else
                {
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");

                    startActivityForResult(intent,callGallery);

                }
            }
        });


        go.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {

                EditText editText=(EditText) view.findViewById(R.id.input);

                if(editText.getText().toString().length()==0)
                {
                   editText.setError("kindly enter your name");

                }
                else if(editText.getText().toString().length()>16)
                {
                    editText.setError("name is too large");
                }
                else if(profile_name_checker(editText.getText().toString()))
                {
                    editText.setError("spacial characters not allowed");

                }
                else
                {
                    editText.setError(null);
                    userName=editText.getText().toString();
                    uploading_data();
                }

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean profile_name_checker(String value)
    {
        boolean result=false;
        Pattern pattern=Pattern.compile("[^a-zA-Z\\s]");
        Matcher matcher=pattern.matcher(value);

        while (matcher.find())
        {
            result=true;

        }
        return result;
    }

     @RequiresApi(api = Build.VERSION_CODES.R)
     public final void uploading_data()
     {

         if(!uploaded_image)
         {
            image_url="noImageUploaded".getBytes();

         }

         ContentResolver contentResolver=getContext().getContentResolver();
         ContentValues contentValues=new ContentValues();
         contentValues.put(ProfileTable.profile_name,userName);
         contentValues.put(ProfileTable.profile_image,image_url);
         contentResolver.insert(Uri.withAppendedPath(DatabaseProvider.content_uri,ProfileTable.table_name),contentValues);

          if(callbackProfile!=null)
          {
              callbackProfile.profileConfirmation(true);
          }
     }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent, callGallery);


            } else {

                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }
                else
                {
                    Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    getContext().startActivity(intent);

                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: ends");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == callGallery && resultCode == Activity.RESULT_OK)
        {
            assert data != null;
            Uri image=data.getData();

            ContentResolver contentResolver=getContext().getContentResolver();
            Cursor cursor=contentResolver.query(image,null,null,null,null);

            if(cursor!=null)
            {
                while (cursor.moveToNext())
                {

                    File file=new File(cursor.getString(cursor.getColumnIndex("_data")));

                    try (FileInputStream fileInputStream = new FileInputStream(file)) {

                        byte[] imageByte=new byte[fileInputStream.available()];

                        fileInputStream.read(imageByte);
                        fileInputStream.close();
                        image_url=imageByte;
                        uploaded_image=true;

                        Bitmap bitmap= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                        RoundedBitmapDrawable roundedBitmapDrawable=RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                        roundedBitmapDrawable.setCircular(true);
                        imageView.setBackground(null);
                        imageView.setImageDrawable(roundedBitmapDrawable);


                    } catch (IOException e)
                    {
                        Log.e(TAG, "onActivityResult: unable to read: "+e.getMessage());

                    }
                }

                cursor.close();

            }

        }
    }
}