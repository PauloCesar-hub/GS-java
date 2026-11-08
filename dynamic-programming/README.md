# RotaSeca — Dynamic Programming

**Global Solution 2026.2 · FIAP · Engenharia de Software (2º ano, Fev) · CitizenTech**

| Integrante | RM |
|---|---|
| Christian Schunck de Almeida | RM563850 |
| Guilherme Vilela Perez | RM564422 |
| Gustavo Panham Dourado | RM563904 |
| Paulo Cesar de Govea Junior | RM566034 |
| Thomas Jeferson Santana Wang | RM565104 |

## A necessidade da CitizenTech

A RotaSeca avisa os moradores do Parque Novo Mundo (Zona Norte de São Paulo) quando uma rua alaga. Esta parte do projeto responde a três perguntas que aparecem **no momento da chuva** e **no planejamento**:

1. *"Como eu chego ao meu destino sem passar por rua alagada?"* — rota mais segura para cada perfil de pessoa.
2. *"Quem ficou ilhado e qual o ponto de apoio mais perto de cada esquina?"* — para orientar todo o bairro de uma vez.
3. *"Com o dinheiro que temos, onde instalar sensores para proteger mais gente?"* — plano de investimento.

A situação de cada ponto de risco (`NORMAL`, `ATENCAO`, `ALERTA`, `EMERGENCIA`) usa os mesmos ids de ponto (1 a 6) do banco Oracle e da API Java. Com `--api`, o programa lê a situação real do endpoint `GET /api/pontos/situacao`.

## Como executar

Requisitos: **Python 3.10 ou superior**. Não há dependências externas (só biblioteca padrão).

```bash
python main.py              # menu interativo
python main.py --demo       # roda todas as funcionalidades com o cenário de temporal
python main.py --bench      # compara força bruta x memoização x tabulação
python main.py --demo --api https://SEU-DEPLOY.onrender.com   # usa a situação real da API Java
python -m unittest discover -s tests -v                      # 14 testes automatizados
```

## Estrutura

```
dynamic-programming/
├── main.py                 # menu interativo, modo demo e benchmark
├── rotaseca_dp/
│   ├── dados.py            # grafo do bairro, cenários de chuva e candidatos a sensor (simulados)
│   ├── grafo.py            # GRAFOS: Dijkstra (rota mais segura) e BFS (quem fica ilhado)
│   ├── floyd.py            # TABULAÇÃO em grafo: Floyd-Warshall (todos para todos)
│   └── sensores.py         # MEMOIZAÇÃO e TABULAÇÃO: mochila 0/1 (onde instalar sensores)
└── tests/test_algoritmos.py
```

## Algoritmos abordados

### 1. Grafos — Dijkstra para a rota mais segura (`grafo.py`)

O bairro é um grafo não direcionado: vértices são esquinas e locais (casa, UBS, escola, terminal, pontos de apoio) e arestas são ruas com distância em metros. Algumas ruas passam por um **ponto de risco**. O custo de atravessar uma rua depende do nível de água e do **perfil** da pessoa:

| Perfil | NORMAL | ATENCAO | ALERTA | EMERGENCIA |
|---|---|---|---|---|
| PEDESTRE | 1,0 × metros | 1,6 × metros | bloqueado | bloqueado |
| MOBILIDADE_REDUZIDA (cadeira de rodas, idoso, carrinho de bebê) | 1,0 × | bloqueado | bloqueado | bloqueado |
| VEICULO | 1,0 × | 1,2 × | 2,0 × | bloqueado |

Dijkstra com fila de prioridade (`heapq`) encontra o caminho de menor custo. **Complexidade:** O((V + E) log V). Se o destino ficar inalcançável, o programa avisa que a pessoa está ilhada.

**Por que Dijkstra:** os pesos são positivos e a consulta é de uma origem para um destino (o caso de uso do app no celular).

### 2. Grafos — BFS para quem fica ilhado (`grafo.py`)

Busca em largura que ignora trechos bloqueados para o perfil e devolve todos os locais alcançáveis. **Complexidade:** O(V + E). Mostra, por exemplo, que no temporal a *Esquina do córrego* fica sem acesso para quem anda a pé.

### 3. Programação Dinâmica por tabulação em grafo — Floyd-Warshall (`floyd.py`)

Quando um alerta dispara, a Defesa Civil precisa dizer **para cada esquina do bairro** qual o ponto de apoio mais perto. Em vez de rodar um Dijkstra por pessoa, calculamos uma vez a tabela de menores custos entre todos os pares.

- **Subproblema:** `D[k][i][j]` = menor custo de `i` até `j` usando só os `k` primeiros vértices como intermediários.
- **Recorrência:** `D[k][i][j] = min(D[k-1][i][j], D[k-1][i][k] + D[k-1][k][j])`.
- **Tabulação:** a tabela é preenchida de baixo para cima, reaproveitando a mesma matriz, e uma matriz `proximo` permite reconstruir o caminho.
- **Complexidade:** tempo O(V³), memória O(V²). Depois disso, cada consulta "abrigo mais próximo" custa O(número de abrigos).
- Para o perfil `MOBILIDADE_REDUZIDA`, só entram pontos de apoio acessíveis (coluna `st_acessivel` do banco).
- O teste `test_floyd_igual_a_dijkstra_para_todos_os_pares` confere que Floyd-Warshall e Dijkstra dão exatamente o mesmo custo para todos os 169 pares, nos 3 perfis e nos 2 cenários.

### 4. Memoização e tabulação — mochila 0/1 para o plano de sensores (`sensores.py`)

Os pontos sem monitoramento vêm da consulta 3 do banco (pontos sem sensor ou com sensor em manutenção) e de outros candidatos. Cada um tem um **custo** (R$) e um **benefício** (pessoas que passam por ali por dia). Queremos proteger o máximo de pessoas sem passar do orçamento: é o problema da **mochila 0/1**. Os custos são contados em unidades de R$ 100 para a tabela ficar pequena (orçamento de R$ 10.000 → W = 100).

- **Força bruta:** testa os 2ⁿ subconjuntos. Serve como referência para conferir as versões de PD.
- **Memoização (top-down):** `melhor(i, w) = max(melhor(i+1, w), valor[i] + melhor(i+1, w − peso[i]))`, com `functools.lru_cache`. Cada par (i, w) é resolvido uma única vez. Tempo e memória O(n·W). O programa mostra quantos subproblemas ficaram no cache.
- **Tabulação (bottom-up):** `T[i][w] = max(T[i-1][w], valor[i-1] + T[i-1][w − peso[i-1]])`, preenchida linha a linha. Tempo e memória O(n·W), sem recursão.
- Nas duas versões o conjunto escolhido é reconstruído seguindo as decisões da tabela.

Com R$ 10.000, o ótimo protege **4.700 pessoas por dia**. A memoização e a tabulação podem escolher conjuntos diferentes quando há empate (por exemplo, `P4+P5+P9` ou `P4+P5+P7+P8`, ambos com 4.700 pessoas). Os dois são ótimos.

#### Eficiência medida (`python main.py --bench`, orçamento de R$ 30.000)

| n candidatos | Força bruta | Memoização | Tabulação |
|---|---|---|---|
| 10 | ~2 ms | ~0,6 ms | ~0,5 ms |
| 20 | ~720 ms | ~1,5 ms | ~0,5 ms |
| 60 | inviável (2⁶⁰ subconjuntos) | ~7 ms | ~1,5 ms |
| 200 | inviável | ~26 ms | ~4,5 ms |

*Os tempos variam conforme a máquina.* A força bruta dobra a cada candidato novo. As versões de PD crescem de forma linear em n·W. A tabulação é mais rápida que a memoização porque não paga o custo das chamadas recursivas e do cache.

## Funcionalidades implementadas

- Rota mais segura entre quaisquer dois locais, para 3 perfis, em dia seco ou com temporal.
- Lista de locais alcançáveis e de locais isolados (quem ficou ilhado).
- Ponto de apoio mais próximo de cada local, respeitando a acessibilidade.
- Plano de sensores com orçamento informado pelo usuário (memoização, tabulação e conferência por força bruta).
- Benchmark comparando as três abordagens.
- Integração opcional com a API Java da RotaSeca (`--api`).
- Validação de entradas (local, perfil, orçamento e custos inválidos) e 14 testes automatizados.

## Dados

Todos os dados são **simulados**: nomes de locais, distâncias, custos e fluxo de pessoas são ilustrativos. Nenhum dado pessoal real é usado.
