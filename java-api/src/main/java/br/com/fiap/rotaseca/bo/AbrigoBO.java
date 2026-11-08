package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.AbrigoDao;
import br.com.fiap.rotaseca.dao.BairroDao;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Abrigo;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbrigoBO {

    private final AbrigoDao dao;
    private final BairroDao bairroDao;

    public AbrigoBO(AbrigoDao dao, BairroDao bairroDao) {
        this.dao = dao;
        this.bairroDao = bairroDao;
    }

    public List<Abrigo> listar(Long idBairro) {
        return idBairro == null ? dao.listar() : dao.listarPorBairro(idBairro);
    }

    public Abrigo buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Abrigo", id));
    }

    public Abrigo criar(Abrigo abrigo) {
        if (abrigo.getAcessivel() == null) {
            abrigo.setAcessivel(Boolean.FALSE);
        }
        validar(abrigo);
        abrigo.setId(null);
        return dao.inserir(abrigo);
    }

    public Abrigo atualizar(Long id, Abrigo abrigo) {
        Abrigo atual = buscar(id);
        if (abrigo.getAcessivel() == null) {
            abrigo.setAcessivel(atual.getAcessivel());
        }
        validar(abrigo);
        abrigo.setId(id);
        dao.atualizar(abrigo);
        return abrigo;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    private void validar(Abrigo a) {
        new Validador()
                .obrigatorio(a.getIdBairro(), "idBairro")
                .obrigatorio(a.getNome(), "nome").tamanhoMaximo(a.getNome(), 100, "nome")
                .obrigatorio(a.getEndereco(), "endereco").tamanhoMaximo(a.getEndereco(), 150, "endereco")
                .obrigatorio(a.getCapacidade(), "capacidade").intervalo(a.getCapacidade(), 1, 99999, "capacidade")
                .validar();
        if (bairroDao.buscarPorId(a.getIdBairro()).isEmpty()) {
            throw new RegraNegocioException("Bairro " + a.getIdBairro() + " nao existe");
        }
    }
}
