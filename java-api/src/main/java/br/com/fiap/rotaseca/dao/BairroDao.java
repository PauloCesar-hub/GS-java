package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Bairro;
import br.com.fiap.rotaseca.model.Zona;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BairroDao extends BaseDao {

    private static final String COLUNAS = "id_bairro, nm_bairro, nm_subprefeitura, nm_zona";

    public BairroDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Bairro> listar() {
        return listar("SELECT " + COLUNAS + " FROM T_RS_BAIRRO ORDER BY nm_bairro", SEM_PARAMETROS, this::mapear);
    }

    public Optional<Bairro> buscarPorId(Long id) {
        return buscarUm("SELECT " + COLUNAS + " FROM T_RS_BAIRRO WHERE id_bairro = ?",
                ps -> ps.setLong(1, id), this::mapear);
    }

    public Bairro inserir(Bairro b) {
        long id = inserir("INSERT INTO T_RS_BAIRRO (nm_bairro, nm_subprefeitura, nm_zona) VALUES (?, ?, ?)",
                "ID_BAIRRO", ps -> {
                    ps.setString(1, b.getNome());
                    ps.setString(2, b.getSubprefeitura());
                    ps.setString(3, b.getZona().name());
                });
        b.setId(id);
        return b;
    }

    public boolean atualizar(Bairro b) {
        return executar("UPDATE T_RS_BAIRRO SET nm_bairro = ?, nm_subprefeitura = ?, nm_zona = ? WHERE id_bairro = ?",
                ps -> {
                    ps.setString(1, b.getNome());
                    ps.setString(2, b.getSubprefeitura());
                    ps.setString(3, b.getZona().name());
                    ps.setLong(4, b.getId());
                }) > 0;
    }

    public boolean excluir(Long id) {
        return executar("DELETE FROM T_RS_BAIRRO WHERE id_bairro = ?", ps -> ps.setLong(1, id)) > 0;
    }

    private Bairro mapear(ResultSet rs) throws SQLException {
        return new Bairro(rs.getLong("id_bairro"), rs.getString("nm_bairro"), rs.getString("nm_subprefeitura"),
                Zona.valueOf(rs.getString("nm_zona")));
    }
}
