package br.com.fiap.rotaseca.model;

import java.time.LocalDateTime;

/** Tabela T_RS_LEITURA. valor = cm de lamina (NIVEL) ou mm/h de chuva (PLUVIOMETRO). */
public class Leitura {

    private Long id;
    private Long idSensor;
    private LocalDateTime dataLeitura;
    private Double valor;

    public Leitura() {
    }

    public Leitura(Long id, Long idSensor, LocalDateTime dataLeitura, Double valor) {
        this.id = id;
        this.idSensor = idSensor;
        this.dataLeitura = dataLeitura;
        this.valor = valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdSensor() { return idSensor; }
    public void setIdSensor(Long idSensor) { this.idSensor = idSensor; }
    public LocalDateTime getDataLeitura() { return dataLeitura; }
    public void setDataLeitura(LocalDateTime dataLeitura) { this.dataLeitura = dataLeitura; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
}
