package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.PontoRiscoBO;
import br.com.fiap.rotaseca.dto.SituacaoPontoResponse;
import br.com.fiap.rotaseca.model.PontoRisco;
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
@RequestMapping("/api/pontos")
public class PontoRiscoController {

    private final PontoRiscoBO bo;

    public PontoRiscoController(PontoRiscoBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<PontoRisco>> listar(@RequestParam(required = false) Long idBairro) {
        return ResponseEntity.ok(bo.listar(idBairro));
    }

    /** Situacao de todos os pontos ativos: usada na tela inicial do app. */
    @GetMapping("/situacao")
    public ResponseEntity<List<SituacaoPontoResponse>> situacaoGeral() {
        return ResponseEntity.ok(bo.situacaoGeral());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoRisco> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @GetMapping("/{id}/situacao")
    public ResponseEntity<SituacaoPontoResponse> situacao(@PathVariable Long id) {
        return ResponseEntity.ok(bo.situacao(id));
    }

    @PostMapping
    public ResponseEntity<PontoRisco> criar(@RequestBody PontoRisco corpo) {
        PontoRisco criado = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/pontos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoRisco> atualizar(@PathVariable Long id, @RequestBody PontoRisco corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
