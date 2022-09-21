package com.rocketflyer.rocketflow.ui.tasklisting;

import com.tracki.data.model.response.config.Task;

import java.util.Comparator;

/**
 * Created by Vikas Kesharvani on 24/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
public class TaskComparator implements Comparator<Task> {


    @Override
    public int compare(Task task, Task t1) {
        if (task.getCreatedAt() > t1.getCreatedAt()) return -1;
        if (task.getCreatedAt() < t1.getCreatedAt()) return 1;
        else return 0;
    }
}
