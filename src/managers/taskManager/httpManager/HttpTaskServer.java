package managers.taskManager.httpManager;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.taskManager.ManagerSaveException;
import managers.taskManager.TaskManager;
import tasks.Status;
import tasks.Task;
import tasks.epics.Epic;
import tasks.epics.subTasks.SubTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class HttpTaskServer {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static Gson gson;
    private final TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer() {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public void start(int port) {
        try {
            httpServer = HttpServer.create();

            httpServer.bind(new InetSocketAddress(port), 0);
            httpServer.createContext("/tasks", new TaskHandler());

            httpServer.start();
        } catch (IOException exc) {
            throw new ManagerSaveException("Can not start http server");
        }
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange);

            switch (endpoint) {
                case GET_PRIORITY: {
                    System.out.println("get priority");
                    writeResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
                    break;
                }
                case GET_TASKS: {
                    System.out.println("get tasks");
                    writeResponse(exchange, gson.toJson(taskManager.getAllTasks()), 200);

                    break;
                }
                case POST_TASK: {
                    System.out.println("post task");
                    handlePostTask(exchange);

                    break;
                }
                case DELETE_ALL_TASKS: {
                    System.out.println("all delete");
                    taskManager.deleteAllTasks();
                    writeResponse(exchange, "all deleted", 205);
                    break;
                }
                case GET_TASK_BY_ID: {
                    System.out.println("get by id");
                    handleGetTaskById(exchange);

                    break;
                }
                case DELETE_TASK_BY_ID: {
                    System.out.println("delete  by id");
                    deleteTaskById(exchange);
                    break;
                }
                case SUBTASKS_METHODS: {
                    System.out.println("subtasks meth");
                    handleSubtasks(exchange);
                    break;
                }
                case EPICS_METHODS: {
                    System.out.println("epics meth");
                    handleEpics(exchange);
                    break;
                }
                case GET_HISTORY: {
                    System.out.println("get history");
                    writeResponse(exchange, gson.toJson(taskManager.historyManager.getHistory()), 200);
                    break;
                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {

            try {
                boolean isAbsent = true;
                String bodyWithTask = readBody(exchange);
                if (bodyWithTask.isEmpty()) {
                    writeResponse(exchange, "no body", 400);
                    return;
                }
                Task inputTask;
                if (bodyWithTask.contains("\"type\": \"TASK\"")) {
                    inputTask = gson.fromJson(bodyWithTask, Task.class);
                } else if (bodyWithTask.contains("\"type\": \"EPIC\"")) {
                    inputTask = gson.fromJson(bodyWithTask, Epic.class);
                } else if (bodyWithTask.contains("\"type\": \"SUBTASK\"")) {
                    inputTask = gson.fromJson(bodyWithTask, SubTask.class);
                } else {
                    throw new JsonSyntaxException("Task type not found");
                }
                for (Task task : taskManager.getAllTasks()) {
                    if (task.getID() == inputTask.getID()) {
                        taskManager.updateTask(inputTask);
                        writeResponse(exchange, "Task updated", 201);
                        isAbsent = false;
                        break;
                    }
                }
                if (isAbsent) {
                    taskManager.createTask(inputTask);
                    writeResponse(exchange, "Task с идентификатором " + inputTask.getID() + " created", 201);
                }
            } catch (JsonSyntaxException exc) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
            } catch (NumberFormatException exc) {
                writeResponse(exchange, "Некорректный идентификатор поста", 400);
            }
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> id = getTaskId(exchange);
            if (id.isPresent()) {
                writeResponse(exchange, gson.toJson(taskManager.getByID(id.get())), 200);
            } else {
                writeResponse(exchange, "incorrect ID", 400);
            }
        }

        private void deleteTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор of task", 400);
                return;
            }
            int taskId = taskIdOpt.get();
            taskManager.removeById(taskId);
            writeResponse(exchange, "Пост с идентификатором " + taskId + " deleted", 200);
        }

        private void handleSubtasks(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (taskIdOpt.isPresent()) {           //tasks/subtasks?id=
                writeResponse(exchange, gson.toJson(((SubTask) taskManager.getByID(taskIdOpt.get())).getEpicID()), 200);
            } else {
                writeResponse(exchange, "wrong id", 400);
            }
        }

        private void handleEpics(HttpExchange exchange) throws IOException {  //tasks/epic/
            String requestPath = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            String[] pathParts = requestPath.split("/");
            Optional<Integer> taskIdOpt = getTaskId(exchange);
            if (pathParts.length == 3 && requestMethod.equals("POST") && taskIdOpt.isPresent()) {
                handleUpdateSubTaskStatusById(exchange, taskIdOpt.get());
            }
            if (pathParts.length == 3 && requestMethod.equals("GET") && taskIdOpt.isPresent()) {
                int epicId = taskIdOpt.get();
                writeResponse(exchange, gson.toJson(((Epic) taskManager.getByID(epicId)).getTaskList()), 200);
            }
            if (pathParts.length == 4 && pathParts[3].equals("endtime") && taskIdOpt.isPresent() && requestMethod.equals("GET")) {
                int epicId = taskIdOpt.get();
                writeResponse(exchange, gson.toJson(taskManager.getByID(epicId).getEndTime()), 200);
            }
        }

        private void handleUpdateSubTaskStatusById(HttpExchange exchange, int epicId) throws IOException {
            try {
                String requestBody = readBody(exchange);
                if (requestBody.isEmpty()) {
                    writeResponse(exchange, "no body", 400);
                    return;
                }
                JsonElement jsonElement = JsonParser.parseString(requestBody);
                if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                    System.out.println("Ответ от сервера не соответствует ожидаемому.");
                    return;
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                int subtaskId = jsonObject.get("id").getAsInt();
                Status newStatus = Status.valueOf(jsonObject.get("status").getAsString());
                ((Epic) taskManager.getByID(epicId)).updateSubTaskStatusById(subtaskId, newStatus);
                writeResponse(exchange, "subtask " + subtaskId + " is " + newStatus, 201);
            } catch (JsonSyntaxException | IllegalArgumentException exc) {
                writeResponse(exchange, "wrong body", 400);
            }
        }

        private Endpoint getEndpoint(HttpExchange exchange) {
            String requestPath = exchange.getRequestURI().getRawPath();
            String requestMethod = exchange.getRequestMethod();
            String[] pathParts = requestPath.split("/");
            Optional<Integer> idOpt = getTaskId(exchange);
            System.out.println(exchange.getRequestURI().getPath());


            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                return Endpoint.GET_PRIORITY;
            }
            if (("/tasks/task").equals(requestPath) && idOpt.isEmpty()) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASKS;
                }
                if (requestMethod.equals("POST")) {
                    return Endpoint.POST_TASK;
                }
                if (requestMethod.equals("DELETE")) {
                    return Endpoint.DELETE_ALL_TASKS;
                }
            }
            if (("/tasks/task").equals(requestPath) && idOpt.isPresent()) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASK_BY_ID;
                }
                if (requestMethod.equals("DELETE")) {
                    return Endpoint.DELETE_TASK_BY_ID;
                }
            }
            if (pathParts.length >= 3 && pathParts[1].equals("tasks") && pathParts[2].equals("subtask")) {
                return Endpoint.SUBTASKS_METHODS;
            }
            if (pathParts.length >= 3 && pathParts[1].equals("tasks") && pathParts[2].equals("epic")) {
                return Endpoint.EPICS_METHODS;
            }
            if (pathParts.length == 3 && pathParts[1].equals("tasks") && pathParts[2].equals("history")) {
                return Endpoint.GET_HISTORY;
            }
            return Endpoint.UNKNOWN;
        }


        private Optional<Integer> getTaskId(HttpExchange exchange) {
            String query = exchange.getRequestURI().getRawQuery();
            System.out.println(query);
            String[] pair = query.split("=");
            try {
                if (pair.length == 2 && Objects.equals(pair[0], "id")) {
                    return Optional.of(Integer.parseInt(pair[1]));
                } else {
                    return Optional.empty();
                }
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private String readBody(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if (responseString.isEmpty()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }
}