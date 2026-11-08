package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.AlertaBO;
import br.com.fiap.rotaseca.model.Alerta;
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
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaBO bo;

    public AlertaController(AlertaBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Alerta>> listar(@RequestParam(defaultValue = "false") boolean ativos) {
        return ResponseEntity.ok(bo.listar(ativos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerta> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Alerta> criar(@RequestBody Alerta corpo) {
        Alerta criado = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/alertas/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alerta> atualizar(@PathVariable Long id, @RequestBody Alerta corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Alerta> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.encerrar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
