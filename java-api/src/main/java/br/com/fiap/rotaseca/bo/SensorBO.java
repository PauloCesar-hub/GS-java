package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.PontoRiscoDao;
import br.com.fiap.rotaseca.dao.SensorDao;
import br.com.fiap.rotaseca.exception.ConflitoException;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Sensor;
import br.com.fiap.rotaseca.model.StatusSensor;
import br.com.fiap.rotaseca.util.Relogio;
import br.com.fiap.rotaseca.util.Validador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorBO {

    private final SensorDao dao;
    private final PontoRiscoDao pontoDao;

    public SensorBO(SensorDao dao, PontoRiscoDao pontoDao) {
        this.dao = dao;
        this.pontoDao = pontoDao;
    }

    public List<Sensor> listar(Long idPonto) {
        return idPonto == null ? dao.listar() : dao.listarPorPonto(idPonto);
    }

    public Sensor buscar(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Sensor", id));
    }

    public Sensor criar(Sensor sensor) {
        if (sensor.getStatus() == null) {
            sensor.setStatus(StatusSensor.ATIVO);
        }
        if (sensor.getDataInstalacao() == null) {
            sensor.setDataInstalacao(Relogio.hoje());
        }
        validar(sensor, null);
        sensor.setId(null);
        return dao.inserir(sensor);
    }

    public Sensor atualizar(Long id, Sensor sensor) {
        Sensor atual = buscar(id);
        if (sensor.getStatus() == null) {
            sensor.setStatus(atual.getStatus());
        }
        if (sensor.getDataInstalacao() == null) {
            sensor.setDataInstalacao(atual.getDataInstalacao());
        }
        validar(sensor, id);
        sensor.setId(id);
        dao.atualizar(sensor);
        return sensor;
    }

    public void excluir(Long id) {
        buscar(id);
        dao.excluir(id);
    }

    private void validar(Sensor s, Long idAtual) {
        new Validador()
                .obrigatorio(s.getIdPonto(), "idPonto")
                .obrigatorio(s.getCodigoSerie(), "codigoSerie").tamanhoMaximo(s.getCodigoSerie(), 30, "codigoSerie")
                .obrigatorio(s.getTipo(), "tipo")
                .regra(s.getDataInstalacao() == null || !s.getDataInstalacao().isAfter(Relogio.hoje()),
                        "dataInstalacao nao pode estar no futuro")
                .validar();
        if (pontoDao.buscarPorId(s.getIdPonto()).isEmpty()) {
            throw new RegraNegocioException("Ponto de risco " + s.getIdPonto() + " nao existe");
        }
        dao.buscarPorCodigoSerie(s.getCodigoSerie())
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new ConflitoException("Ja existe sensor com o codigo de serie " + s.getCodigoSerie());
                });
    }
}
