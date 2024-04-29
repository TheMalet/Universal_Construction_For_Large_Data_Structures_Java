package fr.univ_nantes.universal_construction_large_data_structures;

/**
 * This class represent an operation on registers (i.e. a function).
 */
@FunctionalInterface
public interface CallableOperation<R, E extends Exception> {
    /**
     * The operation to be executed on the registers. The operation should match the following syntax:
     * <p>
     * a read instruction on a register is written as {@code register.get();}
     * <p>
     * a write instruction on a register is written as {@code register.set(<value>);}
     *
     * @return the result of the operation (can be null).
     * @throws E the exception(s) from the operation's code
     * @throws AlreadyExecutedException if the operation does not compute or stumble upon values with timestamp greater or equal to the invoker process' own timestamp.
     */
    R call () throws E, AlreadyExecutedException;
}
