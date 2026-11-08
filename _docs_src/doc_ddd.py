"""PDF da disciplina Domain Driven Design - Java."""
import re

from docgen import EQUIPE, RAIZ, capa, esc, figura, gerar

CONTROLLERS = RAIZ.parent / "java-api" / "src" / "main" / "java" / "br" / "com" / "fiap" / "rotaseca" / "controller"

DESCRICOES = {
    "listar": "Lista os registros", "buscar": "Busca pelo id", "criar": "Cadastra", "atualizar": "Atualiza pelo id",
    "excluir": "Exclui pelo id", "info": "Confirma que a API esta no ar e lista os recursos",
    "situacaoGeral": "Situacao atual de todos os pontos ativos (tela inicial)",
    "situacao": "Situacao atual de um ponto (ultima leitura, alerta ativo, relatos abertos)",
    "registrar": "Registra a leitura do sensor e, se passar das cotas, emite alerta e notifica",
    "encerrar": "Encerra um alerta ativo", "marcarComoLida": "Marca a notificacao como lida",
    "alterarStatus": "Muda o status do relato (?valor=CONFIRMADA); confirmar gera alerta",
}
FILTROS = {
    ("/api/abrigos", "listar"): "?idBairro=", ("/api/alertas", "listar"): "?ativos=true",
    ("/api/leituras", "listar"): "?idSensor=", ("/api/notificacoes", "listar"): "?idCidadao=&idAlerta=",
    ("/api/ocorrencias", "listar"): "?status=&idPonto=", ("/api/pontos", "listar"): "?idBairro=",
    ("/api/sensores", "listar"): "?idPonto=",
}
STATUS = {"GET": "200", "POST": "201", "PUT": "200", "DELETE": "204"}


def endpoints():
    lista = []
    for f in sorted(CONTROLLERS.glob("*.java")):
        src = f.read_text(encoding="utf-8")
        base = re.search(r'@RequestMapping\("([^"]+)"\)', src)
        base = base.group(1) if base else ""
        for m in re.finditer(r'@(Get|Post|Put|Delete)Mapping(?:\("([^"]*)"\))?\s*public [^\n]*? (\w+)\(', src):
            verbo, sufixo, metodo = m.group(1).upper(), m.group(2) or "", m.group(3)
            rota = base + sufixo
            lista.append((verbo, rota + FILTROS.get((rota, metodo), ""), DESCRICOES.get(metodo, metodo),
                          STATUS[verbo]))
    return lista


EPS = endpoints()
CORES = {"GET": "#1e7b34", "POST": "#2563eb", "PUT": "#8a6d00", "DELETE": "#b3261e"}
LINHAS_EP = "".join(
    f'<tr><td><span class="tag" style="background:{CORES[v]}">{v}</span></td><td><code>{esc(r)}</code></td>'
    f"<td>{esc(d)}</td><td>{s}</td></tr>" for v, r, d, s in EPS)
CONTAGEM = {v: sum(1 for e in EPS if e[0] == v) for v in CORES}

SECOES = [
    ("esp", "1. Especificação do projeto"),
    ("arq", "2. Arquitetura e regras de negócio"),
    ("classes", "3. Diagrama de classes"),
    ("telas", "4. Protótipos de tela (front-end)"),
    ("endpoints", "5. Tabela de endpoints"),
    ("der", "6. Diagrama Entidade-Relacionamento"),
    ("exec", "7. Instruções para executar o projeto"),
]

TELAS = [
    ("tela1_agora.png", "Agora - situação das ruas",
     "Consome <code>GET /api/pontos/situacao</code>. Os trechos aparecem do mais grave para o mais seguro. Cada cartão tem cor e texto (não só cor), a dica de ação (\"Não atravesse. Use outro caminho.\"), a última medição do sensor e quantos vizinhos relataram o problema."),
    ("tela2_avisar.png", "Avisar - relato de alagamento",
     "Envia <code>POST /api/ocorrencias</code>. A altura da água é escolhida por referências do corpo (poça, canela, joelho) em vez de centímetros, o que é mais fácil para quem está na rua. O relato nasce com status ABERTA e é confirmado por um agente."),
    ("tela3_alertas.png", "Alertas ativos",
     "Consome <code>GET /api/alertas?ativos=true</code>. Mostra a mensagem gerada pela regra de negócio, o nível e o horário de emissão."),
    ("tela4_apoio.png", "Pontos de apoio",
     "Consome <code>GET /api/abrigos</code>. Mostra a capacidade e se o local é acessível para cadeira de rodas."),
]
TELAS_HTML = "".join(
    f'<div class="tela"><img src="{(RAIZ.parent / "docs" / "telas" / a).as_uri()}"><div><h3 style="margin-top:0">{t}</h3><p>{d}</p></div></div>'
    for a, t, d in TELAS)

CORPO = f"""
<section class="secao" id="esp">
<h2>1. Especificação do projeto</h2>
<h3>Descritivo da solução</h3>
<p>A <strong>RotaSeca</strong> é uma CitizenTech para quem mora no Parque Novo Mundo e nos bairros vizinhos da Zona Norte de São Paulo, região que com frequência entra em estado de atenção e de alerta para alagamentos nos boletins do CGE. Sensores de nível d'água nos trechos que costumam alagar e relatos dos próprios moradores alimentam esta <strong>API RESTful em Java com Spring Boot</strong>. A API decide, por regras de domínio, quando emitir ou encerrar um alerta e para quem enviá-lo. Uma página web (servida pela própria aplicação) mostra a situação de cada trecho, recebe relatos e lista os pontos de apoio.</p>
<h3>Justificativa</h3>
<p>Hoje o morador só descobre que a rua alagou quando já está nela. Os alertas oficiais são por região inteira da cidade ("Zona Norte em estado de atenção") e não dizem <em>qual rua</em> evitar. Quem mais sofre são idosos, pessoas com deficiência, pais com carrinho de bebê e trabalhadores que voltam a pé ou de ônibus à noite. Um dos integrantes da equipe mora no bairro e convive com o problema.</p>
<h3>Objetivos</h3>
<ul>
<li>Representar o domínio do problema (bairros, pontos de risco, sensores, leituras, cidadãos, relatos, alertas, notificações e pontos de apoio) em uma aplicação Java organizada em camadas e fácil de estender.</li>
<li>Implementar a <strong>capacidade central</strong>: transformar uma medição de água (sensor ou relato confirmado) em alerta no nível certo e notificar os moradores do bairro pelo canal adequado ao perfil.</li>
<li>Oferecer CRUD completo (GET, POST, PUT e DELETE) de todas as entidades, com validação, regras de negócio e respostas de erro padronizadas.</li>
<li>Persistir os dados no Oracle da FIAP e publicar a aplicação no Render.</li>
</ul>
</section>

<section class="secao" id="arq">
<h2>2. Arquitetura e regras de negócio</h2>
<p>O projeto segue as camadas trabalhadas em aula: <strong>model → DAO → BO → controller</strong>, com uma camada transversal de <strong>tratamento de exceções</strong>.</p>
<table class="t"><tr><th style="width:18%">Pacote</th><th>Responsabilidade</th></tr>
<tr><td><code>model</code></td><td>9 classes que espelham as tabelas do Oracle e 7 enums com os domínios (nível de alerta, status, perfil...). <code>PontoRisco.classificar()</code> e as transições de <code>StatusOcorrencia</code> são regras do próprio domínio.</td></tr>
<tr><td><code>dao</code></td><td>Acesso ao banco com JDBC puro (<code>PreparedStatement</code>). <code>BaseDao</code> centraliza conexões, ids gerados por IDENTITY e a tradução de <code>SQLException</code> (violação de integridade vira HTTP 409).</td></tr>
<tr><td><code>bo</code></td><td>Regras de negócio e validações (campos obrigatórios, limites, e-mail único, FK existente, transições de status) antes de chamar o DAO.</td></tr>
<tr><td><code>controller</code></td><td>Endpoints REST. Recebem e devolvem JSON, sem regra de negócio.</td></tr>
<tr><td><code>exception</code></td><td>Exceções de domínio e <code>GlobalExceptionHandler</code> (<code>@RestControllerAdvice</code>), que devolve sempre o mesmo corpo de erro.</td></tr>
<tr><td><code>dto</code> / <code>util</code> / <code>config</code></td><td>Respostas compostas (situação do ponto, resultado da avaliação), validador, fuso de São Paulo e CORS.</td></tr></table>
<h3>Regra central: leitura de água → alerta → notificação (<code>AlertaBO.avaliarNivel</code>)</h3>
<ol>
<li>A altura é classificada pelas cotas do ponto: abaixo da cota de atenção = normal; a partir dela = <strong>ATENCAO</strong>; a partir do ponto médio entre as cotas = <strong>ALERTA</strong>; a partir da cota crítica = <strong>EMERGENCIA</strong>.</li>
<li>Se o nível é <strong>mais grave</strong> que o alerta ativo (ou não há alerta ativo), o alerta anterior é encerrado, um novo é emitido com mensagem padrão e todos os moradores do bairro que aceitam alertas são notificados (idosos por WhatsApp, quem não tem telefone por e-mail, os demais por push).</li>
<li>Se o nível é igual ou menos grave, o alerta atual é mantido. Isso evita mandar várias mensagens enquanto a água oscila.</li>
<li>Se a água volta para baixo da cota de atenção, os alertas ativos do ponto são encerrados.</li>
<li>Sensores em manutenção ou inativos não podem registrar leituras (HTTP 422). Um relato de morador só gera alerta depois de confirmado por um agente (<code>ABERTA → CONFIRMADA</code>). Status finais (RESOLVIDA, DESCARTADA) não mudam mais.</li>
</ol>
<h3>Tratamento de exceções</h3>
<table class="t"><tr><th>Exceção</th><th>HTTP</th><th>Quando</th></tr>
<tr><td><code>DadosInvalidosException</code></td><td>400</td><td>Campos obrigatórios, tamanhos, faixas, e-mail inválido. Devolve todos os erros de uma vez em <code>detalhes</code>.</td></tr>
<tr><td>JSON mal formado / parâmetro inválido</td><td>400</td><td>Corpo ilegível, enum inexistente, id não numérico.</td></tr>
<tr><td><code>RecursoNaoEncontradoException</code></td><td>404</td><td>Id inexistente.</td></tr>
<tr><td><code>ConflitoException</code></td><td>409</td><td>E-mail ou código de série duplicado, notificação repetida, exclusão de registro com dependentes.</td></tr>
<tr><td><code>RegraNegocioException</code></td><td>422</td><td>Sensor fora de operação, transição de status proibida, FK para registro inexistente, alerta já encerrado.</td></tr>
<tr><td><code>PersistenciaException</code> / outras</td><td>500</td><td>Falha inesperada. Fica registrada no log, sem expor detalhes ao cliente.</td></tr></table>
<pre>{{ "timestamp": "2026-02-10T17:40:12", "status": 400, "erro": "Bad Request",
  "mensagem": "Os dados enviados sao invalidos", "caminho": "/api/cidadaos",
  "detalhes": ["email nao e um e-mail valido"] }}</pre>
</section>

<section class="secao" id="classes">
<h2>3. Diagrama de classes</h2>
<p>Os diagramas foram gerados a partir do código-fonte (atributos, métodos públicos e dependências injetadas pelos construtores), então refletem exatamente o código Java entregue.</p>
{figura("classes_model.png", "Figura 1 - Camada model: entidades, enums e relacionamentos (cardinalidades iguais às do banco)")}
{figura("classes_excecoes.png", "Figura 2 - Camada de tratamento de exceções")}
</section>
<section class="secao">
{figura("classes_camadas.png", "Figura 3 - Camadas controller → BO → DAO (setas = dependências injetadas; triângulo = herança de BaseDao)", "86%")}
</section>

<section class="secao" id="telas">
<h2>4. Protótipos de tela (front-end)</h2>
<p>As telas abaixo são da página web incluída no projeto (<code>src/main/resources/static/index.html</code>), publicada na raiz do deploy. Ela foi pensada para celular, com letras grandes, alto contraste, linguagem simples e navegação inferior com 4 botões, porque o cidadão real não é especialista. As capturas usam o cenário simulado de um temporal.</p>
{TELAS_HTML}
</section>

<section class="secao" id="endpoints">
<h2>5. Tabela de endpoints</h2>
<p>Base local: <code>http://localhost:8080</code> &middot; Deploy: <code>{esc(EQUIPE['link_deploy'])}</code> &middot; Documentação interativa: <code>/swagger-ui.html</code>. São {len(EPS)} endpoints: {CONTAGEM['GET']} GET, {CONTAGEM['POST']} POST, {CONTAGEM['PUT']} PUT e {CONTAGEM['DELETE']} DELETE.</p>
<table class="t"><tr><th style="width:9%">Método</th><th style="width:37%">Endpoint</th><th>Descrição</th><th style="width:9%">Sucesso</th></tr>{LINHAS_EP}</table>
<h3>Exemplos de requisição</h3>
<pre>POST /api/leituras            (sensor 3 = nível d'água da Passagem sob a rodovia, cotas 20/40 cm)
{{ "idSensor": 3, "valor": 45.0 }}
-> 201 {{ "leitura": {{ "id": 13, ... }},
         "avaliacao": {{ "nivelCalculado": "EMERGENCIA", "alertaEmitido": {{ "id": 6, "idPonto": 2, ... }},
                        "alertasEncerrados": 0, "notificacoesEnviadas": 3 }} }}

POST /api/ocorrencias
{{ "idCidadao": 5, "idPonto": 6, "alturaAguaCm": 20, "descricao": "Rampa da UBS alagada" }}   -> 201 (status ABERTA)

PUT /api/ocorrencias/7/status?valor=CONFIRMADA      -> 200 (gera alerta se passar das cotas)

PUT /api/cidadaos/2
{{ "idBairro": 1, "nome": "Rafael Lima", "email": "rafael.lima@exemplo.com",
  "telefone": "(11) 90000-0002", "perfil": "TRABALHADOR", "recebeAlerta": true }}   -> 200

DELETE /api/bairros/1     -> 409 (bairro possui pontos, abrigos e cidadãos vinculados)</pre>
</section>

<section class="secao" id="der">
<h2>6. Diagrama Entidade-Relacionamento</h2>
<p>Tabelas do banco Oracle usadas pela API. É o mesmo modelo entregue em Database Design: os scripts <code>01_DDL</code> e <code>02_DML</code> criam e populam o banco, e as classes da camada model espelham essas tabelas.</p>
{figura("modelo_logico.png", "Figura 4 - DER (modelo relacional) com as 9 tabelas, chaves e cardinalidades")}
</section>

<section class="secao" id="exec">
<h2>7. Instruções para executar o projeto</h2>
<h3>Ferramentas e versões</h3>
<table class="t"><tr><th>Item</th><th>Versão / uso</th></tr>
<tr><td>Java JDK</td><td>21</td></tr>
<tr><td>Spring Boot</td><td>3.5.6 (Web, JDBC/HikariCP), springdoc-openapi 2.8.9 (Swagger)</td></tr>
<tr><td>Maven</td><td>3.9+ (ou o Maven embutido do IntelliJ)</td></tr>
<tr><td>Banco de dados</td><td>Oracle Database da FIAP (<code>oracle.fiap.com.br:1521:ORCL</code>), driver ojdbc11. H2 em memória (modo Oracle) só para rodar localmente e nos testes.</td></tr>
<tr><td>IDE / ferramentas</td><td>IntelliJ IDEA, Oracle SQL Developer, Postman ou Swagger UI, Docker (deploy), Render.com</td></tr></table>
<h3>Links</h3>
<ul><li><strong>GitHub:</strong> {esc(EQUIPE['link_github'])}</li>
<li><strong>Deploy (Render):</strong> {esc(EQUIPE['link_deploy'])} &nbsp;(página web na raiz, API em <code>/api</code>, Swagger em <code>/swagger-ui.html</code>)</li></ul>
<h3>Passo a passo: rodar localmente</h3>
<ol>
<li>Clonar o repositório e abrir a pasta <code>java-api</code> no IntelliJ (ele reconhece o <code>pom.xml</code>).</li>
<li>Executar <code>RotaSecaApplication</code> (ou <code>mvn spring-boot:run</code>). Sem nenhuma configuração, sobe o perfil <code>dev</code> com H2 em memória já carregado com os mesmos dados do script DML.</li>
<li>Abrir <code>http://localhost:8080</code> (página web) e <code>http://localhost:8080/swagger-ui.html</code> (testar os endpoints).</li>
<li>Rodar os testes automatizados: <code>mvn test</code>.</li>
</ol>
<h3>Passo a passo: usar o Oracle da FIAP</h3>
<ol>
<li>No SQL Developer, conectar com o seu RM e executar <code>database/01_DDL_rotaseca.sql</code> e depois <code>02_DML_rotaseca.sql</code>.</li>
<li>Definir as variáveis de ambiente <code>SPRING_PROFILES_ACTIVE=prod</code>, <code>DB_USER=rmXXXXX</code> e <code>DB_PASSWORD=******</code> (opcional: <code>DB_URL</code>; o padrão é <code>jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL</code>).</li>
<li>Executar a aplicação. Ela passa a ler e gravar no Oracle.</li>
</ol>
<h3>Passo a passo: deploy no Render</h3>
<ol>
<li>Subir o repositório no GitHub.</li>
<li>No Render: <em>New &gt; Web Service</em>, conectar o repositório, <strong>Root Directory</strong> = <code>java-api</code>, <strong>Runtime</strong> = Docker (o <code>Dockerfile</code> já está na pasta), plano Free. Também é possível usar o <code>render.yaml</code> da raiz como Blueprint.</li>
<li>Em <em>Environment</em>, cadastrar <code>SPRING_PROFILES_ACTIVE=prod</code>, <code>DB_USER</code> e <code>DB_PASSWORD</code>.</li>
<li>Fazer o deploy e testar <code>https://SEU-SERVICO.onrender.com/api</code>. Deve responder <code>"status": "UP"</code>.</li>
</ol>
<div class="alerta">No plano gratuito o Render "dorme" depois de 15 minutos sem acesso, e a primeira requisição pode levar até 1 minuto. Abra o link antes da correção ou da apresentação.</div>
</section>
"""

if __name__ == "__main__":
    gerar("RotaSeca - Domain Driven Design Java", capa("Domain Driven Design - Java", "API RESTful em Spring Boot com Oracle - alertas de alagamento"),
          SECOES, CORPO, RAIZ.parent / "java-api" / "RotaSeca_DDD_Java.pdf")
