package br.com.fiap.rotaseca.model;

import java.time.LocalDateTime;

/** Tabela T_RS_NOTIFICACAO: envio de um alerta para um cidadao. */
public class Notificacao {

    private Long id;
    private Long idAlerta;
    private Long idCidadao;
    private LocalDateTime dataEnvio;
    private CanalNotificacao canal;
    private Boolean lida;

    public Notificacao() {
    }

    public Notificacao(Long id, Long idAlerta, Long idCidadao, LocalDateTime dataEnvio, CanalNotificacao canal,
                       Boolean lida) {
        this.id = id;
        this.idAlerta = idAlerta;
        this.idCidadao = idCidadao;
        this.dataEnvio = dataEnvio;
        this.canal = canal;
        this.lida = lida;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }
    public Long getIdCidadao() { return idCidadao; }
    public void setIdCidadao(Long idCidadao) { this.idCidadao = idCidadao; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
    public CanalNotificacao getCanal() { return canal; }
    public void setCanal(CanalNotificacao canal) { this.canal = canal; }
    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }
}
