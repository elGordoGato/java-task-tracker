import managers.taskManager.httpManager.HttpTaskServer;
import managers.taskManager.httpManager.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start(8080);
    }
}
