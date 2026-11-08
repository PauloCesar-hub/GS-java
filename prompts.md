# Registro de uso de IA (IA como copiloto)

Conforme os combinados da GS, registramos aqui como a IA foi usada. **Cada integrante deve saber explicar cada linha do projeto.** Revisem o código e os textos antes da entrega e acrescentem os prompts que vocês mesmos fizerem.

## Ferramenta

Claude (Anthropic), usado como copiloto de desenvolvimento e revisão.

## Prompts

### 1. Pedido inicial
> "Claude, você irá fazer tudo que o PDF pede da melhor forma possível, sem falhas, erros ou bugs, focando em cada matéria de forma individual. Deve ignorar as matérias AR-VR e Network Architect."
> (anexo: GS_CitizenTech_-_2ES_Fev_-_FIAP_2026.pdf)

**O que a IA produziu a partir desse pedido:** proposta da CitizenTech (alagamentos no Parque Novo Mundo), scripts SQL Oracle, API Spring Boot em camadas, notebook de Data Science, algoritmos de Programação Dinâmica, documentos em PDF, diagramas e telas.

**Como a equipe validou:** 
- SQL executado em sequência (DDL → DML → DQL) em banco H2 no modo de compatibilidade Oracle. **Falta rodar no Oracle da FIAP.**
- Regras de negócio da API testadas em 50 cenários (DAOs + BOs); testes de integração com MockMvc em `java-api/src/test`.
- Notebook executado do início ao fim sem erros.
- 14 testes automatizados no módulo de Programação Dinâmica (`python -m unittest discover -s tests -v`).

### 2. Dados da equipe, passo a passo e roteiro
> Pedido: preencher nome e RM de todos os integrantes, criar um passo a passo de tudo o que falta fazer e um roteiro para o vídeo.

**O que a IA produziu:** RMs em todos os arquivos, `PASSO_A_PASSO.md` e `ROTEIRO_VIDEO.md`. A entrevista com o morador e o Planning Poker ficaram para a equipe fazer de verdade.

### 3. (adicionar os próximos prompts da equipe aqui)
> ...

## O que cada integrante deve revisar e saber explicar

- **Regra de alerta** (`AlertaBO.avaliarNivel`): como uma leitura vira alerta e por que não se repetem alertas do mesmo nível.
- **Divisão temporal treino/teste** e por que `lamina_agua_cm` não entra na logística (vazamento de dados).
- **Recorrências** da mochila (memoização x tabulação) e do Floyd-Warshall.
- **Cardinalidades** do modelo relacional e as três consultas DQL.
