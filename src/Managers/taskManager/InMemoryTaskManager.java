package Managers.taskManager;

import Managers.historyManager.HistoryManager;
import tasks.SingleTask;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;
import Managers.Managers;

import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager {
    public HistoryManager historyManager = Managers.getDefaultHistory();
    HashMap<Integer, SingleTask> allTasks = new HashMap<>();
    HashMap<Integer, Epic> allEpics = new HashMap<>();
    HashMap<Integer, SubTask> allSubtasks = new HashMap<>();








    public void createTask(Task newTask) {
        switch (newTask.getType()) {
            case TASK:
                allTasks.put(newTask.getID(), (SingleTask) newTask);
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
        if (allEpics.containsKey(hashId)){
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
        allTasks.clear();
        allEpics.clear();
        allSubtasks.clear();
        for (Task task : historyManager.getHistory()) {
            historyManager.remove(task.getID());
        }
    }

    @Override
    public void updateTask(Task task){
        if (allTasks.containsValue(task)) {
            allTasks.remove(task);
            allTasks.put(task.getID(), (SingleTask) task);
            return;
        }
        if (allEpics.containsValue(task)) {
            allEpics.remove(task);
            allEpics.put(task.getID(), (Epic) task);
            return;
        }
        if (allSubtasks.containsValue(task)) {
            Integer epicId = ((SubTask) task).getEpicID();
            allEpics.get(epicId).removeSubtask((SubTask) task);
            allEpics.get(epicId).addSubTask((SubTask) task);
            allEpics.get(epicId).updateStatus();
            allSubtasks.remove(task);
            allSubtasks.put(task.getID(), (SubTask) task);
            }
        }


    }





