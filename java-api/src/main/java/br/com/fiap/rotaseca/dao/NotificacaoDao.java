package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.CanalNotificacao;
import br.com.fiap.rotaseca.model.Notificacao;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class NotificacaoDao extends BaseDao {

    private static final String COLUNAS = "id_notificacao, id_alerta, id_cidadao, dt_envio, tp_canal, st_lida";

    public NotificacaoDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Notificacao> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_NOTIFICACAO ORDER BY dt_envio DESC, id_notificacao DESC",
                SEM_PARAMETROS, this::mapear);
    }

    public List<Notificacao> listarPorCidadao(Long idCidadao) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_NOTIFICACAO WHERE id_cidadao = ? "
                + "ORDER BY dt_envio DESC, id_notificacao DESC", ps -> ps.setLong(1, idCidadao), this::mapear);
    }

    public List<Notificacao> listarPorAlerta(Long idAlerta) {
        return listar("SELECT " + COLUNAS + " FROM T_RS_NOTIFICACAO WHERE id_alerta = ? ORDER BY id_notificacao",
                ps -> ps.setLong(1, idAlerta), this::mapear);
    }

    public Optional<Notificacao> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_NOTIFICACAO WHERE id_notificacao = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public boolean existe(Long idAlerta, Long idCidadao) {
        return contar("SELECT COUNT(*) FROM T_RS_NOTIFICACAO WHERE id_alerta = ? AND id_cidadao = ?", ps -> {
            ps.setLong(1, idAlerta);
            ps.setLong(2, idCidadao);
        }) > 0;
    }

    public Notificacao inserir(Notificacao n) {
        long id = inserir("INSERT INTO T_RS_NOTIFICACAO (id_alerta, id_cidadao, dt_envio, tp_canal, st_lida) "
                + "VALUES (?, ?, ?, ?, ?)", "ID_NOTIFICACAO", ps -> preencher(ps, n));
        n.setId(id);
        return n;
    }

    public boolean atualizar(Notificacao n) {
        return executar("UPDATE T_RS_NOTIFICACAO SET id_alerta = ?, id_cidadao = ?, dt_envio = ?, tp_canal = ?, "
                + "st_lida = ? WHERE id_notificacao = ?", ps -> {
                    preencher(ps, n);
                    ps.setLong(6, n.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_NOTIFICACAO WHERE id_notificacao = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private void preencher(PreparedStatement ps, Notificacao n) throws SQLException {
        ps.setLong(1, n.getIdAlerta());
        ps.setLong(2, n.getIdCidadao());
        ps.setTimestamp(3, ts(n.getDataEnvio()));
        ps.setString(4, n.getCanal().name());
        ps.setString(5, Boolean.TRUE.equals(n.getLida()) ? "S" : "N");
    }

    private Notificacao mapear(ResultSet rs) throws SQLException {
        return new Notificacao(rs.getLong("id_notificacao"), rs.getLong("id_alerta"), rs.getLong("id_cidadao"),
                deTs(rs.getTimestamp("dt_envio")), CanalNotificacao.valueOf(rs.getString("tp_canal")),
                deSimNao(rs.getString("st_lida")));
    }
}
