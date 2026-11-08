package br.com.fiap.rotaseca.exception;

/** Excecao base da aplicacao. Todas as excecoes de negocio herdam desta classe. */
public abstract class RotaSecaException extends RuntimeException {

    protected RotaSecaException(String mensagem) {
        super(mensagem);
    }

    protected RotaSecaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
