package com.example.todo;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleViewAdaptor extends RecyclerView.Adapter<RecycleViewAdaptor.Holder> {

    private static final String TAG = "RecycleViewAdaptor";
    private List<Tasks> tasks;
    private final editAndDeleteCallBack editAndDeleteCallBack;
    interface editAndDeleteCallBack
    {
        void editTask(Tasks tasks,int position);
        void deleteTask(Tasks tasks,int position);

    }

    public RecycleViewAdaptor(List<Tasks> tasks, RecycleViewAdaptor.editAndDeleteCallBack editAndDeleteCallBack) {
        this.tasks = tasks;
        this.editAndDeleteCallBack = editAndDeleteCallBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_holder,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

       holder.task_name.setText(tasks.get(position).getTask_name());
       holder.description.setText(tasks.get(position).getTask_description());
       String date_time=tasks.get(position).getTask_date()+"\t\t"+tasks.get(position).getTask_time();
       holder.date.setText(date_time);

        holder.edit.setOnClickListener(view -> {

          if(editAndDeleteCallBack!=null)
          {
             editAndDeleteCallBack.editTask(tasks.get(position),tasks.get(position).getTask_id());

          }

        });

        holder.delete.setOnClickListener(view -> {

            if(editAndDeleteCallBack!=null)
            {
                editAndDeleteCallBack.deleteTask(tasks.get(position),tasks.get(position).getTask_id());

            }

        });

    }

    @Override
    public int getItemCount() {
        return tasks!=null && tasks.size()!=0 ? tasks.size():0;
    }

     public final void dataLoader(List<Tasks> tasks)
     {
        this.tasks=tasks;
        notifyDataSetChanged();
     }

    static class Holder extends RecyclerView.ViewHolder
   {

     TextView task_name;
     TextView description;
     TextView date;
     ImageButton edit;
     ImageButton delete;

       public Holder(@NonNull View itemView) {
           super(itemView);

          task_name=itemView.findViewById(R.id.task_name);
          description=itemView.findViewById(R.id.description);
          date=itemView.findViewById(R.id.date_setting);
          edit=itemView.findViewById(R.id.edit_task);
          delete=itemView.findViewById(R.id.delete_task);
       }
   }

}
