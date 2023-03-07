package managers.taskManager;
import tasks.Task;
import tasks.Type;


public interface TaskManager {


    void createTask(Task newTask);

    void deleteAllTasks();       // Удаление всех задач

    void deleteByType(Type type);

    void printAllTasks();

    Task getByID(int hashId);

    void removeById(int hashId);

    void updateTask(Task task);
}

