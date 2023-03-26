package tasks.epics;

import tasks.Status;
import tasks.Task;
import tasks.Type;
import tasks.epics.subTasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class Epic extends Task {
    private final Map<Integer, SubTask> taskList;
    private LocalDateTime endTime = null;


    public Epic(Epic epic) {
        super(epic);
        this.taskList = epic.taskList;
    }

    public Epic(String title, String description) {
        super(title, description, null, null);
        taskList = new HashMap<>();
        this.type = Type.EPIC;
    }

    public Epic(String id, String title, String description) {
        super(id, title, String.valueOf(Status.NEW), description, null, null);
        taskList = new HashMap<>();
        this.type = Type.EPIC;
    }

    public void addSubTask(SubTask subTask) {
        taskList.put(subTask.getID(), subTask);
        updateStatus();
    }

    public void updateSubTaskStatusById(int hashId, Status newStatus) {
        taskList.get(hashId).updateStatus(newStatus);
        updateStatus();
    }

    public void removeSubtask(SubTask subTask) {
        taskList.remove(subTask.getID());
        updateStatus();
    }

    public List<SubTask> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void setStartTime(String startTime) {
        updateTiming();
    }

    @Override
    public void setDuration(String duration) {
        updateTiming();
    }

    @Override
    public void updateStatus(Status newStatus) {
        updateStatus();
    }


    private void updateStatus() {
        updateTiming();
        if ((taskList.size() == 0) || isSubTasksStatusRequired(Status.NEW)) {
            currentStatus = Status.NEW;
        } else if (isSubTasksStatusRequired(Status.DONE)) {
            currentStatus = Status.DONE;
        } else {
            currentStatus = Status.IN_PROGRESS;
        }
    }

    private boolean isSubTasksStatusRequired(Status status) {
        for (Integer subId : taskList.keySet()) {
            if (!taskList.get(subId).getCurrentStatus().equals(status)) {
                return false;
            }
        }
        return true;
    }

    private void updateTiming() {
        Comparator<SubTask> comparator = Comparator.comparing(Task::getStartTime);
        Optional<SubTask> earliestSubTask = taskList.values().stream().filter(subTask -> subTask.getStartTime() != null).min(comparator);
        earliestSubTask.ifPresentOrElse(task -> startTime = task.getStartTime(), () -> startTime = null);
        Optional<SubTask> lastSubTask = taskList.values().stream().filter(subTask -> subTask.getEndTime() != null).max(comparator);
        lastSubTask.ifPresentOrElse(task -> endTime = task.getEndTime(), () -> endTime = null);
        duration = Duration.ZERO;
        taskList.values().stream().filter(subTask -> subTask.getDuration() != null).forEach(subTask -> duration = duration.plus(subTask.getDuration()));
        if (duration == Duration.ZERO) {
            duration = null;
        }
    }

}