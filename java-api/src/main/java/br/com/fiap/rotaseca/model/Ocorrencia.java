package br.com.fiap.rotaseca.model;

import java.time.LocalDateTime;

/** Tabela T_RS_OCORRENCIA: relato de alagamento feito por um morador. */
public class Ocorrencia {

    private Long id;
    private Long idCidadao;
    private Long idPonto;
    private LocalDateTime dataOcorrencia;
    private Integer alturaAguaCm;
    private String descricao;
    private StatusOcorrencia status;

    public Ocorrencia() {
    }

    public Ocorrencia(Long id, Long idCidadao, Long idPonto, LocalDateTime dataOcorrencia, Integer alturaAguaCm,
                      String descricao, StatusOcorrencia status) {
        this.id = id;
        this.idCidadao = idCidadao;
        this.idPonto = idPonto;
        this.dataOcorrencia = dataOcorrencia;
        this.alturaAguaCm = alturaAguaCm;
        this.descricao = descricao;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdCidadao() { return idCidadao; }
    public void setIdCidadao(Long idCidadao) { this.idCidadao = idCidadao; }
    public Long getIdPonto() { return idPonto; }
    public void setIdPonto(Long idPonto) { this.idPonto = idPonto; }
    public LocalDateTime getDataOcorrencia() { return dataOcorrencia; }
    public void setDataOcorrencia(LocalDateTime dataOcorrencia) { this.dataOcorrencia = dataOcorrencia; }
    public Integer getAlturaAguaCm() { return alturaAguaCm; }
    public void setAlturaAguaCm(Integer alturaAguaCm) { this.alturaAguaCm = alturaAguaCm; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }
}
