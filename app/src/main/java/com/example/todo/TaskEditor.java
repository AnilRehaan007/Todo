package com.example.todo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TaskEditor extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener,TaskFeeder.taskFeederCallBack {
    private static final String TAG = "TaskEditor";
    private boolean isDatePicked=false;
    private boolean isTimePicked=false;
    private String task_name;
    private String task_description;
    private  String selected_date;
    private String selected_time;
    private boolean task_confirmation=false;
    private EditText task;
    private  EditText description;
    private int position=0;
    private boolean updater_status=false;
    private final Calendar calendar=Calendar.getInstance();
    private final Handler handler=new Handler();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);
        TextView date_setting=findViewById(R.id.date);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate=LocalDate.now();
        date_setting.setText(dateTimeFormatter.format(localDate));
        task=findViewById(R.id.enter_task3);
        description=findViewById(R.id.enter_description3);

        Bundle bundle=getIntent().getExtras();

         if(bundle!=null)
         {
             updater_status=true;

            Tasks tasks=(Tasks)bundle.getSerializable(Tasks.class.getSimpleName());
            position=bundle.getInt("position");
            task.setText(tasks.getTask_name());
            description.setText(tasks.getTask_description());
         }

        (findViewById(R.id.datePicker)).setOnClickListener(view -> {
            DialogFragment dialogFragment=new com.example.todo.DatePicker();
            dialogFragment.show(getSupportFragmentManager(),"date_picker");
        });

        (findViewById(R.id.timePicker)).setOnClickListener(view -> {

            DialogFragment dialogFragment=new com.example.todo.TimePicker();
            dialogFragment.show(getSupportFragmentManager(),"timePicker");

        });


        (findViewById(R.id.task_recorder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
               intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");


                try {

                   startActivityForResult(intent,1);

                }catch (Exception e)
                {

                    Toast.makeText(TaskEditor.this,"Your Device Does'nt support Voice Recognition",Toast.LENGTH_LONG).show();

                }

            }
        });

        (findViewById(R.id.description_recorder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");


                try {

                    startActivityForResult(intent,2);

                }catch (Exception e)
                {

                    Toast.makeText(TaskEditor.this,"Your Device Does'nt support Voice Recognition",Toast.LENGTH_LONG).show();

                }


            }
        });

        (findViewById(R.id.save)).setOnClickListener(view -> {

            ExecutorService executorService= Executors.newFixedThreadPool(2);
            if(task.getText().toString().length()==0)
            {
                task.setError("Enter Task Name");
            }
            else
            {
                task.setError(null);
                task_confirmation=true;
                task_name=task.getText().toString();
            }

          if(description.getText().toString().length()==0)
          {
              task_description="No Description";
          }
          else
          {
             task_description=description.getText().toString();

          }

          if(!isTimePicked)
          {

              calendar.set(Calendar.HOUR,7);
              calendar.set(Calendar.MINUTE,0);
              calendar.set(Calendar.SECOND,0);
              calendar.set(Calendar.AM_PM,0);
              selected_time="NO TIME";
          }

          if(task_confirmation)
          {

              if(isDatePicked)
              {

                  Runnable[] processing;

                  if(updater_status)
                 {

                     try {
                         processing = new Runnable[]{new TaskFeeder(this,getBaseContext(),task_name,task_description,selected_date,
                                 selected_time, TaskFeeder.Status.Update,position,calenderInstance(),0)};

                         for (Runnable runnable : processing) {

                             executorService.execute(runnable);

                         }
                     } catch (Exception e) {

                         Log.e(TAG, "onCreate: unable to get byte array:"+ e.getMessage());
                     }

                 }
                 else
                 {
                     try {
                         processing = new Runnable[]{new TaskFeeder(this,getBaseContext(),task_name,task_description,selected_date,
                                 selected_time, TaskFeeder.Status.Insert,position,calenderInstance(),0)};

                         for (Runnable runnable : processing) {

                             executorService.execute(runnable);

                         }
                     } catch (Exception e) {

                         Log.e(TAG, "onCreate: unable to get byte array:"+ e.getMessage());
                     }

                 }
                  executorService.shutdown();

              }
              else
              {

                  Dialog dialog=new Dialog(TaskEditor.this);
                  dialog.setContentView(R.layout.pickdate);
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
                      }
                  },1000);


              }


          }


        });

    }


    public final byte[] calenderInstance() throws Exception
    {

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(calendar);

        return byteArrayOutputStream.toByteArray();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK)
        {

            ArrayList<String> voiceValue=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            task_name=voiceValue.get(0);

            task.setText(task_name);

            task_confirmation=true;
        }

        else if(requestCode==2 && resultCode==RESULT_OK)
        {
            ArrayList<String> voiceValue=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            task_description=voiceValue.get(0);

            description.setText(task_description);

        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

          calendar.set(Calendar.MONTH,month);
          calendar.set(Calendar.YEAR,year);
          calendar.set(Calendar.DAY_OF_MONTH,day);

          String month1=String.valueOf(month+1);
          String day1=String.valueOf(day);

           if(month1.length()==1)
           {
              month1="0"+month1;

           }
           if(day1.length()==1)
           {
               day1="0"+day1;
           }

         selected_date=year+"-"+month1+"-"+day1;

         isDatePicked=true;

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);

        if (calendar.get(Calendar.AM_PM) == Calendar.AM)
        {
            calendar.set(Calendar.AM_PM,0);
        }
        else if (calendar.get(Calendar.AM_PM) == Calendar.PM)
        {
            calendar.set(Calendar.AM_PM,1);
        }

        selected_time=hour+":"+minute;
       isTimePicked=true;

    }

    @Override
    public void feedConfirmation(boolean status,boolean divider) {

        handler.post(() -> {

            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.datasaveddialog);
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
                Intent intent=new Intent(TaskEditor.this,MainActivity.class);
                startActivity(intent);
            }
        },1000);

        });

    }

    @Override
    public void onBackPressed() {

         if(task.getText().toString().length()!=0 || description.getText().toString().length()!=0 || isTimePicked || isDatePicked)
         {
             AlertDialog.Builder builder=new AlertDialog.Builder(TaskEditor.this,R.style.dialog_theme)
                     .setMessage("Do You Want To Leave This Page").setNegativeButton("LEAVE", (dialogInterface, i) -> {
                         super.onBackPressed();

                     }).setPositiveButton("STAY", (dialogInterface, i) -> {

                         Log.d(TAG, "onBackPressed: stay pressed");
                     });
             builder.setCancelable(false);
             builder.show();
         }

          else
         {
             super.onBackPressed();

         }


    }
}


