package Managers.taskManager;

import Managers.historyManager.HistoryManager;
import tasks.Task;
import tasks.epics.subTasks.SubTask;
import Managers.Managers;


public class InMemoryTaskManager implements TaskManager {
    public HistoryManager historyManager = Managers.getDefaultHistory();



    @Override
    public void createTask(Task newTask) {
        if (newTask.type.equals("SubTask")){
            allTasks.get(newTask.epicID).taskList.add((SubTask)newTask);
            return;
        }
        allTasks.put(newTask.getID(), newTask);
    }

    @Override
    public void deleteAllTasks() {
        allTasks.clear();
    }

    @Override
    public void printAllTasks() {
        for (Integer hashId : allTasks.keySet()) {
            System.out.println(allTasks.get(hashId));
            if (allTasks.get(hashId).type.equals("Epic")) {
                for (SubTask subTask : allTasks.get(hashId).taskList) {
                    System.out.println("---" + subTask);
                }
            }
        }
    }

    @Override
    public Task getByID(int hashId) {
        if (allTasks.containsKey(hashId)) {
            historyManager.add(allTasks.get(hashId));
            return allTasks.get(hashId);
        }
        for (Integer id : allTasks.keySet()) {
            if (allTasks.get(id).type.equals("Epic")) {
                for (SubTask subTask : allTasks.get(id).taskList) {
                    if (subTask.getID() == hashId) {
                        historyManager.add(subTask);
                        return subTask;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void removeById(int hashId) {
        if (allTasks.containsKey(hashId)) {
            allTasks.remove(hashId);
            return;
        }
        for (Integer id : allTasks.keySet()) {
            if (allTasks.get(id).type.equals("Epic")) {
                allTasks.get(id).taskList.removeIf(subTask -> subTask.getID() == hashId);
            }
        }
    }

    @Override
    public void updateTask(Task task){
        if (allTasks.containsKey(task.getID())) {
            allTasks.put(task.getID(), task);
            return;
        }
        for (Integer id : allTasks.keySet()) {
            if (allTasks.get(id).type.equals("Epic")) {
                for (SubTask subTask : allTasks.get(id).taskList) {
                    if (subTask.equals(task)) {
                        allTasks.get(id).taskList.remove(subTask);
                        allTasks.get(id).taskList.add((SubTask) task);
                        allTasks.get(id).updateStatus();
                    }
                }
            }
        }
    }
    public static void updateId(Task task, int newID) {
        if (allTasks.containsKey(task.getID())) {
            allTasks.put(newID, task);
            if (allTasks.get(newID).type.equals("Epic")) {
                for (SubTask subTask : allTasks.get(newID).taskList) {
                    subTask.epicID = newID;
                }
            }

        }
    }



}

