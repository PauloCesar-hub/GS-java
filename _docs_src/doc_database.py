"""PDF da disciplina Database Design (itens 1 e 2 da entrega)."""
from docgen import RAIZ, capa, figura, gerar

SECOES = [
    ("obj", "1. Objetivo e escopo do projeto"),
    ("func", "2. Principais funcionalidades da solução"),
    ("modelo", "3. Diagrama do modelo lógico"),
    ("dic", "4. Tabelas, chaves e relacionamentos"),
    ("scripts", "5. Scripts SQL entregues"),
]

TABELAS = [
    ("T_RS_BAIRRO", "Bairros atendidos (nome, subprefeitura, zona).", "PK id_bairro; UK (nm_bairro, nm_subprefeitura); CK zona", "—"),
    ("T_RS_PONTO_RISCO", "Trechos de rua que alagam, com coordenadas e as cotas de atenção e crítica (cm).", "PK id_ponto; CK cotas (crítica > atenção > 0), latitude/longitude, st_ativo", "Bairro 1:N Ponto"),
    ("T_RS_ABRIGO", "Pontos de apoio para esperar a chuva passar, com capacidade e acessibilidade.", "PK id_abrigo; CK capacidade > 0, st_acessivel", "Bairro 1:N Abrigo"),
    ("T_RS_SENSOR", "Sensores de nível d'água e pluviômetros instalados em cada ponto.", "PK id_sensor; UK cd_serie; CK tipo, status", "Ponto 1:N Sensor"),
    ("T_RS_LEITURA", "Medições: cm de lâmina (NIVEL) ou mm de chuva/hora (PLUVIOMETRO).", "PK id_leitura; CK nr_valor >= 0", "Sensor 1:N Leitura"),
    ("T_RS_CIDADAO", "Moradores cadastrados para receber alertas e relatar alagamentos, com perfil (idoso, PcD...).", "PK id_cidadao; UK ds_email; CK perfil, st_recebe_alerta", "Bairro 1:N Cidadão"),
    ("T_RS_OCORRENCIA", "Relatos de alagamento enviados pelos moradores e seu ciclo de vida.", "PK id_ocorrencia; CK altura >= 0, status", "Cidadão 1:N Ocorrência; Ponto 1:N Ocorrência"),
    ("T_RS_ALERTA", "Alertas emitidos por ponto (ATENCAO, ALERTA, EMERGENCIA); ativo enquanto não encerrado.", "PK id_alerta; CK nível, encerramento >= emissão", "Ponto 1:N Alerta"),
    ("T_RS_NOTIFICACAO", "Entrega de cada alerta a cada cidadão, com canal e leitura (resolve o N:N Alerta x Cidadão).", "PK id_notificacao; UK (id_alerta, id_cidadao); CK canal, st_lida", "Alerta 1:N Notificação; Cidadão 1:N Notificação"),
]

linhas = "".join(f"<tr><td><code>{t}</code></td><td>{d}</td><td>{c}</td><td>{r}</td></tr>" for t, d, c, r in TABELAS)

CORPO = f"""
<section class="secao" id="obj">
<h2>1. Objetivo e escopo do projeto</h2>
<div class="box"><strong>Problema, em uma frase:</strong> em dias de chuva forte, os moradores do Parque Novo Mundo (Zona Norte de São Paulo) só descobrem que a rua alagou quando já estão nela, sem saber qual caminho é seguro para voltar para casa, chegar ao ônibus ou a um ponto de apoio.</div>
<p><strong>Solução proposta.</strong> A <strong>RotaSeca</strong> é uma CitizenTech que junta sensores de nível d'água instalados nos pontos que costumam alagar, relatos dos próprios moradores e uma API que transforma essas medições em <strong>alertas por bairro</strong>, enviados pelo canal que cada pessoa prefere (WhatsApp, SMS, push ou e-mail). Uma página web mostra a situação de cada trecho e os pontos de apoio acessíveis.</p>
<p><strong>Objetivo deste banco de dados.</strong> Organizar de forma íntegra e consultável todas as informações da solução: onde ficam os pontos de risco e suas cotas, o que os sensores mediram, o que os moradores relataram, quais alertas foram emitidos e para quem cada alerta foi entregue. Os dados também apoiam decisões da subprefeitura, como quais pontos alagam com mais frequência e onde ainda falta monitoramento.</p>
<p><strong>Escopo.</strong></p>
<ul>
<li><strong>Dentro do escopo:</strong> cadastro de bairros, pontos de risco, pontos de apoio (abrigos), sensores e leituras; cadastro de cidadãos com perfil e preferência de alerta; relatos de alagamento com ciclo de vida (aberta → confirmada → resolvida, ou descartada); alertas por ponto e registro das notificações enviadas.</li>
<li><strong>Fora do escopo:</strong> cálculo de rotas (feito em memória pelo módulo de Programação Dinâmica), autenticação de usuários, mapas e envio real das mensagens (feito por serviços externos de mensageria).</li>
<li><strong>Território piloto:</strong> Parque Novo Mundo e bairros vizinhos da Subprefeitura Vila Maria/Vila Guilherme. Todos os dados de exemplo são simulados, sem dados pessoais reais.</li>
</ul>
<p><strong>SGBD:</strong> Oracle Database (servidor Oracle da FIAP). As chaves primárias usam <code>GENERATED ALWAYS AS IDENTITY</code>, e as regras de domínio ficam em <code>CHECK</code> constraints, para que nenhum sistema consiga gravar dados inválidos (por exemplo, cota crítica menor que a de atenção ou altura de água negativa).</p>
</section>

<section class="secao" id="func">
<h2>2. Principais funcionalidades da solução</h2>
<div class="grid2">
<div class="card"><h3>Monitoramento dos pontos de risco</h3><p>Sensores de nível e pluviômetros enviam leituras periódicas. Cada leitura é comparada às cotas do ponto (<code>nr_cota_atencao_cm</code> e <code>nr_cota_critica_cm</code>).</p></div>
<div class="card"><h3>Alertas automáticos</h3><p>Quando a água passa de uma cota, a API emite um alerta (ATENCAO, ALERTA ou EMERGENCIA). Quando a água baixa, o alerta é encerrado (<code>dt_encerramento</code>).</p></div>
<div class="card"><h3>Notificação dos moradores</h3><p>Cada alerta é entregue aos moradores do bairro que aceitaram recebê-lo, pelo canal mais adequado ao perfil (idosos por WhatsApp, por exemplo). Uma restrição <code>UNIQUE</code> impede que a mesma pessoa receba o mesmo alerta duas vezes.</p></div>
<div class="card"><h3>Relatos da comunidade</h3><p>Moradores informam onde está alagado e a altura aproximada da água. Um agente confirma ou descarta o relato, e o relato confirmado também gera alerta. Isso cobre os pontos que ainda não têm sensor.</p></div>
<div class="card"><h3>Pontos de apoio acessíveis</h3><p>Escolas, salões e centros esportivos com capacidade e indicação de acessibilidade para cadeira de rodas.</p></div>
<div class="card"><h3>Apoio à decisão pública</h3><p>Consultas mostram os pontos com mais alertas e emergências (prioridade de drenagem e zeladoria) e os pontos sem monitoramento ativo (onde instalar ou consertar sensores).</p></div>
</div>
</section>

<section class="secao" id="modelo">
<h2>3. Diagrama do modelo lógico</h2>
<p>O modelo relacional tem 9 tabelas. Na notação abaixo, <strong>P</strong> = chave primária, <strong>F</strong> = chave estrangeira, <strong>U</strong> = chave única e <strong>*</strong> = obrigatório (NOT NULL). As linhas ligam a tabela pai (lado "um", barra dupla) à tabela filha (lado "muitos", pé de galinha com círculo, ou seja, 0..N).</p>
{figura("modelo_logico.png", "Figura 1 - Modelo lógico relacional da RotaSeca (tabelas, atributos, PK, FK, UK e cardinalidades)")}
</section>

<section class="secao" id="dic">
<h2>4. Tabelas, chaves e relacionamentos</h2>
<table class="t"><tr><th style="width:24%">Tabela</th><th style="width:34%">Conteúdo</th><th style="width:24%">Chaves e restrições</th><th>Relacionamento</th></tr>{linhas}</table>
<p><strong>Decisões de modelagem:</strong></p>
<ul>
<li>O relacionamento muitos-para-muitos entre <em>Alerta</em> e <em>Cidadão</em> foi resolvido pela tabela associativa <code>T_RS_NOTIFICACAO</code>, que também guarda atributos do envio (data, canal e se foi lida).</li>
<li>As leituras guardam só o valor numérico. A unidade (cm ou mm/h) vem do tipo do sensor, o que evita redundância (3FN).</li>
<li>Os status ficam como texto com <code>CHECK</code>, porque os domínios são pequenos e estáveis e assim os scripts ficam mais legíveis do que com tabelas de domínio.</li>
<li>Um alerta está ativo enquanto <code>dt_encerramento</code> for nulo. Um <code>CHECK</code> garante que o encerramento nunca é anterior à emissão.</li>
</ul>
</section>

<section class="secao" id="scripts">
<h2>5. Scripts SQL entregues</h2>
<p>Os scripts estão em arquivos texto separados (extensão <code>.sql</code>) e devem ser executados nesta ordem no Oracle SQL Developer:</p>
<table class="t"><tr><th style="width:30%">Arquivo</th><th>Conteúdo</th></tr>
<tr><td><code>01_DDL_rotaseca.sql</code></td><td>Remove as tabelas, se já existirem (bloco PL/SQL), e cria as 9 tabelas com <code>CREATE TABLE</code>. Todas as PK, FK, UK e CHECK são criadas com <code>ALTER TABLE ... ADD CONSTRAINT</code> e nomes padronizados (<code>PK_</code>, <code>FK_</code>, <code>UN_</code>, <code>CK_</code>). Inclui comentários de tabela.</td></tr>
<tr><td><code>02_DML_rotaseca.sql</code></td><td>Carga de dados simulados com <code>INSERT</code>. Todas as tabelas recebem no mínimo 5 linhas (bairros 5, pontos 6, abrigos 5, sensores 6, leituras 12, cidadãos 6, ocorrências 6, alertas 5, notificações 6). Termina com <code>COMMIT</code>.</td></tr>
<tr><td><code>03_DQL_rotaseca.sql</code></td><td>As três consultas pedidas, comentadas.</td></tr></table>
<h3>Consultas (DQL)</h3>
<ol>
<li><strong>INNER JOIN</strong> (<code>T_RS_OCORRENCIA</code> x <code>T_RS_PONTO_RISCO</code>): lista os relatos dos moradores com o nome do ponto e classifica a água relatada como NORMAL, ATENÇÃO ou INTRANSITÁVEL, comparando-a com as cotas do ponto.</li>
<li><strong>INNER JOIN + GROUP BY</strong> (<code>T_RS_PONTO_RISCO</code> x <code>T_RS_ALERTA</code>): por ponto, total de alertas (<code>COUNT</code>), emergências e alertas em aberto (<code>SUM</code> com <code>CASE</code>) e o último alerta (<code>MAX</code>), ordenado pelos pontos mais críticos.</li>
<li><strong>LEFT OUTER JOIN + WHERE + ORDER BY</strong> (<code>T_RS_PONTO_RISCO</code> x <code>T_RS_SENSOR</code>): pontos ativos <em>sem monitoramento ativo</em> (sem sensor ou com sensor fora de operação). O <code>LEFT JOIN</code> mantém os pontos que não têm nenhum sensor, e o resultado sai ordenado pela menor cota crítica.</li>
</ol>
<p>Os scripts foram testados em sequência (DDL → DML → DQL), e as três consultas retornam resultados com os dados de exemplo.</p>
</section>
"""

if __name__ == "__main__":
    gerar("RotaSeca - Database Design", capa("Database Design", "Modelo de dados relacional - alertas de alagamento e rotas seguras"),
          SECOES, CORPO, RAIZ.parent / "database" / "RotaSeca_DatabaseDesign.pdf")
