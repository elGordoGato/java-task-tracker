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
    public SubTask(String id, String type, String title, String status, String description, String epicID){
        super(id,type,title,status,description);
        this.epicID = Integer.valueOf(epicID);
    }

    @Override
    public String toString() {
        return String.format("%s,%s",super.toString(),getEpicID());
    }

    public Integer getEpicID() {
        return epicID;
    }

}