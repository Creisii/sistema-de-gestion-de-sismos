package com.grupo6.sistema.repository;

/**
 * Error de acceso a datos con un mensaje entendible para el usuario.
 * Así los servlets no dependen de las excepciones propias de JPA o de MySQL.
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
