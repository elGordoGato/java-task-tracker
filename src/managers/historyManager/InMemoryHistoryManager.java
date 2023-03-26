package managers.historyManager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();
    private final customLinkedList<Task> linkedList = new customLinkedList<>();
    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        linkedList.removeNode(nodeMap.get(task.getID()));
        nodeMap.put(task.getID(), linkedList.linkLast(task));
    }

    @Override
    public void remove(int id) {
        linkedList.removeNode(nodeMap.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        history = linkedList.getTasks();
        return history;
    }

    public static class customLinkedList<E> {
        private Node<E> head;
        private Node<E> tail;

        public Node<E> linkLast(E element) {
            final Node<E> oldTail = tail;
            final Node<E> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            return tail;// Реализуйте метод
        }

        public List<E> getTasks() {
            List<E> customLinkedList = new ArrayList<>();
            for (Node<E> x = head; x != null; x = x.next) {
                customLinkedList.add(x.data);
            }

            return customLinkedList;
        }

        private void removeNode(Node<E> x) {
            if (x != null) {
                final Node<E> next = x.next;
                final Node<E> prev = x.prev;

                if (prev == null) {
                    head = next;
                } else {
                    prev.next = next;
                    x.prev = null;
                }
                if (next == null) {
                    tail = prev;
                } else {
                    next.prev = prev;
                    x.next = null;
                }
                x.data = null;
            }
        }
    }
}
