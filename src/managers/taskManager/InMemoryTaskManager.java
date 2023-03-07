package managers.taskManager;
import managers.Managers;
import managers.historyManager.HistoryManager;
import tasks.Task;
import tasks.Type;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager {
    public HistoryManager historyManager = Managers.getDefaultHistory();
    HashMap<Integer, Task> allTasks = new HashMap<>();
    HashMap<Integer, Epic> allEpics = new HashMap<>();
    HashMap<Integer, SubTask> allSubtasks = new HashMap<>();

    public void createTask(Task newTask) {
        switch (newTask.getType()) {
            case TASK:
                allTasks.put(newTask.getID(), newTask);
                return;
            case EPIC:
                allEpics.put(newTask.getID(), (Epic) newTask);
                return;
            case SUBTASK:
                allSubtasks.put(newTask.getID(), (SubTask) newTask);
                allEpics.get(((SubTask) newTask).getEpicID()).addSubTask((SubTask) newTask);
        }
    }

    @Override
    public void printAllTasks() {
        for (Integer taskId : allTasks.keySet()) {
            System.out.println(allTasks.get(taskId));
        }
        for (Integer epicId : allEpics.keySet()) {
            System.out.println(allEpics.get(epicId));
            for (SubTask subTask : allEpics.get(epicId).getTaskList()) {
                System.out.println("---" + subTask);
            }
        }
    }

    @Override
    public Task getByID(int hashId) {
        if (allTasks.containsKey(hashId)) {
            historyManager.add(allTasks.get(hashId));
            return allTasks.get(hashId);
        }
        if (allEpics.containsKey(hashId)) {
            historyManager.add(allEpics.get(hashId));
            return allEpics.get(hashId);
        }
        if (allSubtasks.containsKey(hashId)) {
            historyManager.add(allSubtasks.get(hashId));
            return allSubtasks.get(hashId);
        }
        return null;
    }

    @Override
    public void removeById(int hashId) {
        allTasks.remove(hashId);
        if (allEpics.containsKey(hashId)) {
            for (SubTask subTask : allEpics.remove(hashId).getTaskList()) {
                allSubtasks.remove(subTask.getID());
                historyManager.remove(subTask.getID());
            }
        }
        if (allSubtasks.containsKey(hashId)) {
            allEpics.get(allSubtasks.get(hashId).getEpicID()).removeSubtask(allSubtasks.get(hashId));
            allSubtasks.remove(hashId);
        }
        historyManager.remove(hashId);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : historyManager.getHistory()) {
            historyManager.remove(task.getID());
        }
        allTasks.clear();
        allEpics.clear();
        allSubtasks.clear();
    }

    @Override
    public void deleteByType(Type type){
        switch (type){
            case TASK:
                for (Integer hashId : allTasks.keySet()) {
                    historyManager.remove(hashId);
                }
                allTasks.clear();
                return;
            case EPIC:
                for (Integer hashId : allEpics.keySet()) {
                    historyManager.remove(hashId);
                }
                for (Integer hashId : allSubtasks.keySet()) {
                    historyManager.remove(hashId);
                }
                allEpics.clear();
                allSubtasks.clear();
                return;
            case SUBTASK:
                for (Integer hashId : allSubtasks.keySet()) {
                    historyManager.remove(hashId);
                    allEpics.get(allSubtasks.get(hashId).getEpicID()).removeSubtask(allSubtasks.get(hashId));
                }
                allSubtasks.clear();
        }
    }

    @Override
    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getID())) {
            allTasks.put(task.getID(),task);
            return;
        }
        if (allEpics.containsKey(task.getID())) {
            allEpics.put(task.getID(), (Epic) task);
            return;
        }
        if (allSubtasks.containsKey(task.getID())) {
            Integer epicId = ((SubTask) task).getEpicID();
            allEpics.get(epicId).removeSubtask(allSubtasks.get(task.getID()));
            allEpics.get(epicId).addSubTask((SubTask) task);
            allEpics.get(epicId).updateStatus();
            allSubtasks.put(task.getID(), (SubTask) task);
        }
    }
}





