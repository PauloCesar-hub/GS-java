package br.com.fiap.rotaseca.dto;

import br.com.fiap.rotaseca.model.Leitura;

/** Resposta do POST de leitura: a leitura gravada e o que ela provocou nos alertas. */
public record LeituraRegistradaResponse(Leitura leitura, AvaliacaoNivelResponse avaliacao) {
}
