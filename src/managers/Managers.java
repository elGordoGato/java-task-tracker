package managers;

import managers.historyManager.HistoryManager;
import managers.historyManager.InMemoryHistoryManager;
import managers.taskManager.FileBackedTasksManager;
import managers.taskManager.InMemoryTaskManager;
import managers.taskManager.TaskManager;
import managers.taskManager.httpManager.HttpTaskManager;

public final class Managers {
    static final String uri = "http://localhost:8078";
    private static final TaskManager fileBackedTasksManager = new FileBackedTasksManager();

    private static final TaskManager httpTaskManager = new HttpTaskManager(uri);
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager inMemoryTaskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return httpTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
