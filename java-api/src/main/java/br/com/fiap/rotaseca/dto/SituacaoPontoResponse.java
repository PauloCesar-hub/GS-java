package br.com.fiap.rotaseca.dto;

import br.com.fiap.rotaseca.model.Alerta;

import java.time.LocalDateTime;

/**
 * Visao consolidada de um ponto de risco para a tela inicial do app.
 * situacao: NORMAL, ATENCAO, ALERTA, EMERGENCIA ou SEM_DADOS.
 */
public record SituacaoPontoResponse(Long idPonto, String nome, String referencia, Double latitude, Double longitude,
                                    Integer cotaAtencaoCm, Integer cotaCriticaCm, Double ultimaAlturaCm,
                                    LocalDateTime dataUltimaLeitura, String situacao, Alerta alertaAtivo,
                                    long ocorrenciasAbertas) {
}
