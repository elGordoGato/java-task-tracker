package Managers.historyManager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> history = new ArrayList<>();
    private static final Map<Integer, Node<Task>> nodeMap = new HashMap<>();
    private final customLinkedList<Task> linkedList = new customLinkedList<>();



    @Override
    public void add(Task task) {
        linkedList.removeNode(nodeMap.get(task.getID()));
        nodeMap.put(task.getID(), linkedList.linkLast(task));
    }
    @Override
    public void remove(int id){
       linkedList.removeNode(nodeMap.get(id));
       nodeMap.remove(id);
    }

    @Override
    public List<Task> getHistory(){
        history = linkedList.getTasks();
        return history;
    }


    public static class customLinkedList<E> {

        private Node<E> head;

        /**
         * Указатель на последний элемент списка. Он же last
         */
        private Node<E> tail;

        private int size = 0;


        public  Node<E> linkLast(E element) {
            final Node<E> oldTail = tail;
            final Node<E> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null){
                head = newNode;
            }
            else {
                oldTail.next = newNode;
            }
            size++;
            return tail;// Реализуйте метод
        }

        public List<E> getTasks() {
            List<E> customLinkedList = new ArrayList<>();
            for (Node<E> x = head; x != null; x = x.next) {
                customLinkedList.add(x.data);
                }

            return customLinkedList;    // Реализуйте метод
        }

        private void removeNode (Node<E> x) {
            if (x != null) {
                final E element = x.data;
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
                size--;
            }
        }


    }


}
