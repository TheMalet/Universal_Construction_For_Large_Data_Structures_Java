package org.example;

/**
 * Exception throw in register.get() when another process already completed the same operation as the current process, and is in the rewrite phase
 */
public class AlreadyExecutedException extends Exception {

}
