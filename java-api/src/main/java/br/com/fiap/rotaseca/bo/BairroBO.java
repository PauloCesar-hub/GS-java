package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.BairroDao;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.model.Bairro;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BairroBO {

    private final BairroDao dao;

    public BairroBO(BairroDao dao) {
        this.dao = dao;
    }

    public List<Bairro> listar() {
        return dao.listar();
    }

    public Bairro buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Bairro", id));
    }

    public Bairro criar(Bairro bairro) {
        validar(bairro);
        bairro.setId(null);
        return dao.inserir(bairro);
    }

    public Bairro atualizar(Long id, Bairro bairro) {
        buscar(id);
        validar(bairro);
        bairro.setId(id);
        dao.atualizar(bairro);
        return bairro;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    private void validar(Bairro b) {
        new Validador()
                .obrigatorio(b.getNome(), "nome").tamanhoMaximo(b.getNome(), 80, "nome")
                .obrigatorio(b.getSubprefeitura(), "subprefeitura").tamanhoMaximo(b.getSubprefeitura(), 80, "subprefeitura")
                .obrigatorio(b.getZona(), "zona")
                .validar();
    }
}
