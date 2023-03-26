package managers.taskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tasks.Status;
import tasks.Task;
import tasks.Type;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TaskManagerTest {
    TaskManager testTaskManager;

    TaskManager dataTaskManager;
    List<Task> expectedTasks;
    Comparator<Task> comparator = Comparator.comparingInt(Task::getID);
    Task task1;
    Task task2;
    Task epic1;
    Task sub1;
    Task sub2;
    Task sub3;
    Task epic2;


    @BeforeEach
    public void setDataTaskManager() {
        dataTaskManager = new InMemoryTaskManager();
        task1 = new Task("Task1", "T1descr",
                null, null);
        task2 = new Task("Task2", "T2descr",
                "24.12.1984 16:20", "754");
        epic1 = new Epic("Epic1", "E1descr");
        sub1 = new SubTask(epic1.getID(), "SubTask1", "S1descr",
                null, null);
        sub2 = new SubTask(epic1.getID(), "SubTask2", "S2descr",
                "24.12.1984 16:24", "44");
        sub3 = new SubTask("111", "SubTask3", "IN_PROGRESS", "S3descr",
                "24.12.1984 16:25", "50", String.valueOf(epic1.getID()));
        epic2 = new Epic("123", "Epic2", "");
        dataTaskManager.createTask(task1);
        dataTaskManager.createTask(epic1);
        dataTaskManager.createTask(epic2);
        expectedTasks = new ArrayList<>(List.of(
                task1,
                epic1,
                sub2,
                sub1,
                epic2));
        expectedTasks.sort(comparator);
    }

    @AfterEach
    void resetCondition(){
        testTaskManager.deleteAllTasks();
    }


    @Test
    void createTask() {
        testTaskManager.createTask(task1);
        assertEquals(task1, testTaskManager.getByID(task1.getID()));
    }

    @Test
    void shouldThrowNullPointerWhenCreateTaskWithNullArgument() {
        assertThrows(NullPointerException.class, () -> testTaskManager.createTask(null));
    }

    @Test
    void shouldThrowNullPointerWhenCreateSubTaskWithoutEpic() {
        assertThrows(NullPointerException.class, () -> testTaskManager.createTask(sub1));
    }

    @Test
    void shouldNotCreateTaskWhenInterferingWithExistingTaskByTime() {
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        testTaskManager = dataTaskManager;
        testTaskManager.createTask(sub3);
        assertNull(testTaskManager.getByID(sub3.getID()));
        assertEquals("1984-12-24T16:24", testTaskManager.getByID(epic1.getID()).getStartTime().toString());
        assertEquals("1984-12-24T17:08", testTaskManager.getByID(epic1.getID()).getEndTime().toString());
        assertEquals(Set.of(sub2,task1,sub1), testTaskManager.getPrioritizedTasks());

    }

    @Test
    void shouldOverrideTaskWithSameId() {
        testTaskManager.createTask(task1);
        Task task3 = new Task(String.valueOf(task1.getID()), "Task3", "IN_PROGRESS", "S3descr", "24.12.1984 16:24", "44");
        testTaskManager.createTask(task3);
        assertEquals(List.of(task3), testTaskManager.getAllTasks());
        assertEquals(Set.of(task3), testTaskManager.getPrioritizedTasks());
    }

    @Test
    void deleteAllTasksWhenNoTasks() {
        assertEquals(List.of(), testTaskManager.getAllTasks());
        testTaskManager.deleteAllTasks();
        assertEquals(List.of(), testTaskManager.getAllTasks());
        assertEquals(Set.of(), testTaskManager.getPrioritizedTasks());
    }

    @Test
    void deleteAllTasksWhenHaveSomeTasks() {
        testTaskManager = dataTaskManager;
        testTaskManager.deleteAllTasks();
        assertEquals(new ArrayList<>(), testTaskManager.getAllTasks());
        assertEquals(Set.of(), testTaskManager.getPrioritizedTasks());
    }

    @Test
    void deleteByTypeWhenNoTasks() {
        assertEquals(new ArrayList<>(), testTaskManager.getAllTasks());
        testTaskManager.deleteByType(Type.TASK);
        assertEquals(new ArrayList<>(), testTaskManager.getAllTasks());
        assertEquals(Set.of(), testTaskManager.getPrioritizedTasks());
    }

    @Test
    void shouldDeleteAllSubTasksWhenDeleteByTypeEpic() {
        assertEquals(new ArrayList<>(), testTaskManager.getAllTasks());
        testTaskManager = dataTaskManager;
        testTaskManager.createTask(new SubTask(epic1.getID(),"SubTask4","S4descr", null, null));
        testTaskManager.deleteByType(Type.EPIC);
        assertEquals(List.of(task1), testTaskManager.getAllTasks());
        assertEquals(Set.of(task1), testTaskManager.getPrioritizedTasks());
    }

    @Test
    void shouldKeepEpicWhenDeleteAllSubTasks(){
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        testTaskManager = dataTaskManager;
        ((Epic)epic1).updateSubTaskStatusById(sub1.getID(),Status.DONE);
        testTaskManager.deleteByType(Type.SUBTASK);
        assertNull(testTaskManager.getByID(epic1.getID()).getStartTime());
        assertNull(testTaskManager.getByID(epic1.getID()).getDuration());
        assertNull(testTaskManager.getByID(epic1.getID()).getDuration());
        assertEquals(Status.NEW, testTaskManager.getByID(epic1.getID()).getCurrentStatus());
        assertEquals(List.of(task1,epic1,epic2), testTaskManager.getAllTasks());
        assertEquals(Set.of(task1), testTaskManager.getPrioritizedTasks());
    }


    @Test
    void shouldThrowNullPointerExemptionWhenDeleteByTypeWithoutArgument() {
        assertEquals(new ArrayList<>(), testTaskManager.getAllTasks());
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        testTaskManager = dataTaskManager;
        assertThrows(NullPointerException.class, () -> testTaskManager.deleteByType(null));
        List<Task> tasksFromTaskManager = testTaskManager.getAllTasks();
        tasksFromTaskManager.sort(comparator);
        assertEquals(expectedTasks, tasksFromTaskManager);
    }


    @Test
    void getAllTasks() {
        assertEquals(List.of(), testTaskManager.getAllTasks());
    }

    @Test
    void shouldReturnTaskWhenGetByID() {
        testTaskManager = dataTaskManager;
        assertEquals(task1, testTaskManager.getByID(task1.getID()));
    }

    @Test
    void shouldReturnNullWhenGetByWrongId() {
        testTaskManager = dataTaskManager;
        int randomId = findUniqueId(testTaskManager);
        assertNull(testTaskManager.getByID(randomId));
    }

    @Test
    void shouldReturnNullWhenGetByWrongIdOnEmptyTaskManager() {
        assertNull(testTaskManager.getByID(task1.getID()));
    }


    @MethodSource("taskIdStream")
    @ParameterizedTest(name = "{index} call method with ID: {0}")
    void removeById(Integer Id) {
        testTaskManager = dataTaskManager;
        testTaskManager.removeById(Id);
        assertNull(testTaskManager.getByID(Id));
    }

    private Stream<Arguments> taskIdStream() {
        return Stream.of(
                Arguments.of(task1.getID()),
                Arguments.of(epic1.getID()),
                Arguments.of(sub1.getID()),
                Arguments.of(sub2.getID()),
                Arguments.of(sub3.getID()),
                Arguments.of(0)
        );
    }

    @Test
    void shouldDeleteAllSubtasksWhenRemoveEpicById() {
        testTaskManager = dataTaskManager;
        testTaskManager.removeById(epic1.getID());
        List<Task> updatedTaskList = testTaskManager.getAllTasks().stream().sorted(comparator).collect(Collectors.toList());
        assertNull(testTaskManager.getByID(epic1.getID()));
        assertNull(testTaskManager.getByID(sub1.getID()));
        assertEquals(Stream.of(task1, epic2).sorted(comparator).collect(Collectors.toList()), updatedTaskList);
        assertEquals(Set.of(task1), testTaskManager.getPrioritizedTasks());
    }

    @Test
    void shouldDoNothingWhenRemoveByIdWithWrongId() {
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        testTaskManager = dataTaskManager;
        int randomId = findUniqueId(testTaskManager);
        testTaskManager.removeById(randomId);
        assertEquals(expectedTasks, testTaskManager.getAllTasks().stream().sorted(comparator).collect(Collectors.toList()));
    }


    @Test
    void shouldReturnUpdatedTask() {
        testTaskManager.createTask(task1);
        Task task3 = new Task(task1);
        task3.setTitle("New Title");
        task3.setDescription("New descr");
        task3.setStartTime("12.04.1974 13:55");
        task3.setDuration("785");
        task3.updateStatus(Status.DONE);
        testTaskManager.updateTask(task3);
        assertEquals(task3.getID() + ",TASK,New Title,DONE,New descr,12.04.1974 13:55,785",
                testTaskManager.getByID(task1.getID()).toString());
    }

    @Test
    void shouldNotUpdateTaskWhenNewTimeIsInterfereWithTimeFromOtherTask() {
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        testTaskManager = dataTaskManager;
        SubTask sub4 = new SubTask((SubTask) sub1);
        sub4.setTitle("Changed time");
        sub4.setStartTime("24.12.1984 16:22");
        sub4.setDuration("785");
        testTaskManager.updateTask(sub4);
        assertEquals(sub4.getID() + ",SUBTASK,SubTask1,NEW,S1descr,null,null," + sub4.getEpicID(),
                testTaskManager.getByID(sub1.getID()).toString());
    }

    @Test
    void shouldUpdateTaskWhenNewTimeIsInterfereWithPreviousTime() {
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        testTaskManager = dataTaskManager;
        SubTask sub4 = new SubTask((SubTask) sub2);
        sub4.setTitle("Changed time");
        sub4.setStartTime("24.12.1984 16:22");
        sub4.setDuration("785");
        testTaskManager.updateTask(sub4);
        assertEquals(sub4.getID() + ",SUBTASK,Changed time,NEW,S2descr,24.12.1984 16:22,785," + sub4.getEpicID(),
                testTaskManager.getByID(sub2.getID()).toString());
    }

    @Test
    void shouldDoNothingWhenUpdateTaskByWrongId() {
        testTaskManager.createTask(task1);
        testTaskManager.updateTask(task2);
        assertEquals(List.of(task1), testTaskManager.getAllTasks());
        assertEquals(task1.getID() + ",TASK,Task1,NEW,T1descr,null,null",
                testTaskManager.getByID(task1.getID()).toString());
    }

    @Test
    void shouldThrowNullPointerExemptionWhenUpdateTaskWithoutArgument() {
        testTaskManager.createTask(task1);
        assertThrows(NullPointerException.class, () -> testTaskManager.updateTask(null));
        assertEquals(List.of(task1), testTaskManager.getAllTasks());
        assertEquals(task1.getID() + ",TASK,Task1,NEW,T1descr,null,null",
                testTaskManager.getByID(task1.getID()).toString());
    }

        @Test
        void shouldUpdateSubtasksWhenUpdatingEpic(){
        testTaskManager = dataTaskManager;
        Epic newEpic = new Epic(String.valueOf(epic1.getID()), "Changed Epic", "new descr");
        newEpic.addSubTask((SubTask)sub3);
        testTaskManager.updateTask(newEpic);
        List<Task> newListOfTasks = new ArrayList<>(testTaskManager.getAllTasks());
        newListOfTasks.sort(comparator);
        assertEquals(Stream.of(task1,newEpic,sub3,epic2).sorted(comparator).collect(Collectors.toList()),
                newListOfTasks);
        assertEquals(Set.of(sub3,task1), testTaskManager.getPrioritizedTasks());

        }



    @Test
    void shouldReturnPrioritizedTasksList(){
        dataTaskManager.createTask(sub1);
        dataTaskManager.createTask(sub2);
        testTaskManager = dataTaskManager;
        SubTask sub4 = new SubTask((SubTask) sub1);
        sub4.setTitle("Changed time");
        sub4.setStartTime("24.12.1984 16:22");
        sub4.setDuration("785");
        testTaskManager.updateTask(sub4);
        Set<Task> prioritizedTasks = testTaskManager.getPrioritizedTasks();
        System.out.println(prioritizedTasks);
        assertEquals(Set.of(sub2,task1,sub1), prioritizedTasks);
    }





    private int findUniqueId(TaskManager testTaskManager) {
        int randomId = 0;
        boolean isIdUnique = false;
        while (!isIdUnique) {
            isIdUnique = true;
            randomId = (int) (Math.random() * 100000 + 1);
            for (Task task : testTaskManager.getAllTasks()) {
                if (task.getID() == randomId) {
                    isIdUnique = false;
                    break;
                }
            }
        }
        return randomId;
    }
}