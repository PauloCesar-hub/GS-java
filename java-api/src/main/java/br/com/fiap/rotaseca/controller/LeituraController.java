package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.LeituraBO;
import br.com.fiap.rotaseca.dto.LeituraRegistradaResponse;
import br.com.fiap.rotaseca.model.Leitura;
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
@RequestMapping("/api/leituras")
public class LeituraController {

    private final LeituraBO bo;

    public LeituraController(LeituraBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Leitura>> listar(@RequestParam(required = false) Long idSensor) {
        return ResponseEntity.ok(bo.listar(idSensor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leitura> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    /** Recebe a medicao do sensor; se passar das cotas do ponto, emite alerta e notifica moradores. */
    @PostMapping
    public ResponseEntity<LeituraRegistradaResponse> registrar(@RequestBody Leitura corpo) {
        LeituraRegistradaResponse resposta = bo.registrar(corpo);
        return ResponseEntity.created(URI.create("/api/leituras/" + resposta.leitura().getId())).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Leitura> atualizar(@PathVariable Long id, @RequestBody Leitura corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
