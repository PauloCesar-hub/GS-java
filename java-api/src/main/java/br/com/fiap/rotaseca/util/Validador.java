package br.com.fiap.rotaseca.util;

import br.com.fiap.rotaseca.exception.DadosInvalidosException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Acumula erros de validacao e lanca DadosInvalidosException com todos eles de uma vez. */
public class Validador {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final List<String> erros = new ArrayList<>();

    public Validador obrigatorio(Object valor, String campo) {
        if (valor == null || (valor instanceof String s && s.isBlank())) {
            erros.add(campo + " e obrigatorio");
        }
        return this;
    }

    public Validador tamanhoMaximo(String valor, int maximo, String campo) {
        if (valor != null && valor.length() > maximo) {
            erros.add(campo + " deve ter no maximo " + maximo + " caracteres");
        }
        return this;
    }

    public Validador intervalo(Number valor, double minimo, double maximo, String campo) {
        if (valor != null && (valor.doubleValue() < minimo || valor.doubleValue() > maximo)) {
            erros.add(campo + " deve estar entre " + formatar(minimo) + " e " + formatar(maximo));
        }
        return this;
    }

    public Validador email(String valor, String campo) {
        if (valor != null && !valor.isBlank() && !EMAIL.matcher(valor).matches()) {
            erros.add(campo + " nao e um e-mail valido");
        }
        return this;
    }

    public Validador regra(boolean condicaoOk, String mensagem) {
        if (!condicaoOk) {
            erros.add(mensagem);
        }
        return this;
    }

    public void validar() {
        if (!erros.isEmpty()) {
            throw new DadosInvalidosException(erros);
        }
    }

    private static String formatar(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}
