package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.OcorrenciaBO;
import br.com.fiap.rotaseca.model.Ocorrencia;
import br.com.fiap.rotaseca.model.StatusOcorrencia;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaBO bo;

    public OcorrenciaController(OcorrenciaBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Ocorrencia>> listar(@RequestParam(required = false) StatusOcorrencia status,
                                                   @RequestParam(required = false) Long idPonto) {
        return ResponseEntity.ok(bo.listar(status, idPonto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ocorrencia> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Ocorrencia> criar(@RequestBody Ocorrencia corpo) {
        Ocorrencia criada = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/ocorrencias/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ocorrencia> atualizar(@PathVariable Long id, @RequestBody Ocorrencia corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    /** Atalho para o agente: PUT /api/ocorrencias/5/status?valor=CONFIRMADA */
    @PutMapping("/{id}/status")
    public ResponseEntity<Ocorrencia> alterarStatus(@PathVariable Long id, @RequestParam StatusOcorrencia valor) {
        return ResponseEntity.ok(bo.alterarStatus(id, valor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
