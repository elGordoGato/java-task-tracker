package tasks;

public class SingleTask extends Task{




    public SingleTask(String title, String description) {
        super(title,description);
        type = Type.TASK;
    }

    public void updateStatus(Status newStatus) {
        currentStatus = newStatus;
    }

}




