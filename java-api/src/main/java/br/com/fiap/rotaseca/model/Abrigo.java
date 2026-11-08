package br.com.fiap.rotaseca.model;

/** Tabela T_RS_ABRIGO: ponto de apoio para a populacao em chuva forte. */
public class Abrigo {

    private Long id;
    private Long idBairro;
    private String nome;
    private String endereco;
    private Integer capacidade;
    private Boolean acessivel;

    public Abrigo() {
    }

    public Abrigo(Long id, Long idBairro, String nome, String endereco, Integer capacidade, Boolean acessivel) {
        this.id = id;
        this.idBairro = idBairro;
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
        this.acessivel = acessivel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdBairro() { return idBairro; }
    public void setIdBairro(Long idBairro) { this.idBairro = idBairro; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
    public Boolean getAcessivel() { return acessivel; }
    public void setAcessivel(Boolean acessivel) { this.acessivel = acessivel; }
}
