package com.philips.reactivesample.ui.main.utility;

import com.philips.reactivesample.ui.main.model.Task;

import java.util.ArrayList;
import java.util.List;

public class DataSource {

    public static List<Task> getTasks(){
        List<Task> k =  new ArrayList<>();
        k.add(new Task("Task1", 1));
        k.add(new Task("Task2", 2));
        k.add(new Task("Task3", 3));
        k.add(new Task("Task4", 4));
        k.add(new Task("Task5", 5));
        return k;
    }
}
