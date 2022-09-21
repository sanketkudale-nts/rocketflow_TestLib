package com.rocketflyer.rocketflow.ui.tasklisting;

import com.tracki.data.model.response.config.Task;

public interface TaskItemClickListener {
    void onItemClick(Task task, int position);

    void onCallClick(String mobile);

    void onDetailsTaskClick(Task task);

}
