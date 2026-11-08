package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.CidadaoBO;
import br.com.fiap.rotaseca.model.Cidadao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cidadaos")
public class CidadaoController {

    private final CidadaoBO bo;

    public CidadaoController(CidadaoBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Cidadao>> listar() {
        return ResponseEntity.ok(bo.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cidadao> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Cidadao> criar(@RequestBody Cidadao corpo) {
        Cidadao criado = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/cidadaos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cidadao> atualizar(@PathVariable Long id, @RequestBody Cidadao corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
