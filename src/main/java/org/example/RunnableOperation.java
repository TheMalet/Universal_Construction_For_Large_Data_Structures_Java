package org.example;

/**
 * This class represent a void operation on registers (i.e. a procedure).
 */
@FunctionalInterface
public interface RunnableOperation<E extends Exception> extends CallableOperation<Void, E> {
    /**
     * The operation to be executed on the registers. The operation should match the following syntax:
     * <p>
     * a read instruction on a register is written as {@code register.get();}
     * <p>
     * a write instruction on a register is written as {@code register.set(<value>);}
     * @throws E the exception(s) from the operation's code
     * @throws AlreadyExecutedException if the operation does not compute or stumble upon values with timestamp greater or equal to the invoker process' own timestamp.
     */
    void run () throws E, AlreadyExecutedException;

    /**
     * The operation to be executed on the registers
     * <p>
     * a read instruction on a register is written as {@code register.get();}
     * <p>
     * a write instruction on a register is written as {@code register.set(<value>);}
     * @return null
     * @throws E the exception(s) from the operation's code
     * @throws AlreadyExecutedException if the operation does not compute or stumble upon values with timestamp greater or equal to the invoker process' own timestamp.
     */
    @Override
    default Void call () throws E, AlreadyExecutedException {
        run();
        return null;
    }
}
