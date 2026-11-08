-- =====================================================================
-- RotaSeca - CitizenTech | Global Solution 2026.2 - FIAP
-- Disciplina: Database Design
-- Script 03 - DQL (Data Query Language) - consultas solicitadas
-- SGBD alvo: Oracle Database 19c (servidor Oracle FIAP)
-- Executar apos 01_DDL e 02_DML.
-- =====================================================================

-- ---------------------------------------------------------------------
-- CONSULTA 1 (10 pts) - INNER JOIN entre duas tabelas
-- Pergunta: quais relatos de alagamento os moradores enviaram, em qual
-- ponto, e a agua relatada ja passou da cota critica do ponto?
-- Tabelas: T_RS_OCORRENCIA x T_RS_PONTO_RISCO
-- ---------------------------------------------------------------------
SELECT o.id_ocorrencia                          AS "Ocorrencia",
       p.nm_ponto                               AS "Ponto de risco",
       TO_CHAR(o.dt_ocorrencia, 'DD/MM/YYYY HH24:MI') AS "Data/hora",
       o.nr_altura_agua_cm                      AS "Agua (cm)",
       p.nr_cota_critica_cm                     AS "Cota critica (cm)",
       CASE
           WHEN o.nr_altura_agua_cm >= p.nr_cota_critica_cm THEN 'INTRANSITAVEL'
           WHEN o.nr_altura_agua_cm >= p.nr_cota_atencao_cm THEN 'ATENCAO'
           ELSE 'NORMAL'
       END                                      AS "Situacao relatada",
       o.st_ocorrencia                          AS "Status"
  FROM T_RS_OCORRENCIA o
 INNER JOIN T_RS_PONTO_RISCO p
    ON p.id_ponto = o.id_ponto
 ORDER BY o.dt_ocorrencia;

-- ---------------------------------------------------------------------
-- CONSULTA 2 (10 pts) - INNER JOIN com funcoes de grupo e GROUP BY
-- Pergunta: quantos alertas cada ponto de risco recebeu, quantos foram
-- de EMERGENCIA e quando foi o ultimo alerta? (ranking dos pontos mais
-- criticos, para priorizar obras de drenagem e zeladoria)
-- Tabelas: T_RS_PONTO_RISCO x T_RS_ALERTA
-- ---------------------------------------------------------------------
SELECT p.id_ponto                                              AS "Id",
       p.nm_ponto                                              AS "Ponto de risco",
       COUNT(a.id_alerta)                                      AS "Total de alertas",
       SUM(CASE WHEN a.tp_nivel = 'EMERGENCIA' THEN 1 ELSE 0 END) AS "Emergencias",
       SUM(CASE WHEN a.dt_encerramento IS NULL THEN 1 ELSE 0 END) AS "Alertas em aberto",
       TO_CHAR(MAX(a.dt_emissao), 'DD/MM/YYYY HH24:MI')        AS "Ultimo alerta"
  FROM T_RS_PONTO_RISCO p
 INNER JOIN T_RS_ALERTA a
    ON a.id_ponto = p.id_ponto
 GROUP BY p.id_ponto, p.nm_ponto
 ORDER BY COUNT(a.id_alerta) DESC, p.nm_ponto;

-- ---------------------------------------------------------------------
-- CONSULTA 3 (10 pts) - LEFT OUTER JOIN + WHERE + ORDER BY
-- Pergunta: quais pontos de risco ativos estao SEM monitoramento ativo
-- (nenhum sensor instalado ou sensor fora de operacao)? Lista usada para
-- decidir onde instalar/consertar sensores primeiro (os pontos com a
-- menor cota critica alagam mais rapido, por isso vem primeiro).
-- O LEFT OUTER JOIN preserva os pontos que nao tem nenhum sensor.
-- Tabelas: T_RS_PONTO_RISCO x T_RS_SENSOR
-- ---------------------------------------------------------------------
SELECT p.id_ponto                          AS "Id",
       p.nm_ponto                          AS "Ponto de risco",
       p.nr_cota_critica_cm                AS "Cota critica (cm)",
       NVL(s.cd_serie, '-')                AS "Sensor",
       NVL(s.st_sensor, 'SEM SENSOR')      AS "Situacao do monitoramento"
  FROM T_RS_PONTO_RISCO p
  LEFT OUTER JOIN T_RS_SENSOR s
    ON s.id_ponto = p.id_ponto
 WHERE p.st_ativo = 'S'
   AND (s.id_sensor IS NULL OR s.st_sensor <> 'ATIVO')
 ORDER BY p.nr_cota_critica_cm ASC, p.nm_ponto ASC;
