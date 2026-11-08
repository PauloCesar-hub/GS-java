package br.com.fiap.rotaseca.model;

import java.util.EnumSet;
import java.util.Set;

/** Ciclo de vida de um relato: ABERTA -> CONFIRMADA -> RESOLVIDA, ou ABERTA -> DESCARTADA. */
public enum StatusOcorrencia {
    ABERTA,
    CONFIRMADA,
    RESOLVIDA,
    DESCARTADA;

    public Set<StatusOcorrencia> proximosPermitidos() {
        return switch (this) {
            case ABERTA -> EnumSet.of(CONFIRMADA, DESCARTADA);
            case CONFIRMADA -> EnumSet.of(RESOLVIDA);
            case RESOLVIDA, DESCARTADA -> EnumSet.noneOf(StatusOcorrencia.class);
        };
    }

    public boolean podeMudarPara(StatusOcorrencia novo) {
        return this == novo || proximosPermitidos().contains(novo);
    }
}
