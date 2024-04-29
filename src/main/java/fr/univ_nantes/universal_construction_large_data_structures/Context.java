package fr.univ_nantes.universal_construction_large_data_structures;

import java.util.Map;

/**
 * This class represents a set of registers and their corresponding states for read and write operations.
 */
interface Context {
    /**
     * Corresponding to the current operation
     */
    int timestamp ();

    /**
     * Set of registers and their corresponding states for write operations.
     */
    Map<Register<?>, Register.WriteOrder<?>> writeset ();

    /**
     * Allows us to store data that will be accessible only by a specific thread
     */
    static ThreadLocal<Context> context = new ThreadLocal<>();

}
