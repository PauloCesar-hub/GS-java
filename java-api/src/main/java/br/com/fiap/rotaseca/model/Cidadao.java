package br.com.fiap.rotaseca.model;

import java.time.LocalDate;

/** Tabela T_RS_CIDADAO. */
public class Cidadao {

    private Long id;
    private Long idBairro;
    private String nome;
    private String email;
    private String telefone;
    private PerfilCidadao perfil;
    private Boolean recebeAlerta;
    private LocalDate dataCadastro;

    public Cidadao() {
    }

    public Cidadao(Long id, Long idBairro, String nome, String email, String telefone, PerfilCidadao perfil,
                   Boolean recebeAlerta, LocalDate dataCadastro) {
        this.id = id;
        this.idBairro = idBairro;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.perfil = perfil;
        this.recebeAlerta = recebeAlerta;
        this.dataCadastro = dataCadastro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdBairro() { return idBairro; }
    public void setIdBairro(Long idBairro) { this.idBairro = idBairro; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public PerfilCidadao getPerfil() { return perfil; }
    public void setPerfil(PerfilCidadao perfil) { this.perfil = perfil; }
    public Boolean getRecebeAlerta() { return recebeAlerta; }
    public void setRecebeAlerta(Boolean recebeAlerta) { this.recebeAlerta = recebeAlerta; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
