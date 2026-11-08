package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.CidadaoDao;
import br.com.fiap.rotaseca.dao.OcorrenciaDao;
import br.com.fiap.rotaseca.dao.PontoRiscoDao;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Ocorrencia;
import br.com.fiap.rotaseca.model.PontoRisco;
import br.com.fiap.rotaseca.model.StatusOcorrencia;
import br.com.fiap.rotaseca.util.Relogio;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OcorrenciaBO {

    private final OcorrenciaDao dao;
    private final CidadaoDao cidadaoDao;
    private final PontoRiscoDao pontoDao;
    private final AlertaBO alertaBO;

    public OcorrenciaBO(OcorrenciaDao dao, CidadaoDao cidadaoDao, PontoRiscoDao pontoDao, AlertaBO alertaBO) {
        this.dao = dao;
        this.cidadaoDao = cidadaoDao;
        this.pontoDao = pontoDao;
        this.alertaBO = alertaBO;
    }

    public List<Ocorrencia> listar(StatusOcorrencia status, Long idPonto) {
        if (idPonto != null) {
            return dao.listarPorPonto(idPonto).stream()
                    .filter(o -> status == null || o.getStatus() == status).toList();
        }
        return status == null ? dao.listar() : dao.listarPorStatus(status);
    }

    public Ocorrencia buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrencia", id));
    }

    /** Todo relato novo nasce ABERTO; so um agente pode confirma-lo depois. */
    public Ocorrencia criar(Ocorrencia ocorrencia) {
        if (ocorrencia.getDataOcorrencia() == null) {
            ocorrencia.setDataOcorrencia(Relogio.agora());
        }
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        PontoRisco ponto = validar(ocorrencia);
        if (!Boolean.TRUE.equals(ponto.getAtivo())) {
            throw new RegraNegocioException("Ponto de risco " + ponto.getId() + " esta desativado");
        }
        ocorrencia.setId(null);
        return dao.inserir(ocorrencia);
    }

    public Ocorrencia atualizar(Long id, Ocorrencia ocorrencia) {
        Ocorrencia atual = buscar(id);
        if (ocorrencia.getStatus() == null) {
            ocorrencia.setStatus(atual.getStatus());
        }
        if (ocorrencia.getDataOcorrencia() == null) {
            ocorrencia.setDataOcorrencia(atual.getDataOcorrencia());
        }
        if (!atual.getStatus().podeMudarPara(ocorrencia.getStatus())) {
            throw new RegraNegocioException("Nao e permitido mudar a ocorrencia de " + atual.getStatus() + " para "
                    + ocorrencia.getStatus() + ". Permitido: " + (atual.getStatus().proximosPermitidos().isEmpty()
                    ? "nenhum (status final)" : atual.getStatus().proximosPermitidos()));
        }
        PontoRisco ponto = validar(ocorrencia);
        ocorrencia.setId(id);
        dao.atualizar(ocorrencia);
        if (atual.getStatus() != StatusOcorrencia.CONFIRMADA && ocorrencia.getStatus() == StatusOcorrencia.CONFIRMADA
                && Boolean.TRUE.equals(ponto.getAtivo())) {
            // relato confirmado por agente vale como medicao: pode gerar alerta
            alertaBO.avaliarNivel(ponto, ocorrencia.getAlturaAguaCm(), Relogio.agora());
        }
        return ocorrencia;
    }

    public Ocorrencia alterarStatus(Long id, StatusOcorrencia novoStatus) {
        if (novoStatus == null) {
            throw new RegraNegocioException("Informe o novo status");
        }
        Ocorrencia atual = buscar(id);
        Ocorrencia alterada = new Ocorrencia(null, atual.getIdCidadao(), atual.getIdPonto(), atual.getDataOcorrencia(),
                atual.getAlturaAguaCm(), atual.getDescricao(), novoStatus);
        return atualizar(id, alterada);
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    private PontoRisco validar(Ocorrencia o) {
        new Validador()
                .obrigatorio(o.getIdCidadao(), "idCidadao")
                .obrigatorio(o.getIdPonto(), "idPonto")
                .obrigatorio(o.getAlturaAguaCm(), "alturaAguaCm").intervalo(o.getAlturaAguaCm(), 0, 500, "alturaAguaCm")
                .tamanhoMaximo(o.getDescricao(), 300, "descricao")
                .regra(!o.getDataOcorrencia().isAfter(Relogio.agora().plusMinutes(5)),
                        "dataOcorrencia nao pode estar no futuro")
                .validar();
        if (cidadaoDao.buscarPorId(o.getIdCidadao()).isEmpty()) {
            throw new RegraNegocioException("Cidadao " + o.getIdCidadao() + " nao existe");
        }
        return pontoDao.buscarPorId(o.getIdPonto())
                .orElseThrow(() -> new RegraNegocioException("Ponto de risco " + o.getIdPonto() + " nao existe"));
    }
}
