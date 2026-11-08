package br.com.fiap.rotaseca.exception;

import java.util.List;

/** Lancada quando os dados enviados nao passam na validacao (HTTP 400). */
public class DadosInvalidosException extends RotaSecaException {

    private final List<String> erros;

    public DadosInvalidosException(List<String> erros) {
        super("Dados invalidos: " + String.join("; ", erros));
        this.erros = List.copyOf(erros);
    }

    public DadosInvalidosException(String erro) {
        this(List.of(erro));
    }

    public List<String> getErros() {
        return erros;
    }
}
