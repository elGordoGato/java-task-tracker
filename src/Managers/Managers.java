package Managers;

import Managers.historyManager.HistoryManager;
import Managers.historyManager.InMemoryHistoryManager;
import Managers.taskManager.InMemoryTaskManager;
import Managers.taskManager.TaskManager;

public final class Managers {
    private static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return historyManager;
    }
}
