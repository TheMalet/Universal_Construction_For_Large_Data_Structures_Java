package fr.univ_nantes.universal_construction_large_data_structures;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Store a shared value in the system.
 * @param <T> the type of the stored value.
 */
public class Register<T> {

    /**
     * The stored value and its last update timestamp. Initially, the timestamp is set at -1.
     */
    final AtomicReference<State<T>> state;

    /**
     * Constructor.
     * @param value the initial stored value.
     */
    public Register (T value) {
        state = new AtomicReference<>(new State<>(value, -1));
    }

    /**
     * Constructor with {@code null} as the base value.
     */
    public Register () {
        state = new AtomicReference<>(new State<>(null, -1));
    }

    /**
     * Reads the stored value and stores it in the invoker process' local Write set.
     * @return the stored value.
     * @throws AlreadyExecutedException if the Write set's timestamp is greater or equal to the stored value's timestamp.
     */
    public T get () throws AlreadyExecutedException {
        Context context = Context.context.get();
        State<T> s = state.get();

        if (s.timestamp() >= context.timestamp()) {
            // Another process already computed the operation, and is already rewriting the results in the shared memory
            throw new AlreadyExecutedException();
        }
        if (context.writeset().containsKey(this)) {
            // The operation already updated the value, so the process has to refer to its local value in the writeset
            // The cast to T is theoretically unnecessary because of the writeorder wrapping both register and the state with the same type
            return (T) context.writeset().get(this).newState.value;
        }
        return s.value();
    }

    /**
     * Writes a new value in the invoker process' local Write set.
     * @param newVal the new value to be stored.
     */
    public void set (T newVal) {
        Context context = Context.context.get();
        State<T> s = new State<>(newVal, context.timestamp());
        context.writeset().put(this, new WriteOrder<T>(this, s));
    }

    public String toString () {
        return state.toString();
    }

    /**
     * This record encapsulate a value and its corresponding timestamp
     * @param value the value
     * @param timestamp its corresponding timestamp
     * @param <T> the value's type
     */
    static record State<T>(T value, int timestamp){

        public String toString () {
            return String.format("[%s, %d]", value, timestamp);
        }

        public boolean equals (State<T> state) {
            return this.timestamp == state.timestamp() && this.value == state.value();
        }
    }

    /**
     * This record serves to wraps both registers and states in order to ensure a correct type inference.
     * @param register the register upon which the update will be performed
     * @param newState the new state to be stored in the register
     * @param <T> the type corresponding to both the register and the state
     */
    record WriteOrder<T> (Register<T> register, State<T> newState) {
        /**
         * Update the register with the new state. This methods functions as a maximizer: the loop runs as long as the register's timestamp is lower than the state's.
         */
        void update () {
            while (true) {
                State<T> localState = register.state.get();

                if (localState.timestamp >= newState.timestamp) {
                    return; // if the register's timestamp is greater than ours, then our value is outdated
                }
                register.state.compareAndSet(localState, newState);
            }
        }
    }
}
