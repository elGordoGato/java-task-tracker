package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task implements Comparable<Task> {


    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final int iD;
    protected Status currentStatus;
    protected Type type;
    protected LocalDateTime startTime = null;
    protected Duration duration = null;
    private String title;
    private String description;


    public Task(Task task) {
        this.title = task.title;
        this.description = task.description;
        this.iD = task.iD;
        this.currentStatus = task.currentStatus;
        this.type = task.type;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    public Task(String title, String description, String startTime, String duration) {
        this.title = title;
        this.description = description;
        iD = hashCode() ^ 2;
        currentStatus = Status.NEW;
        if (Optional.ofNullable(startTime).isPresent()) {
            this.startTime = LocalDateTime.parse(startTime,
                    formatter);
        }
        if (Optional.ofNullable(duration).isPresent()) {
            this.duration = Duration.ofMinutes(Long.parseLong(duration));
        }
        this.type = Type.TASK;
    }

    public Task(String id, String title, String status, String description, String startTime, String duration) {                   //id,type,name,status,description,epic
        this.iD = Integer.parseInt(id);
        this.title = title;
        this.currentStatus = Status.valueOf(status);
        this.description = description;
        if (Optional.ofNullable(startTime).isPresent() && !("null").equals(startTime)) {
            this.startTime = LocalDateTime.parse(startTime, formatter);
        }
        if (Optional.ofNullable(duration).isPresent() && !("null").equals(duration)) {
            this.duration = Duration.ofMinutes(Long.parseLong(duration));
        }
        this.type = Type.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return getTitle().equals(task.getTitle()) && getDescription().equals(task.getDescription()) &&
                getCurrentStatus() == task.getCurrentStatus() && getType() == task.getType() &&
                Objects.equals(getStartTime(), task.getStartTime()) && Objects.equals(getDuration(), task.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getCurrentStatus(), getType(), getStartTime(), getDuration());
    }

    @Override
    public String toString() {                  //id,type,name,status,description,epic
        //3,SUBTASK,Sub Task2,DONE,Description sub task3,2
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                getID(), getType(), getTitle(), getCurrentStatus(), getDescription(),
                Optional.ofNullable(startTime).map(localDateTime -> localDateTime.format(formatter)).orElse(null),
                Optional.ofNullable(duration).map(Duration::toMinutes).orElse(null));
    }

    public int getID() {
        return iD;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public Type getType() {
        return type;
    }

    public void updateStatus(Status newStatus) {
        currentStatus = newStatus;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = Duration.ofMinutes(Long.parseLong(duration));
    }

    public LocalDateTime getEndTime() {
        if (Optional.ofNullable(startTime).isPresent() && Optional.ofNullable(duration).isPresent()) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    @Override
    public int compareTo(Task o) {
        return o.getID() - this.getID();
    }
}


