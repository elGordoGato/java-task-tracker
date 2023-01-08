

import java.util.ArrayList;



public class Task {

    ArrayList<SubTask> taskList;
    private String title;
    private String description;
    private int iD;
    protected String[] status = {"NEW", "IN_PROGRESS", "DONE"};
    private int statusNumber = 0;
    protected String currentStatus;
    protected String type = "Task";
    protected Integer epicID;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        iD=hashCode();
        currentStatus = status[statusNumber];
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
        hash = hash / 31; // умножаем промежуточный результат на простое число

        if (description != null) {
            // вычисляем хеш второго поля и добавляем его к общему результату
            hash = hash + description.hashCode();
        }
        return hash; // возвращаем итоговый хеш
    }


    @Override
    public String toString() {
        String address = getClass()+"{" +
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

    public void setId(int iD) {
        this.iD = iD;
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

    public void updateStatus(){
        currentStatus = status[++statusNumber];
    }


}