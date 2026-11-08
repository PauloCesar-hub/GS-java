package br.com.fiap.rotaseca.exception;

/** Lancada quando um registro procurado pelo id nao existe (HTTP 404). */
public class RecursoNaoEncontradoException extends RotaSecaException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " nao encontrado(a)");
    }
}
