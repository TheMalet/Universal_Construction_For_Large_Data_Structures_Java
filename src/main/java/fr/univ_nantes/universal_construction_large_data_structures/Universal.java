package fr.univ_nantes.universal_construction_large_data_structures;

import java.util.concurrent.atomic.AtomicReference;

public class Universal {
    AtomicReference<Node<?, ?>> head = new AtomicReference<>(null);

    /**
     * This method executes an operation of enqueue on a node in a {@code lock-free} manner.
     * <P>
     * It creates a new node from the provided operation.
     * The node tries to atomically replace the old node with the new one.
     * If the replacement succeeds, the attributes of the new node are updated with the values from the old node, and then its invocation is returned.
     *
     * @param operation The operation to execute on the node.
     * @return The result returned by the operation's invocation.
     */
    public <R, E extends Exception> R call (CallableOperation<R, E> operation) throws E {

        while (true) {
            Node<?, ?> old = head.get();
            Node<R, E> node = new Node<>(operation, old);

            if (head.compareAndSet(old, node)) {
                node.invoke();
                var set = node.rwset.get();

                if (set.isException) {
                    // If the operation resulted in an exception, this exception is raised to the calling code and is expected to be handled there
                    throw set.exception;
                }

                return set.result;
            }
        }
    }

    /**
     * This method executes an operation of enqueue on a node in a {@code lock-free} manner.
     * <P>
     * This method serves as a stub for void operations.
     *
     * @param operation The operation to execute on the node.
     */
    public <E extends Exception> void call (RunnableOperation<E> operation) throws E {
        call((CallableOperation<Void, E>) operation);
    }
}
