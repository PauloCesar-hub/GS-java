package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.AbrigoBO;
import br.com.fiap.rotaseca.model.Abrigo;
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
@RequestMapping("/api/abrigos")
public class AbrigoController {

    private final AbrigoBO bo;

    public AbrigoController(AbrigoBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Abrigo>> listar(@RequestParam(required = false) Long idBairro) {
        return ResponseEntity.ok(bo.listar(idBairro));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Abrigo> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Abrigo> criar(@RequestBody Abrigo corpo) {
        Abrigo criado = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/abrigos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Abrigo> atualizar(@PathVariable Long id, @RequestBody Abrigo corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
