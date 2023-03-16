package tasks.epics.subTasks;
import tasks.Task;
import tasks.Type;
import java.util.Objects;

public class SubTask extends Task /*implements Comparable<SubTask>*/{
    private final Integer epicID;

    public SubTask(SubTask task) {
        super(task);
        this.epicID = task.epicID;
    }

    public SubTask(Integer newEpicID, String title, String description, String startTime, String duration) {
        super(title, description, startTime, duration);
        epicID = newEpicID;
        this.type = Type.SUBTASK;
    }
    public SubTask(String id, String title, String status, String description, String startTime, String duration, String epicID){
        super(id,title,status,description, startTime, duration);
        this.epicID = Integer.valueOf(epicID);
        this.type = Type.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return getEpicID().equals(subTask.getEpicID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicID());
    }

    @Override
    public String toString() {
        return String.format("%s,%s",super.toString(),getEpicID());
    }

    public Integer getEpicID() {
        return epicID;
    }


}