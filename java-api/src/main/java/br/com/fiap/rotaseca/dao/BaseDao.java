package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.exception.ConflitoException;
import br.com.fiap.rotaseca.exception.PersistenciaException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe base dos DAOs: concentra a abertura/fechamento de conexoes (JDBC puro com
 * PreparedStatement) e a traducao de SQLException para as excecoes da aplicacao.
 */
public abstract class BaseDao {

    /** Preenche os parametros (?) de um PreparedStatement. */
    @FunctionalInterface
    protected interface Parametros {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    /** Converte a linha atual do ResultSet em um objeto do modelo. */
    @FunctionalInterface
    protected interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    protected static final Parametros SEM_PARAMETROS = ps -> { };

    private final DataSource dataSource;

    protected BaseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected <T> List<T> listar(String sql, Parametros parametros, Mapeador<T> mapeador) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            parametros.aplicar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    protected <T> Optional<T> buscarUm(String sql, Parametros parametros, Mapeador<T> mapeador) {
        List<T> lista = listar(sql, parametros, mapeador);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    /** Executa um INSERT e devolve o id gerado pela coluna IDENTITY informada. */
    protected long inserir(String sql, String colunaId, Parametros parametros) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[] {colunaId})) {
            parametros.aplicar(ps);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getLong(1);
                }
                throw new PersistenciaException("O banco nao retornou o id gerado", null);
            }
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    /** Executa UPDATE/DELETE e devolve a quantidade de linhas afetadas. */
    protected int executar(String sql, Parametros parametros) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            parametros.aplicar(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    protected long contar(String sql, Parametros parametros) {
        return buscarUm(sql, parametros, rs -> rs.getLong(1)).orElse(0L);
    }

    /**
     * Violacoes de integridade (SQLState 23xxx no H2; ORA-00001/02291/02292 no Oracle)
     * viram ConflitoException (HTTP 409); o resto vira PersistenciaException (HTTP 500).
     */
    protected RuntimeException traduzir(SQLException e) {
        String estado = e.getSQLState();
        int codigo = e.getErrorCode();
        boolean integridade = e instanceof SQLIntegrityConstraintViolationException
                || (estado != null && estado.startsWith("23"))
                || codigo == 1 || codigo == 2291 || codigo == 2292;
        if (integridade) {
            if (codigo == 2292 || "23503".equals(estado)) {
                return new ConflitoException("Registro possui dados vinculados e nao pode ser excluido", e);
            }
            if (codigo == 2291 || "23506".equals(estado)) {
                return new ConflitoException("Registro relacionado (chave estrangeira) nao existe", e);
            }
            return new ConflitoException("Registro duplicado ou violando restricao do banco", e);
        }
        return new PersistenciaException("Erro de acesso ao banco: " + e.getMessage(), e);
    }

    // ----------------------------------------------------------- utilitarios de conversao

    protected static String simNao(Boolean valor) {
        return Boolean.FALSE.equals(valor) ? "N" : "S";
    }

    protected static boolean deSimNao(String valor) {
        return "S".equalsIgnoreCase(valor);
    }

    protected static Timestamp ts(LocalDateTime data) {
        return data == null ? null : Timestamp.valueOf(data);
    }

    protected static LocalDateTime deTs(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    protected static Date dt(LocalDate data) {
        return data == null ? null : Date.valueOf(data);
    }

    protected static LocalDate deDt(Date data) {
        return data == null ? null : data.toLocalDate();
    }

    protected static void setTextoOuNulo(PreparedStatement ps, int indice, String valor) throws SQLException {
        if (valor == null || valor.isBlank()) {
            ps.setNull(indice, Types.VARCHAR);
        } else {
            ps.setString(indice, valor);
        }
    }

    protected static void setTimestampOuNulo(PreparedStatement ps, int indice, LocalDateTime valor)
            throws SQLException {
        if (valor == null) {
            ps.setNull(indice, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(indice, ts(valor));
        }
    }
}
