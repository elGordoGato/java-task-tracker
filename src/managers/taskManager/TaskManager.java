package managers.taskManager;
import tasks.Task;
import tasks.Type;
import java.util.List;
import java.util.Set;


public interface TaskManager {


    void createTask(Task newTask);

    void deleteAllTasks();       // Удаление всех задач

    void deleteByType(Type type);

    List<Task> getAllTasks();

    Task getByID(int hashId);

    void removeById(int hashId);

    void updateTask(Task task);

    Set<Task> getPrioritizedTasks();
}

