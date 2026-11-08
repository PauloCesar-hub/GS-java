package br.com.fiap.rotaseca.model;

import java.time.LocalDateTime;

/** Tabela T_RS_ALERTA. Um alerta esta ativo enquanto dataEncerramento for nula. */
public class Alerta {

    private Long id;
    private Long idPonto;
    private NivelAlerta nivel;
    private String mensagem;
    private LocalDateTime dataEmissao;
    private LocalDateTime dataEncerramento;

    public Alerta() {
    }

    public Alerta(Long id, Long idPonto, NivelAlerta nivel, String mensagem, LocalDateTime dataEmissao,
                  LocalDateTime dataEncerramento) {
        this.id = id;
        this.idPonto = idPonto;
        this.nivel = nivel;
        this.mensagem = mensagem;
        this.dataEmissao = dataEmissao;
        this.dataEncerramento = dataEncerramento;
    }

    public boolean isAtivo() {
        return dataEncerramento == null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdPonto() { return idPonto; }
    public void setIdPonto(Long idPonto) { this.idPonto = idPonto; }
    public NivelAlerta getNivel() { return nivel; }
    public void setNivel(NivelAlerta nivel) { this.nivel = nivel; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }
    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime dataEncerramento) { this.dataEncerramento = dataEncerramento; }
}
