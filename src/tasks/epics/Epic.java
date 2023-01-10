package tasks.epics;

import tasks.Task;
import tasks.epics.subTasks.SubTask;

import java.util.ArrayList;


public class Epic extends Task {


    public Epic(String title, String description) {
        super(title, description);
        type = "Epic";
        currentStatus = status[0];
        taskList = new ArrayList<>();

    }


    @Override
    public void updateStatus() {
        if ( (taskList.size() == 0) || isSubTasksStatusRequired(0)){
            currentStatus = status[0];
        } else if (isSubTasksStatusRequired(2)){
            currentStatus = status[2];
        } else {
            currentStatus = status[1];
        }
    }
    private boolean isSubTasksStatusRequired(int statusNumber){
        for (SubTask subTask : taskList) {
            if (!subTask.currentStatus.equals(status[statusNumber])){
                return false;
            }
        }
        return true;
    }
}