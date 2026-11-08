"""PDF unico da disciplina Agile Methodology With Squad Framework."""
from docgen import EQUIPE, RAIZ, capa, esc, figura, gerar

SECOES = [
    ("ideia", "1. Apresentação descritiva da ideia"),
    ("personas", "2. Personas"),
    ("uc", "3. Diagrama de Casos de Uso"),
    ("backlog", "4. Backlog do Produto"),
    ("prot", "5. Protótipo das telas e intenção de UX"),
    ("pitch", "6. Vídeo Pitch"),
]

# (id, UC, historia, criterio de aceite, MoSCoW, SP, MVP)
BACKLOG = [
    ("US01", "UC01", "Como <b>morador</b>, quero me cadastrar informando bairro, perfil e canal preferido, para receber alertas do jeito que eu entendo.", "E-mail único; perfil obrigatório; aceite de alerta ligado por padrão; posso sair a qualquer momento.", "M", 5, True),
    ("US02", "UC02", "Como <b>morador</b>, quero ver a situação de cada trecho com cor <i>e</i> texto, para decidir se saio de casa agora.", "Trechos ordenados do mais grave ao mais seguro; cada cartão traz uma dica de ação; funciona em tela de 360 px.", "M", 5, True),
    ("US03", "UC15", "Como <b>agente público</b>, quero que os sensores enviem leituras de nível à plataforma, para acompanhar a água sem ir ao local.", "Leitura de sensor inativo é recusada; valor entre 0 e 1000; data no futuro é recusada.", "M", 3, True),
    ("US04", "UC16", "Como <b>morador</b>, quero que o alerta seja emitido automaticamente quando a água passar da cota do ponto, para ser avisado antes de chegar ao trecho.", "ATENÇÃO / ALERTA / EMERGÊNCIA conforme as cotas; não repete alerta do mesmo nível; encerra quando a água baixa.", "M", 8, True),
    ("US05", "UC17, UC03", "Como <b>moradora idosa</b>, quero receber o alerta pelo WhatsApp, para entender o aviso sem precisar abrir um aplicativo.", "Idosos recebem por WhatsApp, quem não tem telefone por e-mail; ninguém recebe o mesmo alerta duas vezes.", "M", 8, True),
    ("US06", "UC05", "Como <b>morador</b>, quero relatar um alagamento escolhendo a altura da água por referências do corpo (poça, canela, joelho), para avisar meus vizinhos em segundos.", "Relato nasce ABERTO; envio em até 3 toques; confirmação na tela.", "M", 5, True),
    ("US07", "UC09", "Como <b>agente público</b>, quero confirmar ou descartar relatos, para que só informação verificada vire alerta.", "ABERTA → CONFIRMADA/DESCARTADA; CONFIRMADA → RESOLVIDA; confirmar gera alerta se passar da cota.", "M", 3, True),
    ("US08", "UC11", "Como <b>agente público</b>, quero encerrar alertas, para que os moradores saibam que a rua liberou.", "Alerta encerrado some da lista de ativos; não é possível encerrar duas vezes.", "M", 3, True),
    ("US09", "UC08", "Como <b>pessoa com deficiência</b>, quero ver os pontos de apoio e se são acessíveis, para saber onde esperar a chuva passar.", "Lista com endereço, capacidade e acessibilidade escrita por extenso.", "M", 3, True),
    ("US10", "UC14", "Como <b>administrador</b>, quero cadastrar bairros, pontos de risco (com cotas), sensores e abrigos, para colocar a solução em funcionamento em um território.", "CRUD completo; cota crítica maior que a de atenção; código de série do sensor único.", "M", 8, True),
    ("US11", "UC10", "Como <b>agente público</b>, quero emitir um alerta manual para um ponto, para avisar sobre algo que o sensor não mede (queda de árvore, bueiro estourado).", "Alerta manual também notifica os moradores do bairro.", "S", 3, False),
    ("US12", "UC07", "Como <b>cadeirante</b>, quero uma rota que evite trechos em atenção ou piores, para chegar ao meu destino sem ficar presa na água.", "Rota diferente por perfil (a pé, mobilidade reduzida, carro); avisa quando não há rota segura.", "S", 13, False),
    ("US13", "UC12", "Como <b>agente da subprefeitura</b>, quero o ranking de pontos com mais alertas e emergências, para priorizar obras de drenagem e limpeza.", "Ranking por total de alertas e emergências; filtro por período.", "S", 5, False),
    ("US14", "UC04", "Como <b>idoso</b>, quero ouvir o alerta em áudio e ler em letra ampliada, para entender mesmo com dificuldade de visão.", "Áudio de até 20 s; fonte mínima de 18 px.", "S", 5, False),
    ("US15", "UC01", "Como <b>morador</b>, quero apagar meu cadastro e meus dados, para ter controle sobre minha privacidade (LGPD).", "Exclusão remove os dados pessoais; relatos ficam anônimos.", "S", 3, False),
    ("US16", "UC13", "Como <b>agente público</b>, quero simular onde instalar sensores com um orçamento, para proteger o maior número de pessoas.", "Mostra o conjunto ótimo de pontos e as pessoas protegidas por dia.", "C", 8, False),
    ("US17", "UC18", "Como <b>morador</b>, quero receber um pré-alerta pela previsão de chuva, para me preparar antes de a água subir.", "Probabilidade por ponto; pré-alerta quando passar do limiar definido.", "C", 13, False),
    ("US18", "UC06", "Como <b>morador</b>, quero anexar uma foto ao relato, para ajudar o agente a confirmar mais rápido.", "Foto de até 5 MB, sem rostos (aviso ao usuário).", "C", 5, False),
    ("US19", "—", "Como <b>morador</b>, quero ver os alertas em um mapa 3D do bairro, para visualizar a água.", "Fora deste ciclo: custo alto e pouco ganho para o público-alvo.", "W", 21, False),
    ("US20", "—", "Como <b>agente</b>, quero que a plataforma bloqueie vias automaticamente pelos semáforos, para impedir a passagem de carros.", "Fora deste ciclo: depende de integração com a CET.", "W", 21, False),
]

ESC = EQUIPE.get("escuta", {})
_v = lambda k: esc(ESC.get(k) or "____________")
ESCUTA_HTML = (
    f'<div class="{"box" if all(ESC.get(k) for k in ("pessoa", "faz_hoje", "atrapalha")) else "alerta"}">'
    f'<b>Escuta de quem convive com o problema.</b> Pessoa ouvida: {_v("pessoa")} &middot; quando e onde: {_v("data_local")}<br>'
    f'<b>O que faz hoje quando chove:</b> {_v("faz_hoje")}<br><b>O que mais atrapalha:</b> {_v("atrapalha")}<br>'
    f'<b>Frase marcante:</b> &ldquo;{_v("frase")}&rdquo;</div>')

MOSCOW = {"M": "Must", "S": "Should", "C": "Could", "W": "Won't"}
LINHAS_BL = "".join(
    f'<tr class="{"mvp" if mvp else ""}"><td><b>{i}</b></td><td>{h}<br><span style="color:#5b6670;font-size:8pt">Aceite: {a}</span></td>'
    f'<td>{uc}</td><td><span class="tag {m}">{MOSCOW[m]}</span></td><td style="text-align:center"><b>{sp}</b></td>'
    f'<td style="text-align:center">{"MVP" if mvp else ""}</td></tr>'
    for i, uc, h, a, m, sp, mvp in BACKLOG)
SP_MVP = sum(sp for *_, m, sp, mvp in BACKLOG if mvp)
SP_TOTAL = sum(b[5] for b in BACKLOG if b[4] != "W")
POR_M = {m: (sum(1 for b in BACKLOG if b[4] == m), sum(b[5] for b in BACKLOG if b[4] == m)) for m in MOSCOW}

MEMBROS = sorted(EQUIPE["integrantes"], key=lambda m: m["nome"])
PRIMEIROS = [m["nome"].split()[0] for m in MEMBROS]
# votos de planning poker (rodada 1 -> rodada 2)
POKER = [
    ("US04 Alerta automático", [5, 8, 8, 13, 8], [8, 8, 8, 8, 8], "Quem votou 13 contava com o envio real de mensagens; a equipe separou o envio (US05) e manteve 8."),
    ("US05 Alerta por WhatsApp", [3, 8, 5, 8, 8], [8, 8, 8, 8, 8], "Quem votou 3 não tinha considerado a regra de canal por perfil nem a duplicidade."),
    ("US12 Rota segura por perfil", [8, 13, 21, 13, 13], [13, 13, 13, 13, 13], "O 21 considerava mapa real; o MVP da rota usa grafo do bairro já mapeado."),
    ("US06 Relatar alagamento", [5, 3, 5, 5, 5], [5, 5, 5, 5, 5], "Consenso rápido; o 3 esqueceu a validação de cidadão e ponto."),
]
LINHAS_POKER = "".join(
    f"<tr><td>{h}</td>" + "".join(f"<td style='text-align:center'>{a}&rarr;<b>{b}</b></td>" for a, b in zip(r1, r2))
    + f"<td style='text-align:center'><b>{r2[0]}</b></td><td style='font-size:8pt'>{obs}</td></tr>"
    for h, r1, r2, obs in POKER)
CAB_POKER = "".join(f"<th style='text-align:center'>{n}</th>" for n in PRIMEIROS)

TELAS = [
    ("tela6_cadastro.png", "Tela 1 - Receber alertas (cadastro)", "US01, US05",
     "<b>Intenção:</b> cadastrar em menos de um minuto, sem senha. Só pedimos o essencial (nome, celular, bairro).",
     "<b>Adequação:</b> as perguntas de perfil (\"Sou idosa(o)\", \"Uso cadeira de rodas\") são botões grandes em linguagem comum. Elas definem o canal (WhatsApp com áudio para idosos) e a rota. O aviso de LGPD fica no rodapé, em uma frase simples."),
    ("tela1_agora.png", "Tela 2 - Agora: situação das ruas", "US02",
     "<b>Intenção:</b> responder em um olhar a pergunta \"posso sair agora?\". O resumo no topo diz quantos trechos evitar.",
     "<b>Adequação:</b> cada trecho tem cor <i>e</i> rótulo escrito (daltônicos), a dica de ação em negrito (\"Não atravesse. Use outro caminho.\") e a hora da última medição, para o morador confiar na informação. Os trechos mais graves vêm primeiro. Letra de 18 px e alto contraste."),
    ("tela2_avisar.png", "Tela 3 - Avisar sobre um alagamento", "US06",
     "<b>Intenção:</b> transformar cada morador em um \"sensor\" para os pontos que ainda não têm equipamento.",
     "<b>Adequação:</b> ninguém sabe medir água em centímetros na rua, então a altura é escolhida por referências do corpo (poça, canela, joelho). São 3 toques até enviar, com retorno claro de que um agente vai confirmar, o que evita pânico com relatos falsos."),
    ("tela3_alertas.png", "Tela 4 - Alertas ativos", "US04, US08",
     "<b>Intenção:</b> concentrar as mensagens oficiais que também chegam por WhatsApp/SMS.",
     "<b>Adequação:</b> a mensagem diz o que fazer, não só o que acontece, e mostra a hora de emissão. Alertas encerrados saem da lista, então o que está na tela é sempre atual."),
    ("tela5_rota.png", "Tela 5 - Rota segura", "US12",
     "<b>Intenção:</b> dar uma alternativa, não só um aviso. A rota muda conforme o perfil escolhido.",
     "<b>Adequação:</b> o quadro vermelho explica quais trechos foram evitados e por quê. Os passos usam pontos de referência do bairro em vez de nomes de rua pouco conhecidos, e o botão \"Ouvir a rota\" atende quem tem dificuldade de leitura."),
    ("tela4_apoio.png", "Tela 6 - Pontos de apoio", "US09",
     "<b>Intenção:</b> saber onde esperar com segurança quando não há rota.",
     "<b>Adequação:</b> acessibilidade escrita por extenso (\"Acessível para cadeira de rodas\"), capacidade e endereço. Pontos sem acessibilidade aparecem por último."),
]
TELAS_HTML = "".join(
    f'<div class="tela"><img src="{(RAIZ.parent / "docs" / "telas" / a).as_uri()}"><div><h3 style="margin-top:0">{t}</h3>'
    f'<p style="font-size:8.5pt;color:#5b6670;margin-bottom:2mm">Histórias: {u}</p><p>{i}</p><p>{ad}</p></div></div>'
    for a, t, u, i, ad in TELAS)

N = PRIMEIROS
ROTEIRO = [
    ("0:00 - 0:20", N[3], "Gancho", "Imagens de chuva forte e rua alagada (gravadas pela equipe no bairro ou ilustrações). Narração: \"Toda vez que chove forte no Parque Novo Mundo, a Dona Aparecida só descobre que a rua alagou quando a água já está no tornozelo.\""),
    ("0:20 - 0:50", N[0], "Problema", "Mapa da Zona Norte e manchetes de estado de alerta do CGE. Os alertas oficiais valem para a região inteira e não dizem qual rua evitar. Quem mais sofre: idosos, pessoas com deficiência e quem volta a pé à noite."),
    ("0:50 - 1:50", N[1], "Solução (demo)", "Gravação de tela do protótipo: situação das ruas → relato de alagamento → alerta chegando no WhatsApp → rota segura para cadeirante → pontos de apoio acessíveis."),
    ("1:50 - 2:20", N[2], "Como funciona", "Animação do fluxo: sensor de nível + relato do morador → API Java com Oracle → alerta automático por nível → notificação por perfil. Previsão por regressão logística e rotas por grafo e programação dinâmica."),
    ("2:20 - 2:45", N[4], "Impacto e ODS", "Indicadores: % de ruas intransitáveis avisadas antes, antecedência média do aviso e moradores cadastrados. ODS 11, 13, 10, 3 e 9."),
    ("2:45 - 3:00", N[3], "Fechamento", "Equipe em cena (ou fotos), nome da solução e frase final: \"RotaSeca: ninguém pego de surpresa pela água no caminho de casa.\""),
]
LINHAS_ROT = "".join(f"<tr><td><b>{t}</b></td><td>{q}</td><td>{s}</td><td>{d}</td></tr>" for t, q, s, d in ROTEIRO)

CORPO = f"""
<section class="secao" id="ideia">
<h2>1. Apresentação descritiva da ideia</h2>
<div class="box"><strong>Problema, em uma frase:</strong> em dias de chuva forte, os moradores do Parque Novo Mundo (Zona Norte de São Paulo) só descobrem que a rua alagou quando já estão nela, sem saber qual caminho é seguro para casa, para o ônibus ou para um ponto de apoio.</div>
<h3>Por que este problema (território e escuta)</h3>
<p>O Parque Novo Mundo fica na Subprefeitura Vila Maria/Vila Guilherme, entre a Marginal Tietê e a Rodovia Fernão Dias, e tem trechos baixos junto a córregos e passagens sob a rodovia. Um dos integrantes da equipe mora no bairro e convive com o problema. Nos temporais, o CGE (Centro de Gerenciamento de Emergências Climáticas) costuma colocar a Zona Norte e as marginais em estado de atenção, e em 2025 a região de Vila Maria e Vila Guilherme chegou a ficar em estado de alerta. Esses avisos valem para regiões inteiras e <strong>não dizem qual rua evitar</strong>.</p>
{ESCUTA_HTML}
<h3>Foco e público-alvo</h3>
<p>Escolhemos o <strong>deslocamento a pé no momento da chuva</strong>, e não a obra de drenagem (que depende do poder público), porque é onde uma informação no tempo certo muda a decisão da pessoa. O público-alvo são os moradores do bairro, com prioridade para quem tem mais dificuldade de desviar da água: <strong>idosos, pessoas com deficiência, pais com carrinho de bebê e trabalhadores</strong> que voltam a pé ou de ônibus à noite. Os <strong>agentes públicos</strong> (Defesa Civil e subprefeitura) são usuários secundários, que confirmam relatos e usam os dados para priorizar a zeladoria.</p>
<h3>A solução</h3>
<p>A <strong>RotaSeca</strong> combina três fontes: <strong>sensores de nível d'água</strong> de baixo custo nos trechos que mais alagam, <strong>relatos dos próprios moradores</strong> (que cobrem os pontos sem sensor) e <strong>previsão por dados de chuva</strong>. Com isso ela:</p>
<ul>
<li>mostra em uma página web simples a situação de cada trecho (normal, atenção, alerta ou intransitável);</li>
<li>emite <strong>alertas automáticos por ponto</strong> e avisa os moradores do bairro pelo canal que cada um entende melhor (WhatsApp com áudio para idosos);</li>
<li>indica a <strong>rota segura por perfil</strong> (a pé, cadeira de rodas ou carrinho, carro) e os <strong>pontos de apoio acessíveis</strong>.</li>
</ul>
<h3>Justificativa das escolhas</h3>
<table class="t"><tr><th style="width:25%">Escolha</th><th>Por quê</th></tr>
<tr><td>Página web leve + WhatsApp/SMS, sem app obrigatório</td><td>O idoso não precisa instalar nada. O alerta chega onde ele já está, e a página abre em celulares simples e com internet fraca.</td></tr>
<tr><td>Relato do morador + confirmação do agente</td><td>Cobre os pontos sem sensor a custo zero, e a confirmação evita pânico com informação falsa.</td></tr>
<tr><td>Alerta por ponto, não por região</td><td>É a informação que falta hoje: qual rua evitar.</td></tr>
<tr><td>Rota e canal por perfil</td><td>A mesma lâmina d'água que um adulto atravessa prende uma cadeira de rodas. A solução trata essas diferenças.</td></tr>
<tr><td>Começar por um bairro</td><td>"Comece pequeno": um território piloto com 6 pontos, que depois pode ser levado para outros bairros com o mesmo cadastro.</td></tr></table>
<h3>ODS conectados</h3>
<p><b>ODS 11</b> (meta 11.5: reduzir pessoas afetadas por desastres, inclusive os relacionados à água) &middot; <b>ODS 13</b> (13.1: resiliência a riscos climáticos) &middot; <b>ODS 10</b> (10.2: inclusão de idosos e pessoas com deficiência) &middot; <b>ODS 3</b> (3.d: alerta precoce e redução de riscos à saúde) &middot; <b>ODS 9</b> (9.1: infraestrutura resiliente e acessível).</p>
<h3>Indicadores de impacto</h3>
<p>Percentual de ruas que ficaram intransitáveis e foram avisadas <i>antes</i> &middot; antecedência média do aviso (minutos) &middot; moradores cadastrados por perfil &middot; tempo entre o relato e a confirmação &middot; pontos de risco com monitoramento ativo.</p>
</section>

<section class="secao" id="personas">
<h2>2. Personas</h2>
<div class="grid2">
<div class="card"><h3 style="margin-top:0">Dona Aparecida, 68 anos - aposentada</h3>
<p><b>Contexto:</b> mora há 30 anos numa rua perto da baixada do córrego, sozinha, com um neto que aparece no fim de semana. Usa o celular para WhatsApp e áudios. Lê com dificuldade letras pequenas.</p>
<p><b>Rotina e dor:</b> vai à UBS e ao mercado a pé. Em fevereiro saiu para buscar remédio, a chuva apertou e ela ficou meia hora parada numa marquise, com água até o tornozelo e medo de cair.</p>
<p><b>Objetivo:</b> saber <i>antes de sair</i> se dá para ir e voltar, e para onde ir se a chuva pegar no caminho.</p>
<p><b>Como a RotaSeca ajuda:</b> alerta por WhatsApp com áudio e o ponto de apoio mais próximo e acessível.</p></div>
<div class="card"><h3 style="margin-top:0">Rafael, 34 anos - auxiliar de logística</h3>
<p><b>Contexto:</b> trabalha em um centro de distribuição perto da Marginal e volta à noite de ônibus. Do ponto até em casa, passa pela passagem sob a rodovia.</p>
<p><b>Rotina e dor:</b> já perdeu um celular e um par de tênis atravessando a passagem alagada, e uma vez esperou duas horas o ônibus desviado. Não tem tempo de ficar olhando notícia.</p>
<p><b>Objetivo:</b> ser avisado no celular se precisa mudar o caminho e por onde ir.</p>
<p><b>Como a RotaSeca ajuda:</b> notificação push quando a passagem passa da cota e rota alternativa a pé.</p></div>
<div class="card"><h3 style="margin-top:0">Beatriz, 27 anos - atendente, usuária de cadeira de rodas</h3>
<p><b>Contexto:</b> mora no Jardim Japão, trabalha em uma loja do bairro e faz fisioterapia na UBS. É conectada e usa bem o celular.</p>
<p><b>Rotina e dor:</b> 5 cm de água já fazem a cadeira travar ou molhar o motor. A rampa da UBS alaga antes de todo o resto, e os aplicativos de mapa não sabem disso.</p>
<p><b>Objetivo:</b> uma rota que evite <i>qualquer</i> trecho com água e saber quais abrigos são acessíveis.</p>
<p><b>Como a RotaSeca ajuda:</b> perfil "mobilidade reduzida", que bloqueia trechos em atenção, e lista de pontos de apoio acessíveis.</p></div>
<div class="card"><h3 style="margin-top:0">Marcos, 45 anos - agente da Defesa Civil / subprefeitura</h3>
<p><b>Contexto:</b> cobre vários bairros da subprefeitura com uma equipe pequena. Recebe ligações e mensagens soltas em grupos.</p>
<p><b>Rotina e dor:</b> não sabe o que é verdade nas mensagens nem consegue estar em todos os pontos. Depois da chuva, não tem dados para pedir limpeza de bueiros onde é mais necessário.</p>
<p><b>Objetivo:</b> confirmar rápido o que está acontecendo, avisar a população certa e priorizar a zeladoria com dados.</p>
<p><b>Como a RotaSeca ajuda:</b> fila de relatos para confirmar, alerta manual, ranking de pontos críticos e plano de sensores com orçamento.</p></div>
</div>
</section>

<section class="secao" id="uc">
<h2>3. Diagrama de Casos de Uso</h2>
{figura("casos_de_uso.png", "Figura 1 - Casos de uso de todo o escopo da solução, para todos os atores")}
<div style="break-before:page">
<h3 style="margin-top:0">Atores</h3>
<ul>
<li><b>Morador</b>: qualquer cidadão cadastrado.</li>
<li><b>Idoso / Pessoa com deficiência</b>: especialização do morador, com casos próprios de acessibilidade.</li>
<li><b>Agente público</b>: Defesa Civil e subprefeitura.</li>
<li><b>Administrador</b>: equipe RotaSeca que implanta a solução em um território.</li>
<li><b>Sensor IoT</b>, <b>Serviço de mensagens</b> e <b>Serviço de previsão</b>: sistemas externos.</li>
</ul>
<h3>Relações</h3>
<ul>
<li><b>&lt;&lt;include&gt;&gt;</b>: enviar leitura e confirmar relato sempre passam por <i>Avaliar nível</i>, que sempre <i>Notifica moradores</i>. A rota segura sempre consulta a situação das ruas.</li>
<li><b>&lt;&lt;extend&gt;&gt;</b>: anexar foto estende o relato, e ouvir em áudio estende o recebimento do alerta (opcionais).</li>
</ul>
<h3>Rastreabilidade</h3>
<p>Cada história do backlog (seção 4) aponta o caso de uso que implementa. O MVP cobre UC01, UC02, UC03, UC05, UC08, UC09, UC11, UC14, UC15, UC16 e UC17.</p>
</div>
</section>

<section class="secao" id="backlog">
<h2>4. Backlog do Produto</h2>
<p>Histórias no formato <i>Como [persona], quero [ação], para [benefício]</i>, com critério de aceite, caso de uso relacionado, prioridade <b>MoSCoW</b> e estimativa em <b>Story Points</b> (Fibonacci, por Planning Poker). As linhas em verde formam o <b>MVP</b>, que soma <b>{SP_MVP} pontos</b> do total de {SP_TOTAL} pontos planejados (sem contar os <i>Won't</i>).</p>
<table class="t"><tr><th style="width:6%">ID</th><th>História e critério de aceite</th><th style="width:9%">UC</th><th style="width:9%">MoSCoW</th><th style="width:5%">SP</th><th style="width:6%">MVP</th></tr>{LINHAS_BL}</table>
<h3>Resumo da priorização (MoSCoW)</h3>
<table class="t"><tr><th>Categoria</th><th>Histórias</th><th>Story Points</th><th>Critério usado pela equipe</th></tr>
<tr><td><span class="tag M">Must</span></td><td>{POR_M['M'][0]}</td><td>{POR_M['M'][1]}</td><td>Sem isso não existe alerta por rua: cadastro, situação, sensores, alerta automático, notificação, relato e confirmação, pontos de apoio e cadastros básicos. <b>= MVP</b></td></tr>
<tr><td><span class="tag S">Should</span></td><td>{POR_M['S'][0]}</td><td>{POR_M['S'][1]}</td><td>Grande valor (rota por perfil, áudio, ranking, LGPD), mas a solução já funciona sem eles no piloto.</td></tr>
<tr><td><span class="tag C">Could</span></td><td>{POR_M['C'][0]}</td><td>{POR_M['C'][1]}</td><td>Diferenciais quando houver dados reais (pré-alerta por previsão, plano de sensores, foto).</td></tr>
<tr><td><span class="tag W">Won't</span></td><td>{POR_M['W'][0]}</td><td>{POR_M['W'][1]}</td><td>Fora deste ciclo: custo alto ou dependência de terceiros.</td></tr></table>
<h3>Planning Poker</h3>
<p>Cada integrante votou com cartas Fibonacci (1, 2, 3, 5, 8, 13, 21). Quando houve divergência, quem deu o menor e o maior voto explicou o raciocínio e a equipe votou de novo. A referência (história de 3 pontos) foi a US07, confirmar relato. Exemplos das histórias com mais discussão (rodada 1 &rarr; rodada 2):</p>
<table class="t"><tr><th>História</th>{CAB_POKER}<th style='text-align:center'>Final</th><th>O que mudou a estimativa</th></tr>{LINHAS_POKER}</table>
<h3>Plano de entrega do MVP</h3>
<p><b>Sprint 1</b> (US10, US03, US04, US02 - {8 + 3 + 8 + 5} pts): cadastros, leituras, regra de alerta e tela de situação. <b>Sprint 2</b> (US01, US05, US06, US07, US08, US09 - {5 + 8 + 5 + 3 + 3 + 3} pts): cadastro do morador, notificação por canal, relatos com confirmação, encerramento e pontos de apoio. Velocidade esperada da equipe: cerca de 25 pontos por sprint de 2 semanas.</p>
</section>

<section class="secao" id="prot">
<h2>5. Protótipo das telas e intenção de UX</h2>
<p>Protótipo navegável de 6 telas (as telas 2, 3, 4 e 6 já funcionam na página web ligada à API Java). Princípios de UX aplicados em todas: <b>letra de 18 px</b>, <b>alto contraste</b>, <b>cor sempre acompanhada de texto</b>, <b>linguagem do dia a dia</b>, <b>botões grandes</b> (área de toque de 44 px ou mais), menu fixo embaixo para uso com uma mão e funcionamento em telas pequenas e conexão fraca.</p>
{TELAS_HTML}
</section>

<section class="secao" id="pitch">
<h2>6. Vídeo Pitch</h2>
<div class="box"><b>Link do vídeo (YouTube, não listado):</b> {esc(EQUIPE['link_pitch'])}<br>Duração máxima: 3 minutos &middot; narração feita pelos integrantes do grupo &middot; conferir se o link abre em uma janela anônima antes de entregar.</div>
<h3>Roteiro e storyboard</h3>
<table class="t"><tr><th style="width:12%">Tempo</th><th style="width:11%">Narração</th><th style="width:14%">Bloco</th><th>Imagem e fala</th></tr>{LINHAS_ROT}</table>
<h3>Mensagem central</h3>
<p>Problema real (a rua alaga e ninguém avisa qual) → solução simples (alerta por rua, no WhatsApp, com rota por perfil) → funcionando (protótipo e API no ar) → para quem (Dona Aparecida, Rafael, Beatriz e Marcos) → impacto medido (ruas avisadas antes e minutos de antecedência).</p>
</section>
"""

if __name__ == "__main__":
    gerar("RotaSeca - Agile Methodology", capa("Agile Methodology With Squad Framework", "Design da solução CitizenTech - alertas de alagamento e rotas seguras"),
          SECOES, CORPO, RAIZ.parent / "agile" / "RotaSeca_Agile.pdf")
