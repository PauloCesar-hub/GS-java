package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.AlertaDao;
import br.com.fiap.rotaseca.dao.BairroDao;
import br.com.fiap.rotaseca.dao.LeituraDao;
import br.com.fiap.rotaseca.dao.OcorrenciaDao;
import br.com.fiap.rotaseca.dao.PontoRiscoDao;
import br.com.fiap.rotaseca.dto.SituacaoPontoResponse;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.Leitura;
import br.com.fiap.rotaseca.model.PontoRisco;
import br.com.fiap.rotaseca.model.StatusOcorrencia;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PontoRiscoBO {

    private final PontoRiscoDao dao;
    private final BairroDao bairroDao;
    private final LeituraDao leituraDao;
    private final AlertaDao alertaDao;
    private final OcorrenciaDao ocorrenciaDao;

    public PontoRiscoBO(PontoRiscoDao dao, BairroDao bairroDao, LeituraDao leituraDao, AlertaDao alertaDao,
                        OcorrenciaDao ocorrenciaDao) {
        this.dao = dao;
        this.bairroDao = bairroDao;
        this.leituraDao = leituraDao;
        this.alertaDao = alertaDao;
        this.ocorrenciaDao = ocorrenciaDao;
    }

    public List<PontoRisco> listar(Long idBairro) {
        return idBairro == null ? dao.listar() : dao.listarPorBairro(idBairro);
    }

    public PontoRisco buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de risco", id));
    }

    public PontoRisco criar(PontoRisco ponto) {
        if (ponto.getAtivo() == null) {
            ponto.setAtivo(Boolean.TRUE);
        }
        validar(ponto);
        ponto.setId(null);
        return dao.inserir(ponto);
    }

    public PontoRisco atualizar(Long id, PontoRisco ponto) {
        PontoRisco atual = buscar(id);
        if (ponto.getAtivo() == null) {
            ponto.setAtivo(atual.getAtivo());
        }
        validar(ponto);
        ponto.setId(id);
        dao.atualizar(ponto);
        return ponto;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    /** Situacao atual de todos os pontos ativos (tela inicial do app). */
    public List<SituacaoPontoResponse> situacaoGeral() {
        List<OcorrenciaContagem> abertas = contarOcorrenciasAbertas();
        return dao.listarAtivos().stream().map(p -> montarSituacao(p, abertas)).toList();
    }

    public SituacaoPontoResponse situacao(Long id) {
        return montarSituacao(buscar(id), contarOcorrenciasAbertas());
    }

    private SituacaoPontoResponse montarSituacao(PontoRisco p, List<OcorrenciaContagem> abertas) {
        Optional<Leitura> ultima = leituraDao.ultimaLeituraDeNivel(p.getId());
        Optional<Alerta> alertaMaisGrave = alertaDao.listarAtivosPorPonto(p.getId()).stream()
                .max(Comparator.comparing(Alerta::getNivel));
        String situacao;
        if (alertaMaisGrave.isPresent()) {
            situacao = alertaMaisGrave.get().getNivel().name();
        } else if (ultima.isPresent()) {
            situacao = "NORMAL";
        } else {
            situacao = "SEM_DADOS";
        }
        long qtdAbertas = abertas.stream().filter(c -> c.idPonto().equals(p.getId()))
                .mapToLong(OcorrenciaContagem::quantidade).sum();
        return new SituacaoPontoResponse(p.getId(), p.getNome(), p.getReferencia(), p.getLatitude(), p.getLongitude(),
                p.getCotaAtencaoCm(), p.getCotaCriticaCm(), ultima.map(Leitura::getValor).orElse(null),
                ultima.map(Leitura::getDataLeitura).orElse(null), situacao, alertaMaisGrave.orElse(null), qtdAbertas);
    }

    private record OcorrenciaContagem(Long idPonto, long quantidade) {
    }

    private List<OcorrenciaContagem> contarOcorrenciasAbertas() {
        return ocorrenciaDao.listarPorStatus(StatusOcorrencia.ABERTA).stream()
                .map(o -> new OcorrenciaContagem(o.getIdPonto(), 1)).toList();
    }

    private void validar(PontoRisco p) {
        new Validador()
                .obrigatorio(p.getIdBairro(), "idBairro")
                .obrigatorio(p.getNome(), "nome").tamanhoMaximo(p.getNome(), 100, "nome")
                .tamanhoMaximo(p.getReferencia(), 200, "referencia")
                .obrigatorio(p.getLatitude(), "latitude").intervalo(p.getLatitude(), -90, 90, "latitude")
                .obrigatorio(p.getLongitude(), "longitude").intervalo(p.getLongitude(), -180, 180, "longitude")
                .obrigatorio(p.getCotaAtencaoCm(), "cotaAtencaoCm").intervalo(p.getCotaAtencaoCm(), 1, 99999, "cotaAtencaoCm")
                .obrigatorio(p.getCotaCriticaCm(), "cotaCriticaCm").intervalo(p.getCotaCriticaCm(), 1, 99999, "cotaCriticaCm")
                .regra(p.getCotaAtencaoCm() == null || p.getCotaCriticaCm() == null
                        || p.getCotaCriticaCm() > p.getCotaAtencaoCm(), "cotaCriticaCm deve ser maior que cotaAtencaoCm")
                .validar();
        if (bairroDao.buscarPorId(p.getIdBairro()).isEmpty()) {
            throw new RegraNegocioException("Bairro " + p.getIdBairro() + " nao existe");
        }
    }
}
