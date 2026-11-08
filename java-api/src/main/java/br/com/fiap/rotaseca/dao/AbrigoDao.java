package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Abrigo;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AbrigoDao extends BaseDao {

    private static final String COLUNAS = "id_abrigo, id_bairro, nm_abrigo, ds_endereco, nr_capacidade, st_acessivel";

    public AbrigoDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Abrigo> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_ABRIGO ORDER BY nm_abrigo", SEM_PARAMETROS, this::mapear);
    }

    public List<Abrigo> listarPorBairro(Long idBairro) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_ABRIGO WHERE id_bairro = ? ORDER BY nm_abrigo",
                ps -> ps.setLong(1, idBairro), this::mapear);
    }

    public Optional<Abrigo> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_ABRIGO WHERE id_abrigo = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public Abrigo inserir(Abrigo a) {
        long id = inserir("INSERT INTO T_RS_ABRIGO (id_bairro, nm_abrigo, ds_endereco, nr_capacidade, st_acessivel) "
                + "VALUES (?, ?, ?, ?, ?)", "ID_ABRIGO", ps -> preencher(ps, a));
        a.setId(id);
        return a;
    }

    public boolean atualizar(Abrigo a) {
        return executar("UPDATE T_RS_ABRIGO SET id_bairro = ?, nm_abrigo = ?, ds_endereco = ?, nr_capacidade = ?, "
                + "st_acessivel = ? WHERE id_abrigo = ?", ps -> {
                    preencher(ps, a);
                    ps.setLong(6, a.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_ABRIGO WHERE id_abrigo = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private void preencher(PreparedStatement ps, Abrigo a) throws SQLException {
        ps.setLong(1, a.getIdBairro());
        ps.setString(2, a.getNome());
        ps.setString(3, a.getEndereco());
        ps.setInt(4, a.getCapacidade());
        ps.setString(5, simNao(a.getAcessivel()));
    }

    private Abrigo mapear(ResultSet rs) throws SQLException {
        return new Abrigo(rs.getLong("id_abrigo"), rs.getLong("id_bairro"), rs.getString("nm_abrigo"),
                rs.getString("ds_endereco"), rs.getInt("nr_capacidade"), deSimNao(rs.getString("st_acessivel")));
    }
}
