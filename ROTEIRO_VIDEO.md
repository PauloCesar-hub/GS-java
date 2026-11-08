# Roteiro do vídeo pitch — RotaSeca

**Duração máxima: 3 minutos** (o roteiro fecha em cerca de 2min50s, com folga) · **Publicar no YouTube como "Não listado"** · Este mesmo vídeo serve para **Agile** (pitch ilustrado com narração do grupo) e para **Testing** (vídeo de até 3 min com a solução e as entregas que mais somaram).

O texto de narração tem cerca de 400 palavras. Leia em ritmo de conversa (~140 palavras por minuto) e cronometre antes de gravar.

---

## Antes de gravar: preparar a demonstração (10 minutos)

1. Suba a API localmente (`java-api` → rodar `RotaSecaApplication`) ou use o link do Render.
2. Abra duas abas no navegador:
   - **Página web:** `http://localhost:8080`
   - **Swagger:** `http://localhost:8080/swagger-ui.html`
3. No Swagger, em **POST /api/leituras**, envie as três leituras abaixo, uma de cada vez (*Try it out → Execute*). Isso cria o cenário de temporal:
   ```json
   { "idSensor": 1, "valor": 24 }
   { "idSensor": 3, "valor": 45 }
   { "idSensor": 5, "valor": 12 }
   ```
   Resultado: Baixada do córrego em **ALERTA**, Passagem sob a rodovia **INTRANSITÁVEL** e Rua do mercado em **ATENÇÃO**. Grave essa parte, porque ela mostra a regra de negócio funcionando de verdade.
4. Deixe abertos: o notebook de Data Science (gráfico de probabilidade por ponto), o terminal com `python main.py --demo` (pasta `dynamic-programming`) e o PDF de Agile (personas e diagrama de casos de uso).
5. Deixe o navegador com zoom de 125% ou use o modo celular do DevTools (F12 → ícone de celular → 390 x 844), para as telas ficarem iguais às do protótipo.

**Ferramentas gratuitas:** OBS Studio ou Clipchamp (já vem no Windows) para gravar a tela e editar. Cada narrador pode gravar o próprio áudio no celular, em lugar silencioso e a uns 15 cm da boca. Junte tudo no Clipchamp e coloque legendas automáticas (ajuda na acessibilidade, que é tema do projeto).

---

## Cena 1 — Abertura (0:00 a 0:20) · Narração: **Paulo**

**Imagem:** Paulo na câmera, em uma rua do Parque Novo Mundo, ou imagens de chuva e rua alagada. Se não chover até a gravação, use vídeos gratuitos do Pexels ou Pixabay (busque "rua alagada chuva") e cite a fonte nos créditos. Na tela: **"Paulo, morador do Parque Novo Mundo"**.

> "Eu moro no Parque Novo Mundo, na Zona Norte de São Paulo. Quando chove forte aqui, a gente só descobre que a rua alagou quando já está com o pé na água. Para quem é idoso, usa cadeira de rodas ou volta do trabalho à noite, isso deixa de ser incômodo e vira perigo."

## Cena 2 — O problema (0:20 a 0:50) · Narração: **Christian**

**Imagem:** manchetes de "estado de atenção para alagamentos" do CGE (print de notícia, com a fonte visível) e depois a página de personas do PDF de Agile, com zoom na Dona Aparecida e na Beatriz.

> "O CGE avisa quando a Zona Norte entra em estado de atenção. Só que esse aviso vale para a região inteira, e ninguém diz qual rua evitar. Pense na Dona Aparecida, sessenta e oito anos, que vai a pé até a UBS. Ou na Beatriz, que usa cadeira de rodas e trava com cinco centímetros de água. Elas precisam saber antes de sair de casa: posso ir? Por onde?"

## Cena 3 — A solução funcionando (0:50 a 1:50) · Narração: **Guilherme**

**Imagem:** gravação de tela da página web, seguindo a ordem da fala:
1. Tela **Agora** (mostrar o resumo "trechos para evitar" e rolar os cartões);
2. Tela **Avisar** (escolher "Entorno da UBS", tocar em "Canela", enviar);
3. Tela **Alertas**;
4. Tela **Apoio**;
5. Imagens das telas de **Rota segura** e **Cadastro** do protótipo (estão em `docs/telas`).

Na tela: **"Protótipo funcionando"**.

> "Por isso criamos a RotaSeca. Na tela inicial, o morador vê cada trecho do bairro com cor e com texto: normal, atenção, alerta ou intransitável, e o que fazer em cada caso.
> Onde ainda não tem sensor, o próprio morador avisa. Ele escolhe o lugar e a altura da água pelo corpo: poça, canela, joelho. São três toques, e um agente confirma o relato para evitar alarme falso.
> Quando a água passa do limite, o alerta sai sozinho e chega pelo canal que a pessoa entende melhor. Para a Dona Aparecida, é WhatsApp com áudio.
> E não é só aviso: o aplicativo mostra a rota segura para cada perfil e os pontos de apoio acessíveis para esperar a chuva passar."

## Cena 4 — Como funciona por dentro (1:50 a 2:25) · Narração: **Gustavo**

**Imagem**, em sequência rápida (5 a 7 segundos cada):
1. Swagger executando **POST /api/leituras** com `{ "idSensor": 3, "valor": 45 }` e a resposta mostrando `"nivelCalculado": "EMERGENCIA"` e `"notificacoesEnviadas": 3`;
2. Diagrama do modelo de dados (PDF de Database);
3. Gráfico de probabilidade por ponto (notebook);
4. Terminal do `python main.py --demo` mostrando a rota do perfil cadeirante.

Na tela: **"Java + Spring Boot · Oracle · Python"**.

> "Por trás, uma API em Java com Spring Boot e banco Oracle recebe a leitura do sensor e decide o nível do alerta pelas cotas de cada ponto. Aqui, quarenta e cinco centímetros na passagem sob a rodovia: emergência, e três moradores avisados na hora.
> Com regressão linear e logística, prevemos a altura da água e a chance de a rua fechar a partir da chuva. E com grafos e programação dinâmica, calculamos a rota mais segura e onde instalar sensores com o orçamento disponível."

## Cena 5 — Impacto e ODS (2:25 a 2:45) · Narração: **Thomas**

**Imagem:** slide simples com os indicadores e os ícones dos ODS 11, 13, 10, 3 e 9 (ícones oficiais da ONU, de uso livre para fins educativos).

> "Medimos o impacto pelo que importa para o morador: quantas ruas que fecharam foram avisadas antes, e com quantos minutos de antecedência. A RotaSeca contribui com os ODS onze e treze, de cidades resilientes e ação climática, e com o ODS dez, de inclusão."

## Cena 6 — Fechamento (2:45 a 2:55) · Narração: **Paulo**

**Imagem:** logo "RotaSeca" e foto ou vídeo curto dos cinco integrantes. Na tela, os nomes e RMs:

- Christian Schunck de Almeida · RM563850
- Guilherme Vilela Perez · RM564422
- Gustavo Panham Dourado · RM563904
- Paulo Cesar de Govea Junior · RM566034
- Thomas Jeferson Santana Wang · RM565104

> "RotaSeca: para ninguém ser pego de surpresa pela água no caminho de casa."

---

## Checklist de publicação

- [ ] Duração final **abaixo de 3:00** (conferir na linha do tempo do editor).
- [ ] Todos os 5 integrantes aparecem nos créditos, e há narração de integrantes do grupo (exigência de Agile).
- [ ] Sem nomes, rostos ou dados pessoais de terceiros sem autorização.
- [ ] Título: **"RotaSeca | Global Solution 2026.2 FIAP | CitizenTech"**.
- [ ] Visibilidade: **Não listado**. Conferir se o vídeo **não** está marcado como "conteúdo para crianças", porque isso desativa alguns recursos.
- [ ] Abrir o link numa **janela anônima** e confirmar que toca. Link inacessível = zero no item.
- [ ] Colar o link em `_docs_src/equipe.json` (`link_pitch`) e rodar `python build_all.py --so-pdfs` para ele entrar no PDF de Agile.
- [ ] Enviar o mesmo link na tarefa de **Testing**.
