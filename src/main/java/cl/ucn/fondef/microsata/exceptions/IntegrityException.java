/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.exceptions;

/**
 * In case of Data Integrity Violation.
 *
 * @author Diego Urrutia-Astorga.
 */
public class IntegrityException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     */
    public IntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

}
