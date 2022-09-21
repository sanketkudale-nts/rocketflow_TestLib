package com.rocketflyer.rocketflow.ui.tasklisting;

import com.tracki.data.model.response.config.Task;

public interface TaskClickListener {

    void onItemClick(Task task, int position);

    void onClickMapIcon(Task task, int position);

    void onCallClick(Task task, int position);

    void onExecuteUpdates(String name, Task task);

    void onDetailsTaskClick(Task task);

     void onChatClick(String buddyId, String name);

}
