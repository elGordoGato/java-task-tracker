package Managers.taskManager;

import tasks.Task;


import java.util.HashMap;

public interface TaskManager {
    HashMap<Integer, Task> allTasks = new HashMap<>();


    void createTask(Task newTask);

    void deleteAllTasks();

    void printAllTasks();

    Task getByID(int hashId);

    void removeById(int hashId);

    void updateTask(Task task);




}

