package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.PontoRisco;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PontoRiscoDao extends BaseDao {

    private static final String COLUNAS = "id_ponto, id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, "
            + "nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo";

    public PontoRiscoDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<PontoRisco> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_PONTO_RISCO ORDER BY id_ponto", SEM_PARAMETROS, this::mapear);
    }

    public List<PontoRisco> listarAtivos() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_PONTO_RISCO WHERE st_ativo = 'S' ORDER BY id_ponto",
                SEM_PARAMETROS, this::mapear);
    }

    public List<PontoRisco> listarPorBairro(Long idBairro) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_PONTO_RISCO WHERE id_bairro = ? ORDER BY id_ponto",
                ps -> ps.setLong(1, idBairro), this::mapear);
    }

    public Optional<PontoRisco> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_PONTO_RISCO WHERE id_ponto = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public PontoRisco inserir(PontoRisco p) {
        long id = inserir("INSERT INTO T_RS_PONTO_RISCO (id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, "
                + "nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "ID_PONTO", ps -> preencher(ps, p));
        p.setId(id);
        return p;
    }

    public boolean atualizar(PontoRisco p) {
        return executar("UPDATE T_RS_PONTO_RISCO SET id_bairro = ?, nm_ponto = ?, ds_referencia = ?, nr_latitude = ?, "
                + "nr_longitude = ?, nr_cota_atencao_cm = ?, nr_cota_critica_cm = ?, st_ativo = ? WHERE id_ponto = ?",
                ps -> {
                    preencher(ps, p);
                    ps.setLong(9, p.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_PONTO_RISCO WHERE id_ponto = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private void preencher(PreparedStatement ps, PontoRisco p) throws SQLException {
        ps.setLong(1, p.getIdBairro());
        ps.setString(2, p.getNome());
        setTextoOuNulo(ps, 3, p.getReferencia());
        ps.setDouble(4, p.getLatitude());
        ps.setDouble(5, p.getLongitude());
        ps.setInt(6, p.getCotaAtencaoCm());
        ps.setInt(7, p.getCotaCriticaCm());
        ps.setString(8, simNao(p.getAtivo()));
    }

    private PontoRisco mapear(ResultSet rs) throws SQLException {
        return new PontoRisco(rs.getLong("id_ponto"), rs.getLong("id_bairro"), rs.getString("nm_ponto"),
                rs.getString("ds_referencia"), rs.getDouble("nr_latitude"), rs.getDouble("nr_longitude"),
                rs.getInt("nr_cota_atencao_cm"), rs.getInt("nr_cota_critica_cm"), deSimNao(rs.getString("st_ativo")));
    }
}
