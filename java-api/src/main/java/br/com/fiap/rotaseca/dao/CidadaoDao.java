package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Cidadao;
import br.com.fiap.rotaseca.model.PerfilCidadao;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CidadaoDao extends BaseDao {

    private static final String COLUNAS = "id_cidadao, id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, "
            + "st_recebe_alerta, dt_cadastro";

    public CidadaoDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Cidadao> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_CIDADAO ORDER BY nm_cidadao", SEM_PARAMETROS, this::mapear);
    }

    public Optional<Cidadao> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_CIDADAO WHERE id_cidadao = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public Optional<Cidadao> buscarPorEmail(String email) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_CIDADAO WHERE LOWER(ds_email) = LOWER(?)",
                ps -> ps.setString(1, email), this::mapear);
    }

    /** Cidadaos do bairro que aceitaram receber alertas (destinatarios das notificacoes). */
    public List<Cidadao> listarQueRecebemAlerta(Long idBairro) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_CIDADAO WHERE id_bairro = ? AND st_recebe_alerta = 'S' "
                + "ORDER BY id_cidadao", ps -> ps.setLong(1, idBairro), this::mapear);
    }

    public Cidadao inserir(Cidadao c) {
        long id = inserir("INSERT INTO T_RS_CIDADAO (id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, "
                + "st_recebe_alerta, dt_cadastro) VALUES (?, ?, ?, ?, ?, ?, ?)", "ID_CIDADAO", ps -> preencher(ps, c));
        c.setId(id);
        return c;
    }

    public boolean atualizar(Cidadao c) {
        return executar("UPDATE T_RS_CIDADAO SET id_bairro = ?, nm_cidadao = ?, ds_email = ?, nr_telefone = ?, "
                + "tp_perfil = ?, st_recebe_alerta = ?, dt_cadastro = ? WHERE id_cidadao = ?", ps -> {
                    preencher(ps, c);
                    ps.setLong(8, c.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_CIDADAO WHERE id_cidadao = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private void preencher(PreparedStatement ps, Cidadao c) throws SQLException {
        ps.setLong(1, c.getIdBairro());
        ps.setString(2, c.getNome());
        ps.setString(3, c.getEmail());
        setTextoOuNulo(ps, 4, c.getTelefone());
        ps.setString(5, c.getPerfil().name());
        ps.setString(6, simNao(c.getRecebeAlerta()));
        ps.setDate(7, dt(c.getDataCadastro()));
    }

    private Cidadao mapear(ResultSet rs) throws SQLException {
        return new Cidadao(rs.getLong("id_cidadao"), rs.getLong("id_bairro"), rs.getString("nm_cidadao"),
                rs.getString("ds_email"), rs.getString("nr_telefone"), PerfilCidadao.valueOf(rs.getString("tp_perfil")),
                deSimNao(rs.getString("st_recebe_alerta")), deDt(rs.getDate("dt_cadastro")));
    }
}
