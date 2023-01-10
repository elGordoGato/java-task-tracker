package tasks.epics.subTasks;

import tasks.Task;

public class SubTask extends Task {



    public SubTask(Integer newEpicID, String title, String description) {
        super(title, description);
        type = "SubTask";
        epicID = newEpicID;
    }

    @Override
    public String toString() {
        return super.toString() +
                "{epicID=" + epicID +
                '}';
    }

    @Override
    public void setId(int iD) {
        this.iD=iD;
    }
}
