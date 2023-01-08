import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> allTasks = new HashMap<>();





    public void createTask(Task newTask) {
        if (newTask.type.equals("SubTask")){
            allTasks.get(newTask.epicID).taskList.add((SubTask)newTask);
            return;
        }
        allTasks.put(newTask.getID(), newTask);
    }



    public void deleteAllTasks() {
        allTasks.clear();
    }


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

    public Task getByID(int hashId) {
        if (allTasks.containsKey(hashId)) {
            return allTasks.get(hashId);
        }
        for (Integer id : allTasks.keySet()) {
            if (allTasks.get(id).type.equals("Epic")) {
                for (SubTask subTask : allTasks.get(id).taskList) {
                    if (subTask.getID() == hashId) {
                        return subTask;
                    }
                }
            }
        }
        return null;
    }

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

}

