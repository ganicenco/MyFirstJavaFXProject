package com.example.myfirstjavafxproject;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private List<Task> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
    }
    public void addTask(Task newTask){
        tasks.add(newTask);
    }


    public void deleteTask(Task task){
        tasks.remove(task);
    }

    public List<Task> getTasks(){
        return tasks;
    }
}
