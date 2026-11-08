package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Sensor;
import br.com.fiap.rotaseca.model.StatusSensor;
import br.com.fiap.rotaseca.model.TipoSensor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SensorDao extends BaseDao {

    private static final String COLUNAS = "id_sensor, id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor";

    public SensorDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Sensor> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_SENSOR ORDER BY id_sensor", SEM_PARAMETROS, this::mapear);
    }

    public List<Sensor> listarPorPonto(Long idPonto) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_SENSOR WHERE id_ponto = ? ORDER BY id_sensor",
                ps -> ps.setLong(1, idPonto), this::mapear);
    }

    public Optional<Sensor> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_SENSOR WHERE id_sensor = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public Optional<Sensor> buscarPorCodigoSerie(String codigo) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_SENSOR WHERE cd_serie = ?",
                ps -> ps.setString(1, codigo), this::mapear);
    }

    public Sensor inserir(Sensor s) {
        long id = inserir("INSERT INTO T_RS_SENSOR (id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor) "
                + "VALUES (?, ?, ?, ?, ?)", "ID_SENSOR", ps -> preencher(ps, s));
        s.setId(id);
        return s;
    }

    public boolean atualizar(Sensor s) {
        return executar("UPDATE T_RS_SENSOR SET id_ponto = ?, cd_serie = ?, tp_sensor = ?, dt_instalacao = ?, "
                + "st_sensor = ? WHERE id_sensor = ?", ps -> {
                    preencher(ps, s);
                    ps.setLong(6, s.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_SENSOR WHERE id_sensor = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private void preencher(PreparedStatement ps, Sensor s) throws SQLException {
        ps.setLong(1, s.getIdPonto());
        ps.setString(2, s.getCodigoSerie());
        ps.setString(3, s.getTipo().name());
        ps.setDate(4, dt(s.getDataInstalacao()));
        ps.setString(5, s.getStatus().name());
    }

    private Sensor mapear(ResultSet rs) throws SQLException {
        return new Sensor(rs.getLong("id_sensor"), rs.getLong("id_ponto"), rs.getString("cd_serie"),
                TipoSensor.valueOf(rs.getString("tp_sensor")), deDt(rs.getDate("dt_instalacao")),
                StatusSensor.valueOf(rs.getString("st_sensor")));
    }
}
