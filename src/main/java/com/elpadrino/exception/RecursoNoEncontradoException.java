package com.elpadrino.exception;

/** Se lanza cuando se busca un registro que no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con código " + id + " no existe");
    }
}
