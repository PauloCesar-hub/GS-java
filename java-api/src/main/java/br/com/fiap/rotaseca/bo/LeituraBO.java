package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.LeituraDao;
import br.com.fiap.rotaseca.dao.PontoRiscoDao;
import br.com.fiap.rotaseca.dao.SensorDao;
import br.com.fiap.rotaseca.dto.AvaliacaoNivelResponse;
import br.com.fiap.rotaseca.dto.LeituraRegistradaResponse;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Leitura;
import br.com.fiap.rotaseca.model.PontoRisco;
import br.com.fiap.rotaseca.model.Sensor;
import br.com.fiap.rotaseca.model.StatusSensor;
import br.com.fiap.rotaseca.model.TipoSensor;
import br.com.fiap.rotaseca.util.Relogio;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeituraBO {

    /** Tolerancia para relogios de sensores levemente adiantados. */
    private static final long TOLERANCIA_FUTURO_MIN = 5;

    private final LeituraDao dao;
    private final SensorDao sensorDao;
    private final PontoRiscoDao pontoDao;
    private final AlertaBO alertaBO;

    public LeituraBO(LeituraDao dao, SensorDao sensorDao, PontoRiscoDao pontoDao, AlertaBO alertaBO) {
        this.dao = dao;
        this.sensorDao = sensorDao;
        this.pontoDao = pontoDao;
        this.alertaBO = alertaBO;
    }

    public List<Leitura> listar(Long idSensor) {
        return idSensor == null ? dao.listar() : dao.listarPorSensor(idSensor);
    }

    public Leitura buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Leitura", id));
    }

    /**
     * Grava a leitura e, se o sensor for de NIVEL, avalia o ponto para emitir ou encerrar alertas.
     * Sensores fora de operacao (MANUTENCAO/INATIVO) nao podem enviar leituras.
     */
    public LeituraRegistradaResponse registrar(Leitura leitura) {
        if (leitura.getDataLeitura() == null) {
            leitura.setDataLeitura(Relogio.agora());
        }
        Sensor sensor = validar(leitura);
        if (sensor.getStatus() != StatusSensor.ATIVO) {
            throw new RegraNegocioException("Sensor " + sensor.getCodigoSerie() + " esta " + sensor.getStatus()
                    + " e nao pode registrar leituras");
        }
        leitura.setId(null);
        Leitura salva = dao.inserir(leitura);
        AvaliacaoNivelResponse avaliacao = null;
        if (sensor.getTipo() == TipoSensor.NIVEL) {
            PontoRisco ponto = pontoDao.buscarPorId(sensor.getIdPonto())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de risco", sensor.getIdPonto()));
            if (Boolean.TRUE.equals(ponto.getAtivo())) {
                avaliacao = alertaBO.avaliarNivel(ponto, salva.getValor(), salva.getDataLeitura());
            }
        }
        return new LeituraRegistradaResponse(salva, avaliacao);
    }

    /** Correcao de uma leitura (nao reavalia alertas, que ja foram emitidos no momento da medicao). */
    public Leitura atualizar(Long id, Leitura leitura) {
        Leitura atual = buscar(id);
        if (leitura.getDataLeitura() == null) {
            leitura.setDataLeitura(atual.getDataLeitura());
        }
        validar(leitura);
        leitura.setId(id);
        dao.atualizar(leitura);
        return leitura;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    private Sensor validar(Leitura l) {
        new Validador()
                .obrigatorio(l.getIdSensor(), "idSensor")
                .obrigatorio(l.getValor(), "valor").intervalo(l.getValor(), 0, 1000, "valor")
                .regra(!l.getDataLeitura().isAfter(Relogio.agora().plusMinutes(TOLERANCIA_FUTURO_MIN)),
                        "dataLeitura nao pode estar no futuro")
                .validar();
        return sensorDao.buscarPorId(l.getIdSensor())
                .orElseThrow(() -> new RegraNegocioException("Sensor " + l.getIdSensor() + " nao existe"));
    }
}
