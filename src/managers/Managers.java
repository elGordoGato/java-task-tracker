package managers;

import managers.historyManager.HistoryManager;
import managers.historyManager.InMemoryHistoryManager;
import managers.taskManager.InMemoryTaskManager;
import managers.taskManager.TaskManager;

public final class Managers {
    private static final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return historyManager;
    }
}
