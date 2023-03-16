package tasks.epics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.epics.subTasks.SubTask;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
 /**a.   Пустой список подзадач.
   *b.   Все подзадачи со статусом NEW.
   *c.    Все подзадачи со статусом DONE.
   *d.    Подзадачи со статусами NEW и DONE.
   *e.    Подзадачи со статусом IN_PROGRESS.*/
   Epic testEpic;
    SubTask subTask3;
    SubTask subTask4;

    @BeforeEach
    void setUp() {
       testEpic = new Epic("Epic Test Name","epic descr");
        subTask3 = new SubTask(testEpic.getID(), "SubTask Test 3", "sub3 descr",
                "12.12.2023 12:00", "56");
        subTask4 = new SubTask("1111", "T4","DONE", "t4descr",
                "11.01.2023 13:35", "566", String.valueOf(testEpic.getID()));
    }

   @Test
   void shouldReturnNewStatusWhenNoSubtasks(){
        assertEquals(testEpic.getTaskList().size(), 0);
        assertEquals(Status.NEW, testEpic.getCurrentStatus(),
                "Epic status is " + testEpic.getCurrentStatus()+ " instead of NEW");
   }

    @Test
    void shouldReturnNewStatusWhenAllSubtasksAreNEW(){
        SubTask subTask1 = new SubTask(testEpic.getID(), "SubTask Test 1", "sub descr",
                "12.12.2023 12:00", "56");
        SubTask subTask2 = new SubTask("1111", "T1","NEW", "descr",
                "11.01.2023 13:35", "566", String.valueOf(testEpic.getID()));
        testEpic.addSubTask(subTask1);
        testEpic.addSubTask(subTask2);
        for (SubTask subTask : testEpic.getTaskList()){
            assertEquals(subTask.getCurrentStatus(), Status.NEW);
        }
        assertEquals(Status.NEW, testEpic.getCurrentStatus(),
                "Epic status is " + testEpic.getCurrentStatus()+ " instead of NEW");
    }

    @Test
    void shouldReturnDONEStatusWhenAllSubtasksAreDONE(){
        SubTask subTask1 = new SubTask(testEpic.getID(), "SubTask Test 1", "sub descr",
                "12.12.2023 12:00", "56");
        SubTask subTask2 = new SubTask("1111", "T1","DONE", "descr",
                "11.01.2023 13:35", "566", String.valueOf(testEpic.getID()));
        testEpic.addSubTask(subTask1);
        testEpic.updateSubTaskStatusById(subTask1.getID(),Status.DONE);
        testEpic.addSubTask(subTask2);
        for (SubTask subTask : testEpic.getTaskList()){
            assertEquals(subTask.getCurrentStatus(), Status.DONE);
        }
        assertEquals(Status.DONE, testEpic.getCurrentStatus(),
                "Epic status is " + testEpic.getCurrentStatus()+ " instead of DONE");
    }

    @Test
    void shouldReturnIN_PROGRESSStatusWhenSubtasksAreNEWandDONE(){
        SubTask subTask1 = new SubTask(testEpic.getID(), "SubTask Test 1", "sub descr",
                "12.12.2023 12:00", "56");
        SubTask subTask2 = new SubTask("1111", "T1","DONE", "descr",
                "11.01.2023 13:35", "566", String.valueOf(testEpic.getID()));
        testEpic.addSubTask(subTask1);
        testEpic.addSubTask(subTask2);
        assertEquals(subTask1.getCurrentStatus(), Status.NEW);
        assertEquals(subTask2.getCurrentStatus(), Status.DONE);
        assertEquals(Status.IN_PROGRESS, testEpic.getCurrentStatus(),
                "Epic status is " + testEpic.getCurrentStatus()+ " instead of IN_PROGRESS");
    }

    @Test
    void shouldReturnIN_PROGRESSStatusWhenAllSubtasksAreIN_PROGRESS(){
        SubTask subTask1 = new SubTask(testEpic.getID(), "SubTask Test 1", "sub descr",
                "12.12.2023 12:00", "56");
        SubTask subTask2 = new SubTask("1111", "T1","IN_PROGRESS", "descr",
                "11.01.2023 13:35", "566", String.valueOf(testEpic.getID()));
        testEpic.addSubTask(subTask1);
        testEpic.updateSubTaskStatusById(subTask1.getID(), Status.IN_PROGRESS);
        testEpic.addSubTask(subTask2);
        for (SubTask subTask : testEpic.getTaskList()){
            assertEquals(subTask.getCurrentStatus(), Status.IN_PROGRESS);
        }
        assertEquals(Status.IN_PROGRESS, testEpic.getCurrentStatus(),
                "Epic status is " + testEpic.getCurrentStatus()+ " instead of IN_PROGRESS");
    }

    @Test
    void shouldReturnNullInsteadOfTimeWhenCreatingEmptyEpic(){
        assertNull(testEpic.getEndTime());
        assertNull(testEpic.getStartTime());
        assertNull(testEpic.getDuration());
        assertEquals(List.of(), testEpic.getTaskList());
    }

    @Test
    void shouldUpdateEpicTimesWhenAddingSubTask(){
        SubTask subTask1 = new SubTask(testEpic.getID(), "SubTask Test 1", "sub descr",
                "12.12.2023 12:00", "56");
        SubTask subTask2 = new SubTask("1111", "T1","IN_PROGRESS", "descr",
                "13.12.2023 13:35", "566", String.valueOf(testEpic.getID()));
        testEpic.addSubTask(subTask1);
        assertEquals("2023-12-12T12:56", testEpic.getEndTime().toString());
        testEpic.addSubTask(subTask2);
        assertEquals("2023-12-12T12:00", testEpic.getStartTime().toString());
        assertEquals("PT10H22M", testEpic.getDuration().toString());
        assertEquals("2023-12-13T23:01", testEpic.getEndTime().toString());
    }

    @Test
    void shouldUpdateEpicTimesWhenRemovingExistingSubTask(){
        SubTask subTask1 = new SubTask(testEpic.getID(), "SubTask Test 1", "sub descr",
                "12.12.2023 12:00", "56");
        SubTask subTask2 = new SubTask("1111", "T1","IN_PROGRESS", "descr",
                "13.12.2023 13:35", "566", String.valueOf(testEpic.getID()));
        testEpic.addSubTask(subTask1);
        testEpic.addSubTask(subTask2);
        assertEquals("2023-12-12T12:00", testEpic.getStartTime().toString());
        testEpic.removeSubtask(subTask1);
        assertEquals("2023-12-13T13:35", testEpic.getStartTime().toString());
        assertEquals("PT9H26M", testEpic.getDuration().toString());
        assertEquals("2023-12-13T23:01", testEpic.getEndTime().toString());
    }

    @Test
    void shouldNotSetManuallyTimeAndStatusForEpic(){
        testEpic.addSubTask(subTask3);
        testEpic.addSubTask(subTask4);
        testEpic.setStartTime("31.02.3678 03:59");
        testEpic.setDuration("9999999");
        testEpic.updateStatus(Status.NEW);
        assertEquals(testEpic.getID()+",EPIC,Epic Test Name,IN_PROGRESS,epic descr,11.01.2023 13:35,622",
                testEpic.toString());
        assertEquals("2023-12-12T12:56", testEpic.getEndTime().toString());
    }
}