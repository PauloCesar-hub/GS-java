-- Copia do database/02_DML_rotaseca.sql (dados simulados)
-- =====================================================================
-- RotaSeca - CitizenTech | Global Solution 2026.2 - FIAP
-- Disciplina: Database Design
-- Script 02 - DML (Data Manipulation Language) - carga de dados de exemplo
-- SGBD alvo: Oracle Database 19c (servidor Oracle FIAP)
--
-- IMPORTANTE
--  * Executar logo apos o 01_DDL (tabelas recem-criadas). As chaves sao
--    geradas por IDENTITY e comecam em 1, por isso as FKs abaixo usam
--    os numeros 1, 2, 3... na mesma ordem dos INSERTs.
--  * Todos os dados sao SIMULADOS. Nomes de pessoas, e-mails, telefones,
--    enderecos e nomes de pontos sao ficticios/ilustrativos (nenhum dado
--    pessoal real), conforme os combinados da GS.
--  * Todas as tabelas recebem no minimo 5 linhas.
-- =====================================================================

-- ---------------------------------------------------------------------
-- T_RS_BAIRRO (5 linhas) -> id_bairro 1..5
-- ---------------------------------------------------------------------
INSERT INTO T_RS_BAIRRO (nm_bairro, nm_subprefeitura, nm_zona) VALUES ('Parque Novo Mundo', 'Vila Maria/Vila Guilherme', 'NORTE');
INSERT INTO T_RS_BAIRRO (nm_bairro, nm_subprefeitura, nm_zona) VALUES ('Vila Maria',        'Vila Maria/Vila Guilherme', 'NORTE');
INSERT INTO T_RS_BAIRRO (nm_bairro, nm_subprefeitura, nm_zona) VALUES ('Vila Guilherme',    'Vila Maria/Vila Guilherme', 'NORTE');
INSERT INTO T_RS_BAIRRO (nm_bairro, nm_subprefeitura, nm_zona) VALUES ('Jardim Japao',      'Vila Maria/Vila Guilherme', 'NORTE');
INSERT INTO T_RS_BAIRRO (nm_bairro, nm_subprefeitura, nm_zona) VALUES ('Vila Medeiros',     'Vila Maria/Vila Guilherme', 'NORTE');

-- ---------------------------------------------------------------------
-- T_RS_PONTO_RISCO (6 linhas) -> id_ponto 1..6
-- Os pontos 5 e 6 ainda NAO possuem sensor (aparecem na consulta 3 do DQL).
-- ---------------------------------------------------------------------
INSERT INTO T_RS_PONTO_RISCO (id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES (1, 'Baixada do corrego', 'Trecho mais baixo da rua, junto a galeria do corrego', -23.511800, -46.574200, 15, 30, 'S');
INSERT INTO T_RS_PONTO_RISCO (id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES (1, 'Passagem sob a rodovia', 'Passagem inferior usada por pedestres e onibus', -23.508900, -46.577600, 20, 40, 'S');
INSERT INTO T_RS_PONTO_RISCO (id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES (1, 'Rua do mercado', 'Em frente ao mercado do bairro', -23.510300, -46.572900, 10, 25, 'S');
INSERT INTO T_RS_PONTO_RISCO (id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES (1, 'Acesso a escola', 'Portao principal da escola estadual', -23.513100, -46.571500, 10, 25, 'S');
INSERT INTO T_RS_PONTO_RISCO (id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES (2, 'Ponto de onibus da avenida', 'Ponto de onibus no sentido centro', -23.515600, -46.579800, 15, 30, 'S');
INSERT INTO T_RS_PONTO_RISCO (id_bairro, nm_ponto, ds_referencia, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES (4, 'Entorno da UBS', 'Calcada da UBS, rampa de acesso', -23.517400, -46.567300, 10, 20, 'S');

-- ---------------------------------------------------------------------
-- T_RS_ABRIGO (5 linhas) -> id_abrigo 1..5
-- ---------------------------------------------------------------------
INSERT INTO T_RS_ABRIGO (id_bairro, nm_abrigo, ds_endereco, nr_capacidade, st_acessivel) VALUES (1, 'Escola municipal - ponto de apoio', 'Rua das Palmeiras, 120 (ilustrativo)', 150, 'S');
INSERT INTO T_RS_ABRIGO (id_bairro, nm_abrigo, ds_endereco, nr_capacidade, st_acessivel) VALUES (1, 'Salao paroquial do bairro',         'Rua dos Ipes, 45 (ilustrativo)',      60, 'N');
INSERT INTO T_RS_ABRIGO (id_bairro, nm_abrigo, ds_endereco, nr_capacidade, st_acessivel) VALUES (2, 'Centro esportivo',                  'Av. das Acacias, 900 (ilustrativo)', 200, 'S');
INSERT INTO T_RS_ABRIGO (id_bairro, nm_abrigo, ds_endereco, nr_capacidade, st_acessivel) VALUES (4, 'Associacao de moradores',           'Rua dos Cedros, 310 (ilustrativo)',   40, 'S');
INSERT INTO T_RS_ABRIGO (id_bairro, nm_abrigo, ds_endereco, nr_capacidade, st_acessivel) VALUES (5, 'Clube da comunidade',               'Rua das Hortensias, 77 (ilustrativo)', 80, 'N');

-- ---------------------------------------------------------------------
-- T_RS_SENSOR (6 linhas) -> id_sensor 1..6
-- ---------------------------------------------------------------------
INSERT INTO T_RS_SENSOR (id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor) VALUES (1, 'RS-NIV-0001', 'NIVEL',       DATE '2025-11-03', 'ATIVO');
INSERT INTO T_RS_SENSOR (id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor) VALUES (1, 'RS-PLU-0001', 'PLUVIOMETRO', DATE '2025-11-03', 'ATIVO');
INSERT INTO T_RS_SENSOR (id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor) VALUES (2, 'RS-NIV-0002', 'NIVEL',       DATE '2025-11-10', 'ATIVO');
INSERT INTO T_RS_SENSOR (id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor) VALUES (2, 'RS-PLU-0002', 'PLUVIOMETRO', DATE '2025-11-10', 'ATIVO');
INSERT INTO T_RS_SENSOR (id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor) VALUES (3, 'RS-NIV-0003', 'NIVEL',       DATE '2025-12-01', 'ATIVO');
INSERT INTO T_RS_SENSOR (id_ponto, cd_serie, tp_sensor, dt_instalacao, st_sensor) VALUES (4, 'RS-NIV-0004', 'NIVEL',       DATE '2025-12-01', 'MANUTENCAO');

-- ---------------------------------------------------------------------
-- T_RS_LEITURA (12 linhas) - temporal simulado de 10/02/2026
-- NIVEL = cm de lamina d'agua | PLUVIOMETRO = mm de chuva na ultima hora
-- ---------------------------------------------------------------------
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (2, TIMESTAMP '2026-02-10 16:50:00', 18.40);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (1, TIMESTAMP '2026-02-10 17:00:00',  6.00);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (2, TIMESTAMP '2026-02-10 17:10:00', 42.60);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (1, TIMESTAMP '2026-02-10 17:20:00', 17.50);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (1, TIMESTAMP '2026-02-10 17:40:00', 33.00);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (4, TIMESTAMP '2026-02-10 17:10:00', 38.20);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (3, TIMESTAMP '2026-02-10 17:25:00', 22.00);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (3, TIMESTAMP '2026-02-10 17:45:00', 41.50);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (5, TIMESTAMP '2026-02-10 17:30:00', 12.00);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (5, TIMESTAMP '2026-02-10 18:30:00',  3.00);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (1, TIMESTAMP '2026-02-10 19:05:00',  4.00);
INSERT INTO T_RS_LEITURA (id_sensor, dt_leitura, nr_valor) VALUES (3, TIMESTAMP '2026-02-10 19:55:00',  6.00);

-- ---------------------------------------------------------------------
-- T_RS_CIDADAO (6 linhas) -> id_cidadao 1..6  (pessoas ficticias)
-- ---------------------------------------------------------------------
INSERT INTO T_RS_CIDADAO (id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, st_recebe_alerta, dt_cadastro) VALUES (1, 'Aparecida Souza (ficticia)',  'aparecida.souza@exemplo.com', '(11) 90000-0001', 'IDOSO',       'S', DATE '2025-11-15');
INSERT INTO T_RS_CIDADAO (id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, st_recebe_alerta, dt_cadastro) VALUES (1, 'Rafael Lima (ficticio)',      'rafael.lima@exemplo.com',     '(11) 90000-0002', 'TRABALHADOR', 'S', DATE '2025-11-16');
INSERT INTO T_RS_CIDADAO (id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, st_recebe_alerta, dt_cadastro) VALUES (1, 'Juliana Alves (ficticia)',    'juliana.alves@exemplo.com',   '(11) 90000-0003', 'RESPONSAVEL', 'S', DATE '2025-11-20');
INSERT INTO T_RS_CIDADAO (id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, st_recebe_alerta, dt_cadastro) VALUES (2, 'Marcos Pereira (ficticio)',   'marcos.pereira@exemplo.com',  '(11) 90000-0004', 'AGENTE',      'S', DATE '2025-12-02');
INSERT INTO T_RS_CIDADAO (id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, st_recebe_alerta, dt_cadastro) VALUES (4, 'Beatriz Costa (ficticia)',    'beatriz.costa@exemplo.com',   '(11) 90000-0005', 'PCD',         'S', DATE '2025-12-05');
INSERT INTO T_RS_CIDADAO (id_bairro, nm_cidadao, ds_email, nr_telefone, tp_perfil, st_recebe_alerta, dt_cadastro) VALUES (1, 'Joao Martins (ficticio)',     'joao.martins@exemplo.com',    NULL,              'MORADOR',     'N', DATE '2026-01-08');

-- ---------------------------------------------------------------------
-- T_RS_OCORRENCIA (6 linhas) - relatos dos moradores
-- ---------------------------------------------------------------------
INSERT INTO T_RS_OCORRENCIA (id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, ds_ocorrencia, st_ocorrencia) VALUES (1, 1, TIMESTAMP '2026-02-10 17:25:00', 20, 'Agua cobrindo a calcada, nao consigo passar com o carrinho de feira', 'CONFIRMADA');
INSERT INTO T_RS_OCORRENCIA (id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, ds_ocorrencia, st_ocorrencia) VALUES (2, 2, TIMESTAMP '2026-02-10 17:30:00', 42, 'Passagem sob a rodovia com agua acima do joelho',                 'CONFIRMADA');
INSERT INTO T_RS_OCORRENCIA (id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, ds_ocorrencia, st_ocorrencia) VALUES (3, 4, TIMESTAMP '2026-02-10 17:35:00', 12, 'Entrada da escola com poca grande, criancas sendo carregadas',     'RESOLVIDA');
INSERT INTO T_RS_OCORRENCIA (id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, ds_ocorrencia, st_ocorrencia) VALUES (5, 6, TIMESTAMP '2026-02-10 17:50:00', 15, 'Rampa da UBS alagada, cadeirantes sem acesso',                     'ABERTA');
INSERT INTO T_RS_OCORRENCIA (id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, ds_ocorrencia, st_ocorrencia) VALUES (6, 3, TIMESTAMP '2026-02-10 18:05:00',  5, 'Agua ja baixando em frente ao mercado',                            'DESCARTADA');
INSERT INTO T_RS_OCORRENCIA (id_cidadao, id_ponto, dt_ocorrencia, nr_altura_agua_cm, ds_ocorrencia, st_ocorrencia) VALUES (4, 5, TIMESTAMP '2026-02-10 18:10:00', 25, 'Ponto de onibus isolado, passageiros aguardando na rua',           'ABERTA');

-- ---------------------------------------------------------------------
-- T_RS_ALERTA (5 linhas) -> id_alerta 1..5 (o ultimo segue em aberto)
-- ---------------------------------------------------------------------
INSERT INTO T_RS_ALERTA (id_ponto, tp_nivel, ds_mensagem, dt_emissao, dt_encerramento) VALUES (1, 'ATENCAO',    'Baixada do corrego: agua acumulando. Evite a calcada do lado do corrego.',      TIMESTAMP '2026-02-10 17:20:00', TIMESTAMP '2026-02-10 17:40:00');
INSERT INTO T_RS_ALERTA (id_ponto, tp_nivel, ds_mensagem, dt_emissao, dt_encerramento) VALUES (1, 'EMERGENCIA', 'Baixada do corrego intransitavel. Use a rota pela rua de cima.',                 TIMESTAMP '2026-02-10 17:40:00', TIMESTAMP '2026-02-10 19:10:00');
INSERT INTO T_RS_ALERTA (id_ponto, tp_nivel, ds_mensagem, dt_emissao, dt_encerramento) VALUES (2, 'ALERTA',     'Passagem sob a rodovia com mais de 20 cm de agua. Pedestres devem evitar.',     TIMESTAMP '2026-02-10 17:25:00', TIMESTAMP '2026-02-10 17:45:00');
INSERT INTO T_RS_ALERTA (id_ponto, tp_nivel, ds_mensagem, dt_emissao, dt_encerramento) VALUES (2, 'EMERGENCIA', 'Passagem sob a rodovia intransitavel. Onibus desviados.',                       TIMESTAMP '2026-02-10 17:45:00', TIMESTAMP '2026-02-10 20:00:00');
INSERT INTO T_RS_ALERTA (id_ponto, tp_nivel, ds_mensagem, dt_emissao, dt_encerramento) VALUES (4, 'ATENCAO',    'Acesso a escola com agua acumulada. Use o portao lateral.',                     TIMESTAMP '2026-02-10 17:30:00', NULL);

-- ---------------------------------------------------------------------
-- T_RS_NOTIFICACAO (6 linhas)
-- ---------------------------------------------------------------------
INSERT INTO T_RS_NOTIFICACAO (id_alerta, id_cidadao, dt_envio, tp_canal, st_lida) VALUES (1, 1, TIMESTAMP '2026-02-10 17:20:05', 'WHATSAPP', 'S');
INSERT INTO T_RS_NOTIFICACAO (id_alerta, id_cidadao, dt_envio, tp_canal, st_lida) VALUES (2, 1, TIMESTAMP '2026-02-10 17:40:05', 'WHATSAPP', 'S');
INSERT INTO T_RS_NOTIFICACAO (id_alerta, id_cidadao, dt_envio, tp_canal, st_lida) VALUES (2, 3, TIMESTAMP '2026-02-10 17:40:06', 'PUSH',     'N');
INSERT INTO T_RS_NOTIFICACAO (id_alerta, id_cidadao, dt_envio, tp_canal, st_lida) VALUES (3, 2, TIMESTAMP '2026-02-10 17:25:04', 'PUSH',     'S');
INSERT INTO T_RS_NOTIFICACAO (id_alerta, id_cidadao, dt_envio, tp_canal, st_lida) VALUES (4, 2, TIMESTAMP '2026-02-10 17:45:03', 'SMS',      'S');
INSERT INTO T_RS_NOTIFICACAO (id_alerta, id_cidadao, dt_envio, tp_canal, st_lida) VALUES (5, 3, TIMESTAMP '2026-02-10 17:30:02', 'PUSH',     'N');

COMMIT;
