# RotaSeca

**Global Solution 2026.2 · FIAP · Engenharia de Software (2º ano, Fev) · Desafio CitizenTech**

## Ficha da CitizenTech

| | |
|---|---|
| **Nome da CitizenTech** | RotaSeca: alertas de alagamento e rotas seguras |
| **Cidade e bairro** | São Paulo (SP), Parque Novo Mundo e vizinhos (Subprefeitura Vila Maria/Vila Guilherme, Zona Norte) |
| **Cidadão beneficiado** | Moradores que se deslocam a pé ou de ônibus em dias de chuva, com prioridade para idosos, pessoas com deficiência, pais com carrinho de bebê e trabalhadores que voltam à noite |
| **Problema, em uma frase** | Em dias de chuva forte, os moradores só descobrem que a rua alagou quando já estão nela, sem saber qual caminho é seguro para casa, para o ônibus ou para um ponto de apoio. |
| **Como funciona** | Sensores de nível d'água nos trechos que alagam e relatos dos moradores chegam a uma API. A API classifica cada ponto (atenção, alerta, emergência), emite alertas por rua e avisa os moradores do bairro pelo canal certo para cada perfil (WhatsApp para idosos). A página web mostra a situação das ruas e os pontos de apoio. Os modelos de dados preveem a altura da água e a chance de a rua fechar, e os algoritmos de grafo indicam a rota segura por perfil. |
| **O que cada parte faz** | **Agile:** ideia, personas, casos de uso, backlog e protótipo · **Database Design:** modelo relacional Oracle e scripts DDL/DML/DQL · **DDD Java:** API REST Spring Boot + Oracle, regra de alerta e página web · **Data Science:** regressão linear (altura da água) e logística (rua intransitável) · **Dynamic Programming:** rota segura (Dijkstra/BFS), abrigo mais próximo (Floyd-Warshall) e plano de sensores (mochila com memoização e tabulação) |
| **Link do repositório** | https://github.com/PauloCesar-hub/GS-java |
| **Link do site no render** | https://gs-java-j78f.onrender.com |
| **Indicador de impacto** | % de ruas intransitáveis avisadas **antes** · antecedência média do aviso (min) · moradores cadastrados por perfil · tempo entre relato e confirmação · pontos com monitoramento ativo |
| **ODS conectados** | 11 (11.5), 13 (13.1), 10 (10.2), 3 (3.d), 9 (9.1) |

## Equipe

| Integrante | RM |
|---|---|
| Christian Schunck de Almeida | RM563850 |
| Guilherme Vilela Perez | RM564422 |
| Gustavo Panham Dourado | RM563904 |
| Paulo Cesar de Govea Junior | RM566034 |
| Thomas Jeferson Santana Wang | RM565104 |

## Estrutura do repositório

```
rotaseca/
├── README.md                 # esta ficha
├── PASSO_A_PASSO.md          # tudo o que falta fazer até a entrega, em ordem
├── ROTEIRO_VIDEO.md          # roteiro do pitch (falas, cenas e checklist)
├── prompts.md                # registro do uso de IA como copiloto
├── render.yaml               # blueprint de deploy no Render (API Java)
├── agile/                    # RotaSeca_Agile.pdf (documento único)
├── database/                 # 01_DDL, 02_DML, 03_DQL (.sql), diagrama e PDF
├── java-api/                 # API Spring Boot + Oracle, Dockerfile, testes e PDF
├── data-science/             # notebook executado, CSV, gerador da base e integrantes
├── dynamic-programming/      # algoritmos em Python, testes e README
├── docs/telas/               # capturas das telas do protótipo
└── _docs_src/                # scripts que geram os PDFs e diagramas (equipe.json = nomes/RMs/links)
```

As partes conversam entre si: os mesmos 6 pontos de risco (ids 1 a 6) e as mesmas cotas aparecem no banco Oracle, na API Java, na base de Data Science e no grafo de Dynamic Programming. O módulo Python pode ler a situação real dos pontos direto da API (`python main.py --api URL`).

## Dados

Todos os dados são **simulados ou ilustrativos**: pessoas, e-mails, telefones, endereços e medições. Nenhum dado pessoal real é usado. A origem da base de Data Science está em `data-science/FONTE_DOS_DADOS.txt`.
