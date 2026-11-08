package br.com.fiap.rotaseca.exception;

/** Falha inesperada de acesso ao banco de dados (HTTP 500). */
public class PersistenciaException extends RotaSecaException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
