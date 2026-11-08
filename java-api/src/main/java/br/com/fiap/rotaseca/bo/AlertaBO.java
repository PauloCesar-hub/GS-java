package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.AlertaDao;
import br.com.fiap.rotaseca.dao.CidadaoDao;
import br.com.fiap.rotaseca.dao.NotificacaoDao;
import br.com.fiap.rotaseca.dao.PontoRiscoDao;
import br.com.fiap.rotaseca.dto.AvaliacaoNivelResponse;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.CanalNotificacao;
import br.com.fiap.rotaseca.model.Cidadao;
import br.com.fiap.rotaseca.model.NivelAlerta;
import br.com.fiap.rotaseca.model.Notificacao;
import br.com.fiap.rotaseca.model.PerfilCidadao;
import br.com.fiap.rotaseca.model.PontoRisco;
import br.com.fiap.rotaseca.util.Relogio;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Capacidade central da RotaSeca: transformar uma medicao de agua (sensor ou relato
 * confirmado) em alerta e notificar os moradores do bairro que querem ser avisados.
 */
@Service
public class AlertaBO {

    private final AlertaDao dao;
    private final PontoRiscoDao pontoDao;
    private final CidadaoDao cidadaoDao;
    private final NotificacaoDao notificacaoDao;

    public AlertaBO(AlertaDao dao, PontoRiscoDao pontoDao, CidadaoDao cidadaoDao, NotificacaoDao notificacaoDao) {
        this.dao = dao;
        this.pontoDao = pontoDao;
        this.cidadaoDao = cidadaoDao;
        this.notificacaoDao = notificacaoDao;
    }

    public List<Alerta> listar(boolean somenteAtivos) {
        return somenteAtivos ? dao.listarAtivos() : dao.listar();
    }

    public Alerta buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
    }

    /** Alerta manual (ex.: agente da Defesa Civil). Tambem notifica os moradores do bairro. */
    public Alerta criar(Alerta alerta) {
        if (alerta.getDataEmissao() == null) {
            alerta.setDataEmissao(Relogio.agora());
        }
        PontoRisco ponto = validar(alerta);
        alerta.setId(null);
        Alerta salvo = dao.inserir(alerta);
        if (salvo.isAtivo()) {
            notificarMoradores(salvo, ponto);
        }
        return salvo;
    }

    public Alerta atualizar(Long id, Alerta alerta) {
        Alerta atual = buscar(id);
        if (alerta.getDataEmissao() == null) {
            alerta.setDataEmissao(atual.getDataEmissao());
        }
        validar(alerta);
        alerta.setId(id);
        dao.atualizar(alerta);
        return alerta;
    }

    public Alerta encerrar(Long id) {
        Alerta alerta = buscar(id);
        if (!alerta.isAtivo()) {
            throw new RegraNegocioException("Alerta " + id + " ja foi encerrado");
        }
        LocalDateTime agora = Relogio.agora();
        alerta.setDataEncerramento(agora.isBefore(alerta.getDataEmissao()) ? alerta.getDataEmissao() : agora);
        dao.atualizar(alerta);
        return alerta;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    /**
     * Regra de negocio principal. Dada a altura de agua medida em um ponto:
     * <ul>
     *   <li>abaixo da cota de atencao: encerra os alertas ativos do ponto (a agua baixou);</li>
     *   <li>nivel mais grave que o alerta ativo (ou nenhum alerta ativo): encerra o anterior,
     *       emite um novo alerta e notifica os moradores do bairro;</li>
     *   <li>nivel igual ou menos grave que o ativo: mantem o alerta atual (evita enviar
     *       varias mensagens enquanto a agua oscila).</li>
     * </ul>
     */
    public AvaliacaoNivelResponse avaliarNivel(PontoRisco ponto, double alturaCm, LocalDateTime quando) {
        NivelAlerta nivel = ponto.classificar(alturaCm);
        List<Alerta> ativos = dao.listarAtivosPorPonto(ponto.getId());
        Optional<NivelAlerta> nivelAtivo = ativos.stream().map(Alerta::getNivel).max(Comparator.naturalOrder());

        if (nivel == null) {
            int encerrados = ativos.isEmpty() ? 0 : dao.encerrarAtivosDoPonto(ponto.getId(), maisTarde(quando, ativos));
            return new AvaliacaoNivelResponse(null, null, encerrados, 0);
        }
        if (nivelAtivo.isPresent() && !nivel.maisGraveQue(nivelAtivo.get())) {
            return AvaliacaoNivelResponse.semMudanca(nivel);
        }
        int encerrados = ativos.isEmpty() ? 0 : dao.encerrarAtivosDoPonto(ponto.getId(), maisTarde(quando, ativos));
        Alerta novo = dao.inserir(new Alerta(null, ponto.getId(), nivel, mensagemPadrao(ponto, nivel, alturaCm),
                quando, null));
        int enviadas = notificarMoradores(novo, ponto);
        return new AvaliacaoNivelResponse(nivel, novo, encerrados, enviadas);
    }

    static String mensagemPadrao(PontoRisco ponto, NivelAlerta nivel, double alturaCm) {
        long cm = Math.round(alturaCm);
        return switch (nivel) {
            case ATENCAO -> ponto.getNome() + ": agua acumulando (" + cm + " cm). Atencao ao passar pelo trecho.";
            case ALERTA -> ponto.getNome() + ": " + cm + " cm de agua. Pedestres, idosos e cadeirantes devem evitar o trecho.";
            case EMERGENCIA -> ponto.getNome() + ": via intransitavel (" + cm + " cm). Nao atravesse; use a rota alternativa.";
        };
    }

    private int notificarMoradores(Alerta alerta, PontoRisco ponto) {
        int enviadas = 0;
        for (Cidadao c : cidadaoDao.listarQueRecebemAlerta(ponto.getIdBairro())) {
            if (!notificacaoDao.existe(alerta.getId(), c.getId())) {
                notificacaoDao.inserir(new Notificacao(null, alerta.getId(), c.getId(), alerta.getDataEmissao(),
                        canalPreferido(c), Boolean.FALSE));
                enviadas++;
            }
        }
        return enviadas;
    }

    /** Idosos recebem por WhatsApp (canal mais familiar); quem nao tem telefone recebe por e-mail. */
    static CanalNotificacao canalPreferido(Cidadao c) {
        if (c.getTelefone() == null || c.getTelefone().isBlank()) {
            return CanalNotificacao.EMAIL;
        }
        return c.getPerfil() == PerfilCidadao.IDOSO ? CanalNotificacao.WHATSAPP : CanalNotificacao.PUSH;
    }

    private static LocalDateTime maisTarde(LocalDateTime quando, List<Alerta> ativos) {
        LocalDateTime maiorEmissao = ativos.stream().map(Alerta::getDataEmissao).max(Comparator.naturalOrder())
                .orElse(quando);
        return quando.isBefore(maiorEmissao) ? maiorEmissao : quando;
    }

    private PontoRisco validar(Alerta a) {
        new Validador()
                .obrigatorio(a.getIdPonto(), "idPonto")
                .obrigatorio(a.getNivel(), "nivel")
                .obrigatorio(a.getMensagem(), "mensagem").tamanhoMaximo(a.getMensagem(), 300, "mensagem")
                .regra(a.getDataEncerramento() == null || a.getDataEmissao() == null
                        || !a.getDataEncerramento().isBefore(a.getDataEmissao()),
                        "dataEncerramento nao pode ser anterior a dataEmissao")
                .validar();
        return pontoDao.buscarPorId(a.getIdPonto())
                .orElseThrow(() -> new RegraNegocioException("Ponto de risco " + a.getIdPonto() + " nao existe"));
    }
}
