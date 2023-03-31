package managers.taskManager;

import managers.historyManager.HistoryManager;
import managers.historyManager.InMemoryHistoryManager;
import tasks.Task;
import tasks.Type;

import java.util.List;
import java.util.Set;


public interface TaskManager {

    HistoryManager historyManager = new InMemoryHistoryManager();

    void createTask(Task newTask);

    void deleteAllTasks();       // Удаление всех задач

    void deleteByType(Type type);

    List<Task> getAllTasks();

    Task getByID(int hashId);

    void removeById(int hashId);

    void updateTask(Task task);

    Set<Task> getPrioritizedTasks();
}

