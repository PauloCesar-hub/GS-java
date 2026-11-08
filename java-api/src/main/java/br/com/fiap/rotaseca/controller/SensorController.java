package br.com.fiap.rotaseca.controller;

import br.com.fiap.rotaseca.bo.SensorBO;
import br.com.fiap.rotaseca.model.Sensor;
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
@RequestMapping("/api/sensores")
public class SensorController {

    private final SensorBO bo;

    public SensorController(SensorBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public ResponseEntity<List<Sensor>> listar(@RequestParam(required = false) Long idPonto) {
        return ResponseEntity.ok(bo.listar(idPonto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sensor> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Sensor> criar(@RequestBody Sensor corpo) {
        Sensor criado = bo.criar(corpo);
        return ResponseEntity.created(URI.create("/api/sensores/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sensor> atualizar(@PathVariable Long id, @RequestBody Sensor corpo) {
        return ResponseEntity.ok(bo.atualizar(id, corpo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
