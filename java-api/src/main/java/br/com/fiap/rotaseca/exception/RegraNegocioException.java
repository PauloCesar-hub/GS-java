package br.com.fiap.rotaseca.exception;

/** Lancada quando a operacao viola uma regra de negocio (HTTP 422). */
public class RegraNegocioException extends RotaSecaException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
