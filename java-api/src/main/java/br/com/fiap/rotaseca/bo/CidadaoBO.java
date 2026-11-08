package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.BairroDao;
import br.com.fiap.rotaseca.dao.CidadaoDao;
import br.com.fiap.rotaseca.exception.ConflitoException;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Cidadao;
import br.com.fiap.rotaseca.util.Relogio;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CidadaoBO {

    private final CidadaoDao dao;
    private final BairroDao bairroDao;

    public CidadaoBO(CidadaoDao dao, BairroDao bairroDao) {
        this.dao = dao;
        this.bairroDao = bairroDao;
    }

    public List<Cidadao> listar() {
        return dao.listar();
    }

    public Cidadao buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cidadao", id));
    }

    public Cidadao criar(Cidadao cidadao) {
        if (cidadao.getRecebeAlerta() == null) {
            cidadao.setRecebeAlerta(Boolean.TRUE);
        }
        cidadao.setDataCadastro(Relogio.hoje());
        normalizar(cidadao);
        validar(cidadao, null);
        cidadao.setId(null);
        return dao.inserir(cidadao);
    }

    public Cidadao atualizar(Long id, Cidadao cidadao) {
        Cidadao atual = buscar(id);
        if (cidadao.getRecebeAlerta() == null) {
            cidadao.setRecebeAlerta(atual.getRecebeAlerta());
        }
        cidadao.setDataCadastro(atual.getDataCadastro());
        normalizar(cidadao);
        validar(cidadao, id);
        cidadao.setId(id);
        dao.atualizar(cidadao);
        return cidadao;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    private void normalizar(Cidadao c) {
        if (c.getEmail() != null) {
            c.setEmail(c.getEmail().trim().toLowerCase());
        }
        if (c.getNome() != null) {
            c.setNome(c.getNome().trim());
        }
    }

    private void validar(Cidadao c, Long idAtual) {
        new Validador()
                .obrigatorio(c.getIdBairro(), "idBairro")
                .obrigatorio(c.getNome(), "nome").tamanhoMaximo(c.getNome(), 100, "nome")
                .obrigatorio(c.getEmail(), "email").tamanhoMaximo(c.getEmail(), 120, "email").email(c.getEmail(), "email")
                .tamanhoMaximo(c.getTelefone(), 20, "telefone")
                .obrigatorio(c.getPerfil(), "perfil")
                .validar();
        if (bairroDao.buscarPorId(c.getIdBairro()).isEmpty()) {
            throw new RegraNegocioException("Bairro " + c.getIdBairro() + " nao existe");
        }
        dao.buscarPorEmail(c.getEmail())
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new ConflitoException("E-mail " + c.getEmail() + " ja esta cadastrado");
                });
    }
}
