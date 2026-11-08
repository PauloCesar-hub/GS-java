package br.com.fiap.rotaseca;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao da API usando o perfil dev (H2 em memoria, modo Oracle,
 * carregado com os mesmos scripts DDL/DML da disciplina de Database Design).
 * Cada teste usa registros diferentes para nao depender da ordem de execucao.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RotaSecaApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listaOsBairrosCarregadosPeloScriptDml() throws Exception {
        mvc.perform(get("/api/bairros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void pontoInexistenteRetorna404ComCorpoPadrao() throws Exception {
        mvc.perform(get("/api/pontos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.caminho").value("/api/pontos/999"));
    }

    @Test
    void leituraAcimaDaCotaCriticaEmiteAlertaENotificaMoradores() throws Exception {
        // sensor 3 = NIVEL no ponto 2 (cotas 20/40 cm), bairro 1 tem 3 moradores que aceitam alertas
        mvc.perform(post("/api/leituras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idSensor\": 3, \"valor\": 45.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.leitura.id").exists())
                .andExpect(jsonPath("$.avaliacao.nivelCalculado").value("EMERGENCIA"))
                .andExpect(jsonPath("$.avaliacao.alertaEmitido.idPonto").value(2))
                .andExpect(jsonPath("$.avaliacao.notificacoesEnviadas").value(3));

        mvc.perform(get("/api/pontos/2/situacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("EMERGENCIA"));
    }

    @Test
    void sensorEmManutencaoNaoRegistraLeitura() throws Exception {
        mvc.perform(post("/api/leituras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idSensor\": 6, \"valor\": 10}"))
                .andExpect(status().is(422));
    }

    @Test
    void cidadaoComEmailInvalidoRetorna400() throws Exception {
        mvc.perform(post("/api/cidadaos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idBairro\": 3, \"nome\": \"Teste\", \"email\": \"sem-arroba\", \"perfil\": \"MORADOR\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes[0]").value(containsString("email")));
    }

    @Test
    void excluirBairroComDependentesRetorna409() throws Exception {
        mvc.perform(delete("/api/bairros/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void jsonMalFormatadoRetorna400() throws Exception {
        mvc.perform(post("/api/bairros").contentType(MediaType.APPLICATION_JSON).content("{ nome:"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crudCompletoDeAbrigo() throws Exception {
        String criado = mvc.perform(post("/api/abrigos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idBairro\": 3, \"nome\": \"Abrigo de teste\", \"endereco\": \"Rua Teste, 10\", "
                                + "\"capacidade\": 25, \"acessivel\": true}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(criado, "$.id");

        mvc.perform(put("/api/abrigos/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idBairro\": 3, \"nome\": \"Abrigo de teste 2\", \"endereco\": \"Rua Teste, 10\", "
                                + "\"capacidade\": 30}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacidade").value(30))
                .andExpect(jsonPath("$.acessivel").value(true));

        mvc.perform(get("/api/abrigos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Abrigo de teste 2"));

        mvc.perform(delete("/api/abrigos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/abrigos/" + id)).andExpect(status().isNotFound());
    }
}
