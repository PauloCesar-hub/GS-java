package br.com.fiap.rotaseca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Ponto de entrada da API: mostra o que existe e confirma que o deploy esta no ar. */
@RestController
public class InfoController {

    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("aplicacao", "RotaSeca API - alertas de alagamento e rotas seguras");
        corpo.put("status", "UP");
        corpo.put("documentacao", "/swagger-ui.html");
        corpo.put("recursos", List.of("/api/bairros", "/api/pontos", "/api/pontos/situacao", "/api/abrigos",
                "/api/sensores", "/api/leituras", "/api/cidadaos", "/api/ocorrencias", "/api/alertas",
                "/api/notificacoes"));
        return ResponseEntity.ok(corpo);
    }
}
