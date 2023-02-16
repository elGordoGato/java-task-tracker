package managers.taskManager;
import tasks.Task;


public interface TaskManager {


    void createTask(Task newTask);

    void deleteAllTasks();

    void printAllTasks();

    Task getByID(int hashId);

    void removeById(int hashId);

    void updateTask(Task task);
}

