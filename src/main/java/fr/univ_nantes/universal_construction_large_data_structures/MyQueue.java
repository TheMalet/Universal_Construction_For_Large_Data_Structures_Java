package fr.univ_nantes.universal_construction_large_data_structures;

/**
 * Lock-free queue that uses read/write operations of Register. Relieves the user from synchronization management
 * @param <T> type of the values in the Queue
 */
public class MyQueue<T> {

    static final Universal universal = new Universal();
    private final Register<QueueNode<T>> head;
    private final Register<QueueNode<T>> tail;

    /**
     * Node class.
     * @param <T>
     */
    static class QueueNode<T> {
        final T value;
        final Register<QueueNode<T>> next = new Register<>(null);

        public QueueNode (T value) {
            this.value = value;;
        }
    }

    /**
     * Constructor for an empty queue.
     */
    public MyQueue() {
        QueueNode<T> sentinel = new QueueNode<>(null);
        this.head = new Register<>(sentinel);
        this.tail = new Register<>(sentinel);
    }

    /**
     * Enqueues a value in a new node inside the queue.
     * @param value the new value
     */
    public void enqueue (T value) {
        //Uses call to add the value in a lock-free manner
        universal.call(() -> {
            QueueNode<T> node = new QueueNode<>(value);
            tail.get().next.set(node);
            tail.set(node);
        });
    }

    /**
     * Removes & returns the first value from the queue
     * @return the first value from the queue
     * @throws EmptyMyQueueException if the queue is empty
     */
    public T dequeue () throws EmptyMyQueueException {
        //Uses call to dequeue in a lock-free manner
        return universal.call(() -> {
            if (head.get() == tail.get()) {
                throw new EmptyMyQueueException();
            }
            QueueNode<T> newHead = head.get().next.get();
            head.set(newHead);
            return newHead.value;
        });
    }
}
