package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.AlertaDao;
import br.com.fiap.rotaseca.dao.CidadaoDao;
import br.com.fiap.rotaseca.dao.NotificacaoDao;
import br.com.fiap.rotaseca.exception.ConflitoException;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Notificacao;
import br.com.fiap.rotaseca.util.Relogio;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoBO {

    private final NotificacaoDao dao;
    private final AlertaDao alertaDao;
    private final CidadaoDao cidadaoDao;

    public NotificacaoBO(NotificacaoDao dao, AlertaDao alertaDao, CidadaoDao cidadaoDao) {
        this.dao = dao;
        this.alertaDao = alertaDao;
        this.cidadaoDao = cidadaoDao;
    }

    public List<Notificacao> listar(Long idCidadao, Long idAlerta) {
        if (idCidadao != null) {
            return dao.listarPorCidadao(idCidadao).stream()
                    .filter(n -> idAlerta == null || n.getIdAlerta().equals(idAlerta)).toList();
        }
        return idAlerta == null ? dao.listar() : dao.listarPorAlerta(idAlerta);
    }

    public Notificacao buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Notificacao", id));
    }

    public Notificacao criar(Notificacao n) {
        if (n.getDataEnvio() == null) {
            n.setDataEnvio(Relogio.agora());
        }
        if (n.getLida() == null) {
            n.setLida(Boolean.FALSE);
        }
        validar(n);
        if (dao.existe(n.getIdAlerta(), n.getIdCidadao())) {
            throw new ConflitoException("O cidadao " + n.getIdCidadao() + " ja foi notificado do alerta " + n.getIdAlerta());
        }
        n.setId(null);
        return dao.inserir(n);
    }

    public Notificacao atualizar(Long id, Notificacao n) {
        Notificacao atual = buscar(id);
        if (n.getDataEnvio() == null) {
            n.setDataEnvio(atual.getDataEnvio());
        }
        if (n.getLida() == null) {
            n.setLida(atual.getLida());
        }
        validar(n);
        boolean trocouPar = !atual.getIdAlerta().equals(n.getIdAlerta()) || !atual.getIdCidadao().equals(n.getIdCidadao());
        if (trocouPar && dao.existe(n.getIdAlerta(), n.getIdCidadao())) {
            throw new ConflitoException("O cidadao " + n.getIdCidadao() + " ja foi notificado do alerta " + n.getIdAlerta());
        }
        n.setId(id);
        dao.atualizar(n);
        return n;
    }

    public Notificacao marcarComoLida(Long id) {
        Notificacao n = buscar(id);
        n.setLida(Boolean.TRUE);
        dao.atualizar(n);
        return n;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    private void validar(Notificacao n) {
        new Validador()
                .obrigatorio(n.getIdAlerta(), "idAlerta")
                .obrigatorio(n.getIdCidadao(), "idCidadao")
                .obrigatorio(n.getCanal(), "canal")
                .validar();
        if (alertaDao.buscarPorId(n.getIdAlerta()).isEmpty()) {
            throw new RegraNegocioException("Alerta " + n.getIdAlerta() + " nao existe");
        }
        if (cidadaoDao.buscarPorId(n.getIdCidadao()).isEmpty()) {
            throw new RegraNegocioException("Cidadao " + n.getIdCidadao() + " nao existe");
        }
    }
}
