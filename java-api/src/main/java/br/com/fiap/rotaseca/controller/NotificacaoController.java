package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.NotificacaoBO;
import br.com.fiap.rotaseca.model.Notificacao;
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
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoBO bo;

    public NotificacaoController(NotificacaoBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Notificacao>> listar(@RequestParam(required = false) Long idCidadao,
                                                    @RequestParam(required = false) Long idAlerta) {
        return ResponseEntity.ok(bo.listar(idCidadao, idAlerta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Notificacao> criar(@RequestBody Notificacao corpo) {
        Notificacao criada = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/notificacoes/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacao> atualizar(@PathVariable Long id, @RequestBody Notificacao corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<Notificacao> marcarComoLida(@PathVariable Long id) {
        return ResponseEntity.ok(bo.marcarComoLida(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
