package tasks.epics;

import tasks.Status;
import tasks.Task;
import tasks.Type;
import tasks.epics.subTasks.SubTask;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    List<SubTask> taskList;


    public Epic(String title, String description) {
        super(title, description);
        type = Type.EPIC;
        taskList = new ArrayList<>();

    }


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
            if (!subTask.getCurrentStatus().equals(status)){
                return false;
            }
        }
        return true;
    }
    public void addSubTask(SubTask subTask){
        taskList.add(subTask);
    }
    public void removeSubtask(SubTask subTask){
        taskList.remove(subTask);
    }

    public List<SubTask> getTaskList() {
        return taskList;
    }
}