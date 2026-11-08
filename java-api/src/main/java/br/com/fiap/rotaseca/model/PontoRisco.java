package br.com.fiap.rotaseca.model;

/** Tabela T_RS_PONTO_RISCO: trecho de via com historico de alagamento. */
public class PontoRisco {

    private Long id;
    private Long idBairro;
    private String nome;
    private String referencia;
    private Double latitude;
    private Double longitude;
    private Integer cotaAtencaoCm;
    private Integer cotaCriticaCm;
    private Boolean ativo;

    public PontoRisco() {
    }

    public PontoRisco(Long id, Long idBairro, String nome, String referencia, Double latitude, Double longitude,
                      Integer cotaAtencaoCm, Integer cotaCriticaCm, Boolean ativo) {
        this.id = id;
        this.idBairro = idBairro;
        this.nome = nome;
        this.referencia = referencia;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cotaAtencaoCm = cotaAtencaoCm;
        this.cotaCriticaCm = cotaCriticaCm;
        this.ativo = ativo;
    }

    /**
     * Classifica uma altura de agua (cm) segundo as cotas deste ponto.
     * Abaixo da cota de atencao nao ha alerta (retorna null).
     * O nivel ALERTA comeca na metade do caminho entre a cota de atencao e a critica.
     */
    public NivelAlerta classificar(double alturaCm) {
        if (alturaCm >= cotaCriticaCm) {
            return NivelAlerta.EMERGENCIA;
        }
        double limiteAlerta = (cotaAtencaoCm + cotaCriticaCm) / 2.0;
        if (alturaCm >= limiteAlerta) {
            return NivelAlerta.ALERTA;
        }
        if (alturaCm >= cotaAtencaoCm) {
            return NivelAlerta.ATENCAO;
        }
        return null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdBairro() { return idBairro; }
    public void setIdBairro(Long idBairro) { this.idBairro = idBairro; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Integer getCotaAtencaoCm() { return cotaAtencaoCm; }
    public void setCotaAtencaoCm(Integer cotaAtencaoCm) { this.cotaAtencaoCm = cotaAtencaoCm; }
    public Integer getCotaCriticaCm() { return cotaCriticaCm; }
    public void setCotaCriticaCm(Integer cotaCriticaCm) { this.cotaCriticaCm = cotaCriticaCm; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
