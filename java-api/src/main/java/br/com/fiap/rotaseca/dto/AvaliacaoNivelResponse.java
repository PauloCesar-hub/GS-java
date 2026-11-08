package br.com.fiap.rotaseca.dto;

import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.NivelAlerta;

/**
 * Resultado da avaliacao automatica de nivel de agua de um ponto.
 * nivelCalculado = null significa agua abaixo da cota de atencao (situacao normal).
 */
public record AvaliacaoNivelResponse(NivelAlerta nivelCalculado, Alerta alertaEmitido, int alertasEncerrados,
                                     int notificacoesEnviadas) {

    public static AvaliacaoNivelResponse semMudanca(NivelAlerta nivel) {
        return new AvaliacaoNivelResponse(nivel, null, 0, 0);
    }
}
