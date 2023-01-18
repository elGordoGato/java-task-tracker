package tasks.epics;

import tasks.Status;
import tasks.Task;
import tasks.epics.subTasks.SubTask;

import java.util.ArrayList;


public class Epic extends Task {


    public Epic(String title, String description) {
        super(title, description);
        type = "Epic";
        taskList = new ArrayList<>();

    }

    @Override
    public void updateStatus() {
        if ( (taskList.size() == 0) || isSubTasksStatusRequired(Status.NEW)){
            currentStatus = Status.NEW;
        } else if (isSubTasksStatusRequired(Status.DONE)){
            currentStatus = Status.DONE;
        } else {
            currentStatus = Status.IN_PROGRESS;
        }
    }
    private boolean isSubTasksStatusRequired(Status status){
        for (SubTask subTask : taskList) {
            if (!subTask.currentStatus.equals(status)){
                return false;
            }
        }
        return true;
    }
}