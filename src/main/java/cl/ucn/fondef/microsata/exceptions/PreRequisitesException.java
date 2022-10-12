/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.exceptions;

/**
 * In case of pre-requisites violation.
 *
 * @author Diego Urrutia-Astorga.
 */
public class PreRequisitesException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     */
    public PreRequisitesException(String message) {
        super(message);
    }

}
