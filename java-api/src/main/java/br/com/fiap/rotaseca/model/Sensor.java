package br.com.fiap.rotaseca.model;

import java.time.LocalDate;

/** Tabela T_RS_SENSOR. */
public class Sensor {

    private Long id;
    private Long idPonto;
    private String codigoSerie;
    private TipoSensor tipo;
    private LocalDate dataInstalacao;
    private StatusSensor status;

    public Sensor() {
    }

    public Sensor(Long id, Long idPonto, String codigoSerie, TipoSensor tipo, LocalDate dataInstalacao,
                  StatusSensor status) {
        this.id = id;
        this.idPonto = idPonto;
        this.codigoSerie = codigoSerie;
        this.tipo = tipo;
        this.dataInstalacao = dataInstalacao;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdPonto() { return idPonto; }
    public void setIdPonto(Long idPonto) { this.idPonto = idPonto; }
    public String getCodigoSerie() { return codigoSerie; }
    public void setCodigoSerie(String codigoSerie) { this.codigoSerie = codigoSerie; }
    public TipoSensor getTipo() { return tipo; }
    public void setTipo(TipoSensor tipo) { this.tipo = tipo; }
    public LocalDate getDataInstalacao() { return dataInstalacao; }
    public void setDataInstalacao(LocalDate dataInstalacao) { this.dataInstalacao = dataInstalacao; }
    public StatusSensor getStatus() { return status; }
    public void setStatus(StatusSensor status) { this.status = status; }
}
