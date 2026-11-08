package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.NivelAlerta;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AlertaDao extends BaseDao {

    private static final String COLUNAS = "id_alerta, id_ponto, tp_nivel, ds_mensagem, dt_emissao, dt_encerramento";

    public AlertaDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Alerta> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_ALERTA ORDER BY dt_emissao DESC, id_alerta DESC",
                SEM_PARAMETROS, this::mapear);
    }

    public List<Alerta> listarAtivos() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_ALERTA WHERE dt_encerramento IS NULL "
                + "ORDER BY dt_emissao DESC, id_alerta DESC", SEM_PARAMETROS, this::mapear);
    }

    public List<Alerta> listarAtivosPorPonto(Long idPonto) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_ALERTA WHERE id_ponto = ? AND dt_encerramento IS NULL "
                + "ORDER BY dt_emissao DESC, id_alerta DESC", ps -> ps.setLong(1, idPonto), this::mapear);
    }

    public Optional<Alerta> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_ALERTA WHERE id_alerta = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public Alerta inserir(Alerta a) {
        long id = inserir("INSERT INTO T_RS_ALERTA (id_ponto, tp_nivel, ds_mensagem, dt_emissao, dt_encerramento) "
                + "VALUES (?, ?, ?, ?, ?)", "ID_ALERTA", ps -> preencher(ps, a));
        a.setId(id);
        return a;
    }

    public boolean atualizar(Alerta a) {
        return executar("UPDATE T_RS_ALERTA SET id_ponto = ?, tp_nivel = ?, ds_mensagem = ?, dt_emissao = ?, "
                + "dt_encerramento = ? WHERE id_alerta = ?", ps -> {
                    preencher(ps, a);
                    ps.setLong(6, a.getId());
                }) > 0;
    }

    /** Encerra todos os alertas ativos do ponto e devolve quantos foram encerrados. */
    public int encerrarAtivosDoPonto(Long idPonto, LocalDateTime quando) {
        return executar("UPDATE T_RS_ALERTA SET dt_encerramento = ? WHERE id_ponto = ? AND dt_encerramento IS NULL",
                ps -> {
                    ps.setTimestamp(1, ts(quando));
                    ps.setLong(2, idPonto);
                });
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_ALERTA WHERE id_alerta = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private void preencher(PreparedStatement ps, Alerta a) throws SQLException {
        ps.setLong(1, a.getIdPonto());
        ps.setString(2, a.getNivel().name());
        ps.setString(3, a.getMensagem());
        ps.setTimestamp(4, ts(a.getDataEmissao()));
        setTimestampOuNulo(ps, 5, a.getDataEncerramento());
    }

    private Alerta mapear(ResultSet rs) throws SQLException {
        return new Alerta(rs.getLong("id_alerta"), rs.getLong("id_ponto"), NivelAlerta.valueOf(rs.getString("tp_nivel")),
                rs.getString("ds_mensagem"), deTs(rs.getTimestamp("dt_emissao")),
                deTs(rs.getTimestamp("dt_encerramento")));
    }
}
