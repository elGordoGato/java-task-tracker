package tasks.epics.subTasks;
import tasks.Task;
import tasks.Type;

public class SubTask extends Task {
    private final Integer epicID;


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

}