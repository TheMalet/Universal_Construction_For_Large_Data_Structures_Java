package fr.univ_nantes.universal_construction_large_data_structures;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is a node of the linked list.
 */
public class Node<R, E extends Exception> {

    /**
     * A local set of registers and their corresponding states for read and write operations.
     */
    final AtomicReference<NodeContext<R, E>> rwset = new AtomicReference<>(null);

    /**
     * The operation that will be executed by the invoke method.
     */
    final CallableOperation<R, E> operation;

    /**
     * The operation's timestamp. It is initially valued at 0 but any subsequent node will increment its own timestamp based on the previous operation.
     */
    final int timestamp;

    /**
     * A reference to the previous node.
     */
    volatile Node<?, ?> previous;

    /**
     * The Constructor of the Node class.
     * @param operation the operation to be executed.
     */
    public Node (CallableOperation<R, E> operation, Node<?, ?> previous) {
        this.operation = operation;
        this.previous = previous;
        if (this.previous == null) {
            this.timestamp = 0;
        } else {
            this.timestamp = previous.timestamp + 1;
        }
    }

    /**
     * This method will try to help the previous node to perform its operation.
     * Otherwise, it will run its own operation and fill the rwset with the rwset of the operation
     */
    public void invoke () {
        Node<?, ?> p = previous;

        //Helping previous node to complete its operation
        if (p != null) {
            p.invoke();
        }


        if (rwset.get() == null) {
            // The current node's operation isn't complete so the process computes it
            NodeContext<R, E> t = new NodeContext<>(timestamp);
            Context.context.set(t);

            try {
                // The process tries to compute the operation
                t.result = operation.call();
                // The context is placed in the node to share its results, and renders the update deterministic
                rwset.compareAndSet(null, t);
            } catch (AlreadyExecutedException e) {
                // Empty catch block design to prevent execution on incoherent shared state
            } catch (Exception other) {
                // The computation led to an error due to the operation's code. The context is returned, but as an error to be handled by the calling code.
                t.isException = true;
                t.exception = (E) other;
                rwset.compareAndSet(null, t);
            }
        }
        var set = rwset.get();
        // In any case, the shared memory is updated to reflect its new state.
        set.collapse();
        // The previous field is set to null in order to tag the current node as the last operation executed.
        previous = null;
    }
}
