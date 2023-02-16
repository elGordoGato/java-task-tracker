package tasks;


public class Task {


    private String title;
    private String description;
    private final int iD;
    protected Status currentStatus;
    protected Type type;


    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        iD = hashCode();
        currentStatus = Status.NEW;
        type = Type.TASK;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return iD == task.getID();
    }


    @Override
    public int hashCode() {
        int hash = 17 * getClass().hashCode();

        if (title != null) {
            // вычисляем хеш первого поля и добавляем к нему начальное значение
            hash = hash + title.hashCode();
        }
        hash = hash * 31; // умножаем промежуточный результат на простое число

        if (description != null) {
            // вычисляем хеш второго поля и добавляем его к общему результату
            hash = hash + description.hashCode();
        }
        return hash * hash; // возвращаем итоговый хеш
    }


    @Override
    public String toString() {
        String address = getType() + "{" +
                "title='" + title + '\'';
        if (description != null) {
            address += ", description.length='" + description.length() + '\'';
        } else {
            address += ", description.length='null'";
        }
        return address + ", iD=" + iD +
                ", status=" + currentStatus +
                '}';
    }

    public int getID() {
        return iD;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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
}


