package br.com.fiap.rotaseca.model;

/** Tabela T_RS_BAIRRO. */
public class Bairro {

    private Long id;
    private String nome;
    private String subprefeitura;
    private Zona zona;

    public Bairro() {
    }

    public Bairro(Long id, String nome, String subprefeitura, Zona zona) {
        this.id = id;
        this.nome = nome;
        this.subprefeitura = subprefeitura;
        this.zona = zona;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSubprefeitura() { return subprefeitura; }
    public void setSubprefeitura(String subprefeitura) { this.subprefeitura = subprefeitura; }
    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }
}
