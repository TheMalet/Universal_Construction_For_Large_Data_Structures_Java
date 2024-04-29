package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a set of registers and their corresponding states for read and write operations.
 */
public class NodeContext<R, E extends Exception> implements Context {

    /**
     * Set of registers and their corresponding states for write operations.
     */
    Map<Register<?>, Register.WriteOrder<?>> writeset = new HashMap<>();

    /**
     * Corresponding to the current operation
     */
    int timestamp;

    /**
     * Result of the operation. Allows to CAS the context atomically
     */
    R result;

    /**
     * If the excution of the operation throw an Exception (not AlreadyExecutedException)
     */
    boolean isException = false;

    E exception = null;

    public NodeContext(int timestamp) {
        this.timestamp = timestamp;
    }

    /** Update the context rwset of the node */
    void collapse () {
        writeset.forEach((r, s) -> s.update());
    }

    public String toString () {
        return String.format(writeset.toString());
    }

    @Override
    public int timestamp () {
        return timestamp;
    }

    @Override
    public Map<Register<?>, Register.WriteOrder<?>> writeset() {
        return writeset;
    }
}
