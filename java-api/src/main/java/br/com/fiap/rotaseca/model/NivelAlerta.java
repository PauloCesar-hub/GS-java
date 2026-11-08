package br.com.fiap.rotaseca.model;

/** Niveis de alerta, do menos para o mais grave. A ordem do enum e usada nas comparacoes. */
public enum NivelAlerta {
    ATENCAO,
    ALERTA,
    EMERGENCIA;

    public boolean maisGraveQue(NivelAlerta outro) {
        return outro == null || this.ordinal() > outro.ordinal();
    }
}
