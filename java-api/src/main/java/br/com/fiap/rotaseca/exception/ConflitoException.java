package br.com.fiap.rotaseca.exception;

/** Lancada em conflitos de integridade: duplicidade ou registro com dependentes (HTTP 409). */
public class ConflitoException extends RotaSecaException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }

    public ConflitoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
