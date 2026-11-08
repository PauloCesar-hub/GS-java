package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.BairroBO;
import br.com.fiap.rotaseca.model.Bairro;
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
@RequestMapping("/api/bairros")
public class BairroController {

    private final BairroBO bo;

    public BairroController(BairroBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Bairro>> listar() {
        return ResponseEntity.ok(bo.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bairro> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Bairro> criar(@RequestBody Bairro corpo) {
        Bairro criado = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/bairros/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bairro> atualizar(@PathVariable Long id, @RequestBody Bairro corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
