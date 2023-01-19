package tasks.epics.subTasks;

import tasks.Status;
import tasks.Task;
import tasks.Type;

public class SubTask extends Task {
    Integer epicID;


    public SubTask(Integer newEpicID, String title, String description) {
        super(title, description);
        type = Type.SUBTASK;
        epicID = newEpicID;
    }

    @Override
    public String toString() {
        return super.toString() +
                "{epicID=" + epicID +
                '}';
    }

    public Integer getEpicID() {
        return epicID;
    }

    public void updateStatus(Status newStatus) {
        currentStatus = newStatus;
    }
}