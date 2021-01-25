package com.example.todo;

import java.io.Serializable;

public class Tasks implements Serializable {

    private final String task_name;
    private final String task_description;
    private final String task_date;
    private final String task_time;
    private final int task_id;

    public Tasks(String task_name, String task_description, String task_date, String task_time, int task_id) {
        this.task_name = task_name;
        this.task_description = task_description;
        this.task_date = task_date;
        this.task_time = task_time;
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public String getTask_description() {
        return task_description;
    }

    public String getTask_date() {
        return task_date;
    }
    public int getTask_id() {
        return task_id;
    }

    public String getTask_time() {
        return task_time;
    }
}
