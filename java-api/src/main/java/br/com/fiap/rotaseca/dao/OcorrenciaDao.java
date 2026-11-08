package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Ocorrencia;
import br.com.fiap.rotaseca.model.StatusOcorrencia;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class OcorrenciaDao extends BaseDao {

    private static final String COLUNAS = "id_ocorrencia, id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, "
            + "ds_ocorrencia, st_ocorrencia";

    public OcorrenciaDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Ocorrencia> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_OCORRENCIA ORDER BY dt_ocorrencia DESC, id_ocorrencia DESC",
                SEM_PARAMETROS, this::mapear);
    }

    public List<Ocorrencia> listarPorStatus(StatusOcorrencia status) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_OCORRENCIA WHERE st_ocorrencia = ? "
                + "ORDER BY dt_ocorrencia DESC, id_ocorrencia DESC", ps -> ps.setString(1, status.name()), this::mapear);
    }

    public List<Ocorrencia> listarPorPonto(Long idPonto) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_OCORRENCIA WHERE id_ponto = ? "
                + "ORDER BY dt_ocorrencia DESC, id_ocorrencia DESC", ps -> ps.setLong(1, idPonto), this::mapear);
    }

    public Optional<Ocorrencia> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_OCORRENCIA WHERE id_ocorrencia = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public Ocorrencia inserir(Ocorrencia o) {
        long id = inserir("INSERT INTO T_RS_OCORRENCIA (id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, "
                + "ds_ocorrencia, st_ocorrencia) VALUES (?, ?, ?, ?, ?, ?)", "ID_OCORRENCIA", ps -> preencher(ps, o));
        o.setId(id);
        return o;
    }

    public boolean atualizar(Ocorrencia o) {
        return executar("UPDATE T_RS_OCORRENCIA SET id_cidadao = ?, id_ponto = ?, dt_ocorrencia = ?, "
                + "nr_altura_agua_cm = ?, ds_ocorrencia = ?, st_ocorrencia = ? WHERE id_ocorrencia = ?", ps -> {
                    preencher(ps, o);
                    ps.setLong(7, o.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_OCORRENCIA WHERE id_ocorrencia = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private void preencher(PreparedStatement ps, Ocorrencia o) throws SQLException {
        ps.setLong(1, o.getIdCidadao());
        ps.setLong(2, o.getIdPonto());
        ps.setTimestamp(3, ts(o.getDataOcorrencia()));
        ps.setInt(4, o.getAlturaAguaCm());
        setTextoOuNulo(ps, 5, o.getDescricao());
        ps.setString(6, o.getStatus().name());
    }

    private Ocorrencia mapear(ResultSet rs) throws SQLException {
        return new Ocorrencia(rs.getLong("id_ocorrencia"), rs.getLong("id_cidadao"), rs.getLong("id_ponto"),
                deTs(rs.getTimestamp("dt_ocorrencia")), rs.getInt("nr_altura_agua_cm"), rs.getString("ds_ocorrencia"),
                StatusOcorrencia.valueOf(rs.getString("st_ocorrencia")));
    }
}
