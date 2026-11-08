package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Leitura;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class LeituraDao extends BaseDao {

    private static final String COLUNAS = "l.id_leitura, l.id_sensor, l.dt_leitura, l.nr_valor";

    public LeituraDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Leitura> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_LEITURA l ORDER BY l.dt_leitura DESC, l.id_leitura DESC",
                SEM_PARAMETROS, this::mapear);
    }

    public List<Leitura> listarPorSensor(Long idSensor) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_LEITURA l WHERE l.id_sensor = ? "
                + "ORDER BY l.dt_leitura DESC, l.id_leitura DESC", ps -> ps.setLong(1, idSensor), this::mapear);
    }

    public Optional<Leitura> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_LEITURA l WHERE l.id_leitura = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    /** Leitura mais recente de um sensor do tipo NIVEL instalado no ponto. */
    public Optional<Leitura> ultimaLeituraDeNivel(Long idPonto) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_LEITURA l "
                + "INNER JOIN T_RS_SENSOR s ON s.id_sensor = l.id_sensor "
                + "WHERE s.id_ponto = ? AND s.tp_sensor = 'NIVEL' "
                + "ORDER BY l.dt_leitura DESC, l.id_leitura DESC FETCH FIRST 1 ROWS ONLY",
                ps -> ps.setLong(1, idPonto), this::mapear);
    }

    public Leitura inserir(Leitura l) {
        long id = inserir("INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (?, ?, ?)",
                "ID_LEITURA", ps -> {
                    ps.setLong(1, l.getIdSensor());
                    ps.setTimestamp(2, ts(l.getDataLeitura()));
                    ps.setDouble(3, l.getValor());
                });
        l.setId(id);
        return l;
    }

    public boolean atualizar(Leitura l) {
        return executar("UPDATE T_RS_LEITURA SET id_sensor = ?, dt_leitura = ?, nr_valor = ? WHERE id_leitura = ?",
                ps -> {
                    ps.setLong(1, l.getIdSensor());
                    ps.setTimestamp(2, ts(l.getDataLeitura()));
                    ps.setDouble(3, l.getValor());
                    ps.setLong(4, l.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_LEITURA WHERE id_leitura = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private Leitura mapear(ResultSet rs) throws SQLException {
        return new Leitura(rs.getLong("id_leitura"), rs.getLong("id_sensor"), deTs(rs.getTimestamp("dt_leitura")),
                rs.getDouble("nr_valor"));
    }
}
