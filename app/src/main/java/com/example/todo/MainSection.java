package com.example.todo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainSection extends Fragment implements ProfileDataUploader.ProfileDataCallBack,
        TaskUploader.TaskCallBack,RecycleViewAdaptor.editAndDeleteCallBack,TaskFeeder.taskFeederCallBack
{
    private View mainView;
    private RecycleViewAdaptor recycleViewAdaptor;
    private static final String TAG = "MainSection";
    private final Handler handler=new Handler();
    private boolean today_button_status=false;
    private boolean week_button_status=false;
    private boolean month_button_status=false;
    private boolean uploaded_image=false;
    private byte[] image_url;
    private String userName;
    private ImageView edit_image;
    private EditText editText;
    private TextView today;
    private TextView week;
    private TextView month;
    private final int callGallery=1;
    public MainSection() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_section, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

          mainView=view;
          ExecutorService executorService=Executors.newFixedThreadPool(4);
          Runnable[] processing={new ProfileDataUploader(this,getContext(), ProfileDataUploader.ProfileStatus.All,null),
                  new TaskUploader(this,getContext(),new ArrayList<>(), TaskUploader.Filter.All)};

         if(data_base_status())
         {

            ImageView bottom_button=view.findViewById(R.id.bottom_add_task);
            bottom_button.setVisibility(View.INVISIBLE);

         }

         else
         {
             ImageView upper_tab_add_button=view.findViewById(R.id.first_tab_add3);
            TextView upper_add_description_text=view.findViewById(R.id.tab_to_add_task);
             ImageView bottom_button=view.findViewById(R.id.bottom_add_task);
            upper_tab_add_button.setVisibility(View.INVISIBLE);
            upper_add_description_text.setVisibility(View.INVISIBLE);
             bottom_button.setVisibility(View.VISIBLE);

         }

               today=view.findViewById(R.id.today_button3);
                week=view.findViewById(R.id.week_button3);
               month=view.findViewById(R.id.month_button3);

        RecyclerView recyclerView=view.findViewById(R.id.task_list_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleViewAdaptor=new RecycleViewAdaptor(new ArrayList<>(),this);
        recyclerView.setAdapter(recycleViewAdaptor);

        for (Runnable runnable : processing) {

            executorService.execute(runnable);
        }
         executorService.shutdown();

        (view.findViewById(R.id.bottom_add_task)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 MainActivity.editorCaller=true;

                Intent intent=new Intent(getContext(),TaskEditor.class);
                startActivity(intent);
            }
        });
        (view.findViewById(R.id.first_tab_add3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 MainActivity.editorCaller=true;

                Intent intent=new Intent(getContext(),TaskEditor.class);
                startActivity(intent);
            }
        });

        (view.findViewById(R.id.touch3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog=new Dialog(getContext(),R.style.translate_animator);
                dialog.setContentView(R.layout.touch_click);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                Window window=dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                dialog.show();

                (dialog.findViewById(R.id.minimize2)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.callBackFromWhere=ServiceConfirmation.touch;
                        Intent intent=new Intent(getContext(),MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                (dialog.findViewById(R.id.edit_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       new Thread(new ProfileDataUploader(MainSection.this,getContext(),
                               ProfileDataUploader.ProfileStatus.Edition,null)).start();
                       dialog.dismiss();
                    }
                });

                (dialog.findViewById(R.id.clear_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                         dialog.dismiss();
                        Dialog dialog1=new Dialog(getContext());
                        dialog1.setContentView(R.layout.clear_data_dialog);
                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog1.setCanceledOnTouchOutside(false);
                        Window window=dialog1.getWindow();
                        window.setGravity(Gravity.CENTER);
                        dialog1.show();

                       dialog1.findViewById(R.id.minimize2).setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               dialog1.dismiss();
                           }
                       });

                      dialog1.findViewById(R.id.account_delete_button).setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                                 MainActivity.callBackFromWhere=ServiceConfirmation.clear_data;
                               new Thread(new TaskFeeder(MainSection.this,getContext(),null,
                                       null,null,null, TaskFeeder.Status.DeleteAll,0,new byte[]{},0)).start();
                            dialog1.dismiss();
                          }
                      });

                    }
                });

                (dialog.findViewById(R.id.delete_account_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                         dialog.dismiss();

                        Dialog dialog2=new Dialog(getContext());
                        dialog2.setContentView(R.layout.permanent_delete_box);
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog2.setCanceledOnTouchOutside(false);
                        Window window=dialog2.getWindow();
                        window.setGravity(Gravity.CENTER);
                        dialog2.show();

                       dialog2.findViewById(R.id.minimize2).setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {

                               dialog2.dismiss();
                           }
                       });

                      dialog2.findViewById(R.id.account_delete_button).setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {

                              MainActivity.callBackFromWhere=ServiceConfirmation.delete_account;

                               new Thread(new Runnable() {
                                   @Override
                                   public void run() {

                                       if(getContext()!=null)
                                       {
                                           ContentResolver contentResolver=getContext().getContentResolver();
                                           contentResolver.delete(Uri.withAppendedPath(DatabaseProvider.content_uri,ProfileTable.table_name),
                                                   null,null);
                                           contentResolver.delete(Uri.withAppendedPath(DatabaseProvider.content_uri,TaskTable.table_name),
                                                   null,null);
                                       }

                                      handler.post(new Runnable() {
                                          @Override
                                          public void run() {

                                              Dialog dialog3=new Dialog(getContext());
                                              dialog3.setContentView(R.layout.account_delete_box);
                                              dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                              dialog3.setCanceledOnTouchOutside(false);
                                              Window window=dialog3.getWindow();
                                              window.setGravity(Gravity.CENTER);
                                              dialog3.show();

                                              Timer timer=new Timer();
                                              timer.schedule(new TimerTask() {
                                                  @Override
                                                  public void run() {
                                                      dialog3.dismiss();
                                                      timer.cancel();
                                                      Intent intent=new Intent(getContext(),MainActivity.class);
                                                      startActivity(intent);
                                                  }
                                              },1000);

                                          }
                                      });


                                   }
                               }).start();

                              dialog2.dismiss();

                          }
                      });
                    }
                });

            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(week_button_status)
                {
                   week.setBackgroundResource(0);
                    week_button_status=false;
                }
                else if(month_button_status)
                {
                    month.setBackgroundResource(0);
                    month_button_status=false;
                }

                today.setBackgroundResource(R.drawable.onclickbutton);
                today_button_status=true;

               new Thread(new TaskUploader(MainSection.this,getContext(),new ArrayList<>(),TaskUploader.Filter.TODAY)).start();
            }
        });

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(today_button_status)
                {
                    today.setBackgroundResource(0);
                    today_button_status=false;
                }
                else if(month_button_status)
                {
                   month.setBackgroundResource(0);
                   month_button_status=false;
                }

                week.setBackgroundResource(R.drawable.onclickbutton);
                week_button_status=true;

                new Thread(new TaskUploader(MainSection.this,getContext(),new ArrayList<>(), TaskUploader.Filter.WEEK)).start();
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 if(today_button_status)
                 {

                     today.setBackgroundResource(0);
                     today_button_status=false;
                 }
                 else if(week_button_status)
                 {

                     week.setBackgroundResource(0);
                     week_button_status=false;

                 }

                 month.setBackgroundResource(R.drawable.onclickbutton);
                 month_button_status=true;


                new Thread(new TaskUploader(MainSection.this,getContext(),new ArrayList<>(), TaskUploader.Filter.MONTH)).start();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public final boolean data_base_status()
    {
        boolean status=true;

        SQLiteDatabase sqLiteDatabase=getContext().openOrCreateDatabase("Todo", Context.MODE_PRIVATE,null);

        Cursor cursor=sqLiteDatabase.rawQuery("select count(*) from "+TaskTable.table_name,null);

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
    public void profileData(String name, byte[] image_uri, ProfileDataUploader.ProfileStatus profileStatus) {

        if(profileStatus==null)
        {
            handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {

                    ImageView user_image=mainView.findViewById(R.id.user_image);
                    TextView user_name=mainView.findViewById(R.id.profile_name2);
                    TextView date_setting=mainView.findViewById(R.id.date2);
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate localDate=LocalDate.now();
                    date_setting.setText(dateTimeFormatter.format(localDate));

                    try {

                        Bitmap bitmap;

                        if(new String(image_uri).equals("noImageUploaded"))
                        {
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user);
                            RoundedBitmapDrawable roundedBitmapDrawable=RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                            roundedBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(roundedBitmapDrawable);
                            user_name.setText(name);
                        }
                        else
                        {
                            user_name.setText(name);

                            Bitmap bitmap1= BitmapFactory.decodeByteArray(image_uri,0,image_uri.length);
                            RoundedBitmapDrawable roundedBitmapDrawable=RoundedBitmapDrawableFactory.create(getResources(),bitmap1);
                            roundedBitmapDrawable.setCircular(true);
                            user_image.setBackground(null);
                            user_image.setImageDrawable(roundedBitmapDrawable);


                        }

                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "profileData: unable to upload image "+ e.getMessage());
                    }

                }
            });

        }
        else
        {

          handler.post(new Runnable() {
              @Override
              public void run() {

                  Dialog dialog=new Dialog(getContext());
                  dialog.setContentView(R.layout.profile_edit_box);
                  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                  dialog.setCanceledOnTouchOutside(false);
                  Window window=dialog.getWindow();
                  window.setGravity(Gravity.CENTER);
                  dialog.show();
                  dialog.findViewById(R.id.minimize2).setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {

                          dialog.dismiss();
                      }
                  });

                  dialog.findViewById(R.id.edit_camera).setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {

                          int permission= ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

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
                   edit_image=dialog.findViewById(R.id.profile_edit_image);
                   editText=dialog.findViewById(R.id.update_name);

                  dialog.findViewById(R.id.account_delete_button).setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {

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

                              if(!uploaded_image)
                              {
                                  image_url="noImageUploaded".getBytes();

                              }

                              Bundle bundle=new Bundle();
                              bundle.putString(ProfileTable.profile_name,userName);
                             bundle.putByteArray(ProfileTable.profile_image,image_url);
                              new Thread(new ProfileDataUploader(MainSection.this,getContext(),
                                      ProfileDataUploader.ProfileStatus.Update,bundle)).start();
                              dialog.dismiss();
                          }



                      }
                  });

                  try {

                      Bitmap bitmap;

                      if(new String(image_uri).equals("noImageUploaded"))
                      {
                          bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user);
                          RoundedBitmapDrawable roundedBitmapDrawable=RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                          roundedBitmapDrawable.setCircular(true);
                          edit_image.setImageDrawable(roundedBitmapDrawable);
                          editText.setText(name);
                      }
                      else
                      {
                          editText.setText(name);

                          Bitmap bitmap1= BitmapFactory.decodeByteArray(image_uri,0,image_uri.length);
                          RoundedBitmapDrawable roundedBitmapDrawable=RoundedBitmapDrawableFactory.create(getResources(),bitmap1);
                          roundedBitmapDrawable.setCircular(true);
                          edit_image.setImageDrawable(roundedBitmapDrawable);
                      }

                  }
                  catch (Exception e)
                  {
                      Log.e(TAG, "profileData: unable to upload image "+ e.getMessage());
                  }

              }
          });



        }
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
                        edit_image.setImageDrawable(roundedBitmapDrawable);


                    } catch (IOException e)
                    {
                        Log.e(TAG, "onActivityResult: unable to read: "+e.getMessage());

                    }
                }

                cursor.close();

            }

        }

    }

    @Override
    public void TaskData(List<Tasks> tasks, TaskUploader.Filter filter) {

         if(recycleViewAdaptor!=null)
         {
            if(tasks.isEmpty())
            {
               switch (filter)
               {
                   case TODAY:

                       handler.post(new Runnable() {
                           @Override
                           public void run() {

                               Dialog dialog=new Dialog(getContext());
                               dialog.setContentView(R.layout.task_detailsbox);
                               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                               dialog.setCanceledOnTouchOutside(false);
                               Window window=dialog.getWindow();
                               window.setGravity(Gravity.CENTER);
                               ((TextView)dialog.findViewById(R.id.status)).setText(TaskUploader.Filter.TODAY.toString());
                               dialog.show();
                               dialog.findViewById(R.id.cancel_confirmation).setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {

                                       today.setBackground(null);
                                       dialog.dismiss();
                                   }
                               });

                           }
                       });

                       break;

                   case WEEK:
                  handler.post(new Runnable() {
                      @Override
                      public void run() {

                          Dialog dialog1=new Dialog(getContext());
                          dialog1.setContentView(R.layout.task_detailsbox);
                          dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                          dialog1.setCanceledOnTouchOutside(false);
                          Window window1=dialog1.getWindow();
                          window1.setGravity(Gravity.CENTER);
                          ((TextView)dialog1.findViewById(R.id.status)).setText(TaskUploader.Filter.WEEK.toString());
                          dialog1.show();
                          dialog1.findViewById(R.id.cancel_confirmation).setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View view) {

                                 week.setBackground(null);
                                  dialog1.dismiss();
                              }
                          });


                      }
                  });
                       break;

                   case MONTH:
                       handler.post(new Runnable() {
                           @Override
                           public void run() {

                               Dialog dialog2=new Dialog(getContext());
                               dialog2.setContentView(R.layout.task_detailsbox);
                               dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                               dialog2.setCanceledOnTouchOutside(false);
                               Window window2=dialog2.getWindow();
                               window2.setGravity(Gravity.CENTER);
                               ((TextView)dialog2.findViewById(R.id.status)).setText(TaskUploader.Filter.MONTH.toString());
                               dialog2.show();
                               dialog2.findViewById(R.id.cancel_confirmation).setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {

                                       month.setBackground(null);
                                       dialog2.dismiss();
                                   }
                               });

                           }
                       });
                       break;

               }

            }

            else
            {

              handler.post(new Runnable() {
                  @Override
                  public void run() {

                      TextView textView=mainView.findViewById(R.id.task_information);
                      textView.setText((textView.getText().toString()).replaceAll("\\d",String.valueOf(tasks.size())));
                      recycleViewAdaptor.dataLoader(tasks);
                  }
              });
            }
         }


    }

        @Override
        public void editTask(Tasks tasks,int position) {

         MainActivity.editorCaller=true;

         Intent intent=new Intent(getContext(),TaskEditor.class);
         intent.putExtra(Tasks.class.getSimpleName(),tasks);
         intent.putExtra("position",position);
         startActivity(intent);
        }

        @Override
        public void deleteTask(Tasks tasks,int position) {

            Dialog dialog=new Dialog(getContext(),R.style.dialog_animation);
            dialog.setContentView(R.layout.deletetaskdialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            Window window=dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            dialog.show();

            ((TextView)(dialog.findViewById(R.id.taskname1))).setText(tasks.getTask_name());
            ((TextView)(dialog.findViewById(R.id.description1))).setText(tasks.getTask_description());
            String date_time=tasks.getTask_date()+"\t\t"+tasks.getTask_time();
            ((TextView)(dialog.findViewById(R.id.time1))).setText(date_time);

            dialog.findViewById(R.id.minimize2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.account_delete_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 MainActivity.callBackFromWhere=ServiceConfirmation.delete_task;
                    new Thread(new TaskFeeder(MainSection.this,getContext(),null,null,null,null,
                            TaskFeeder.Status.Delete,position,new byte[]{},0)).start();

                    dialog.dismiss();
                }
            });


        }

    @Override
    public void feedConfirmation(boolean status,boolean divider) {
       if(!divider)
       {
           handler.post(() -> {

               Dialog dialog=new Dialog(getContext());
               dialog.setContentView(R.layout.datadeletebox);
               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               dialog.setCanceledOnTouchOutside(false);
               Window window=dialog.getWindow();
               window.setGravity(Gravity.CENTER);
               dialog.show();

               Timer timer=new Timer();
               timer.schedule(new TimerTask() {
                   @Override
                   public void run() {

                       dialog.dismiss();
                       timer.cancel();
                       Intent intent=new Intent(getContext(),MainActivity.class);
                       startActivity(intent);
                   }
               },1000);

           });
       }
       else
       {
           handler.post(() -> {

               Dialog dialog=new Dialog(getContext());
               dialog.setContentView(R.layout.clear_delete_box);
               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               dialog.setCanceledOnTouchOutside(false);
               Window window=dialog.getWindow();
               window.setGravity(Gravity.CENTER);
               dialog.show();

               Timer timer=new Timer();
               timer.schedule(new TimerTask() {
                   @Override
                   public void run() {

                       dialog.dismiss();
                       timer.cancel();
                       Intent intent=new Intent(getContext(),MainActivity.class);
                       startActivity(intent);
                   }
               },1000);

           });



       }

    }
}
