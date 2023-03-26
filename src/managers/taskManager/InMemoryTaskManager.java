package managers.taskManager;

import tasks.Task;
import tasks.Type;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> allTasks = new HashMap<>();
    protected final HashMap<Integer, Epic> allEpics = new HashMap<>();
    protected final HashMap<Integer, SubTask> allSubtasks = new HashMap<>();
    private final Comparator<Task> comparator = (o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null) {
            return o1.getID() - o2.getID();
        }
        if (o1.getStartTime() == null) {
            return +1;
        }
        if (o2.getStartTime() == null) {
            return -1;
        }
        return o1.getStartTime().compareTo(o2.getStartTime());
    };

    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);


    public void createTask(Task newTask) {
        try {
            switch (newTask.getType()) {
                case TASK:
                    addTaskToPriorityList(newTask);
                    allTasks.put(newTask.getID(), newTask);
                    return;
                case EPIC:
                    allEpics.put(newTask.getID(), (Epic) newTask);
                    return;
                case SUBTASK:
                    addTaskToPriorityList(newTask);
                    allSubtasks.put(newTask.getID(), (SubTask) newTask);
                    allEpics.get(((SubTask) newTask).getEpicID()).addSubTask((SubTask) newTask);
            }
        } catch (ManagerSaveException exc) {
            System.out.println(exc.getMessage());
        }

    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> everyTask = new ArrayList<>(allTasks.values());
        everyTask.addAll(allEpics.values());
        everyTask.addAll(allSubtasks.values());
        for (Integer taskId : allTasks.keySet()) {
            System.out.println(allTasks.get(taskId));
        }
        for (Integer epicId : allEpics.keySet()) {
            System.out.println(allEpics.get(epicId));
            for (SubTask subTask : allEpics.get(epicId).getTaskList()) {
                System.out.println("---" + subTask);
            }
        }
        return everyTask;
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
        if (allTasks.containsKey(hashId)) {
            prioritizedTasks.remove(allTasks.remove(hashId));
        }
        if (allEpics.containsKey(hashId)) {
            for (SubTask subTask : allEpics.remove(hashId).getTaskList()) {
                prioritizedTasks.remove(allSubtasks.remove(subTask.getID()));
                historyManager.remove(subTask.getID());
            }
        }
        if (allSubtasks.containsKey(hashId)) {
            allEpics.get(allSubtasks.get(hashId).getEpicID()).removeSubtask(allSubtasks.get(hashId));
            prioritizedTasks.remove(allSubtasks.remove(hashId));
        }
        historyManager.remove(hashId);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : historyManager.getHistory()) {
            historyManager.remove(task.getID());
        }
        prioritizedTasks.clear();
        allTasks.clear();
        allEpics.clear();
        allSubtasks.clear();
    }

    @Override
    public void deleteByType(Type type) {
        switch (type) {
            case TASK:
                for (Integer hashId : allTasks.keySet()) {
                    historyManager.remove(hashId);
                }
                prioritizedTasks.removeAll(allTasks.values());
                allTasks.clear();
                return;
            case EPIC:
                for (Integer hashId : allEpics.keySet()) {
                    historyManager.remove(hashId);
                }
                for (Integer hashId : allSubtasks.keySet()) {
                    historyManager.remove(hashId);
                }
                prioritizedTasks.removeAll(allSubtasks.values());
                allEpics.clear();
                allSubtasks.clear();
                return;
            case SUBTASK:
                for (Integer hashId : allSubtasks.keySet()) {
                    historyManager.remove(hashId);
                    allEpics.get(allSubtasks.get(hashId).getEpicID()).removeSubtask(allSubtasks.get(hashId));
                }
                prioritizedTasks.removeAll(allSubtasks.values());
                allSubtasks.clear();
        }
    }

    @Override
    public void updateTask(Task task) {
        Task prevTask = null;
        try {
            if (allTasks.containsKey(task.getID())) {
                prevTask = allTasks.get(task.getID());
                prioritizedTasks.remove(prevTask);
                addTaskToPriorityList(task);
                allTasks.put(task.getID(), task);
                return;
            }
            if (allEpics.containsKey(task.getID())) {
                allEpics.get(task.getID()).getTaskList().forEach(t -> removeById(t.getID()));
                allEpics.put(task.getID(), (Epic) task);
                ((Epic) task).getTaskList().forEach(this::createTask);
                return;
            }
            if (allSubtasks.containsKey(task.getID())) {
                prevTask = allSubtasks.get(task.getID());
                prioritizedTasks.remove(prevTask);
                addTaskToPriorityList(task);
                Integer epicId = ((SubTask) task).getEpicID();
                allEpics.get(epicId).removeSubtask(allSubtasks.get(task.getID()));
                allEpics.get(epicId).addSubTask((SubTask) task);
                allSubtasks.put(task.getID(), (SubTask) task);
            }
        } catch (ManagerSaveException exc) {
            System.out.println(exc.getMessage());
            addTaskToPriorityList(prevTask);
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addTaskToPriorityList(Task newTask) throws ManagerSaveException {
        prioritizedTasks.removeIf(task -> task.getID() == newTask.getID());
        if (prioritizedTasks.add(newTask)) {
            checkTasksForIntersection(newTask);
        } else {
            throw new ManagerSaveException("Ошибка: данная задача пересекается по времени с уже существующей");
        }
    }

    private void checkTasksForIntersection(Task checkingTask) {
        List<Task> notNullTasks = prioritizedTasks.stream().filter(task -> task.getStartTime() != null).collect(Collectors.toList());
        for (int i = 0; i + 1 < notNullTasks.size(); i++) {
            LocalDateTime endTime = notNullTasks.get(i).getEndTime();
            if (endTime != null && endTime.isAfter(notNullTasks.get(i + 1).getStartTime())) {
                prioritizedTasks.remove(checkingTask);
                throw new ManagerSaveException("Ошибка добавления: данная задача пересекается по времени с уже существующей");
            }
        }
    }
}