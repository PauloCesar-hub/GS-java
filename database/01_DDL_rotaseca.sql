-- =====================================================================
-- RotaSeca - CitizenTech | Global Solution 2026.2 - FIAP
-- Disciplina: Database Design
-- Script 01 - DDL (Data Definition Language)
-- SGBD alvo: Oracle Database 19c (servidor Oracle FIAP)
--
-- Ordem de execucao: 01_DDL -> 02_DML -> 03_DQL
-- Este script remove as tabelas (se existirem) e as recria do zero.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0) Limpeza: remove as tabelas caso ja existam (ordem: filhas -> pais)
--    O bloco ignora o erro ORA-00942 (tabela inexistente).
-- ---------------------------------------------------------------------
BEGIN
    FOR t IN (SELECT table_name FROM user_tables
               WHERE table_name IN ('T_RS_NOTIFICACAO', 'T_RS_ALERTA', 'T_RS_OCORRENCIA',
                                    'T_RS_LEITURA', 'T_RS_SENSOR', 'T_RS_CIDADAO',
                                    'T_RS_ABRIGO', 'T_RS_PONTO_RISCO', 'T_RS_BAIRRO'))
    LOOP
        EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS PURGE';
    END LOOP;
END;
/

-- ---------------------------------------------------------------------
-- 1) T_RS_BAIRRO - bairros atendidos pela solucao
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_BAIRRO (
    id_bairro         NUMBER(10)    GENERATED ALWAYS AS IDENTITY,
    nm_bairro         VARCHAR2(80)  NOT NULL,
    nm_subprefeitura  VARCHAR2(80)  NOT NULL,
    nm_zona           VARCHAR2(10)  NOT NULL
);

ALTER TABLE T_RS_BAIRRO ADD CONSTRAINT PK_RS_BAIRRO PRIMARY KEY (id_bairro);
ALTER TABLE T_RS_BAIRRO ADD CONSTRAINT UN_RS_BAIRRO_NOME UNIQUE (nm_bairro, nm_subprefeitura);
ALTER TABLE T_RS_BAIRRO ADD CONSTRAINT CK_RS_BAIRRO_ZONA
    CHECK (nm_zona IN ('NORTE', 'SUL', 'LESTE', 'OESTE', 'CENTRO'));

-- ---------------------------------------------------------------------
-- 2) T_RS_PONTO_RISCO - trechos de rua que costumam alagar
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_PONTO_RISCO (
    id_ponto            NUMBER(10)    GENERATED ALWAYS AS IDENTITY,
    id_bairro           NUMBER(10)    NOT NULL,
    nm_ponto            VARCHAR2(100) NOT NULL,
    ds_referencia       VARCHAR2(200),
    nr_latitude         NUMBER(9,6)   NOT NULL,
    nr_longitude        NUMBER(9,6)   NOT NULL,
    nr_cota_atencao_cm  NUMBER(5)     NOT NULL,
    nr_cota_critica_cm  NUMBER(5)     NOT NULL,
    st_ativo            CHAR(1)       DEFAULT 'S' NOT NULL
);

ALTER TABLE T_RS_PONTO_RISCO ADD CONSTRAINT PK_RS_PONTO_RISCO PRIMARY KEY (id_ponto);
ALTER TABLE T_RS_PONTO_RISCO ADD CONSTRAINT FK_RS_PONTO_BAIRRO
    FOREIGN KEY (id_bairro) REFERENCES T_RS_BAIRRO (id_bairro);
ALTER TABLE T_RS_PONTO_RISCO ADD CONSTRAINT CK_RS_PONTO_ATIVO CHECK (st_ativo IN ('S', 'N'));
ALTER TABLE T_RS_PONTO_RISCO ADD CONSTRAINT CK_RS_PONTO_COTAS
    CHECK (nr_cota_atencao_cm > 0 AND nr_cota_critica_cm > nr_cota_atencao_cm);
ALTER TABLE T_RS_PONTO_RISCO ADD CONSTRAINT CK_RS_PONTO_LAT CHECK (nr_latitude BETWEEN -90 AND 90);
ALTER TABLE T_RS_PONTO_RISCO ADD CONSTRAINT CK_RS_PONTO_LON CHECK (nr_longitude BETWEEN -180 AND 180);

-- ---------------------------------------------------------------------
-- 3) T_RS_ABRIGO - pontos de apoio (escolas, igrejas, UBS) para chuva forte
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_ABRIGO (
    id_abrigo      NUMBER(10)    GENERATED ALWAYS AS IDENTITY,
    id_bairro      NUMBER(10)    NOT NULL,
    nm_abrigo      VARCHAR2(100) NOT NULL,
    ds_endereco    VARCHAR2(150) NOT NULL,
    nr_capacidade  NUMBER(5)     NOT NULL,
    st_acessivel   CHAR(1)       DEFAULT 'S' NOT NULL
);

ALTER TABLE T_RS_ABRIGO ADD CONSTRAINT PK_RS_ABRIGO PRIMARY KEY (id_abrigo);
ALTER TABLE T_RS_ABRIGO ADD CONSTRAINT FK_RS_ABRIGO_BAIRRO
    FOREIGN KEY (id_bairro) REFERENCES T_RS_BAIRRO (id_bairro);
ALTER TABLE T_RS_ABRIGO ADD CONSTRAINT CK_RS_ABRIGO_CAP CHECK (nr_capacidade > 0);
ALTER TABLE T_RS_ABRIGO ADD CONSTRAINT CK_RS_ABRIGO_ACESS CHECK (st_acessivel IN ('S', 'N'));

-- ---------------------------------------------------------------------
-- 4) T_RS_SENSOR - sensores de nivel de agua e pluviometros
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_SENSOR (
    id_sensor      NUMBER(10)    GENERATED ALWAYS AS IDENTITY,
    id_ponto       NUMBER(10)    NOT NULL,
    cd_serie       VARCHAR2(30)  NOT NULL,
    tp_sensor      VARCHAR2(12)  NOT NULL,
    dt_instalacao  DATE          NOT NULL,
    st_sensor      VARCHAR2(12)  DEFAULT 'ATIVO' NOT NULL
);

ALTER TABLE T_RS_SENSOR ADD CONSTRAINT PK_RS_SENSOR PRIMARY KEY (id_sensor);
ALTER TABLE T_RS_SENSOR ADD CONSTRAINT FK_RS_SENSOR_PONTO
    FOREIGN KEY (id_ponto) REFERENCES T_RS_PONTO_RISCO (id_ponto);
ALTER TABLE T_RS_SENSOR ADD CONSTRAINT UN_RS_SENSOR_SERIE UNIQUE (cd_serie);
ALTER TABLE T_RS_SENSOR ADD CONSTRAINT CK_RS_SENSOR_TIPO CHECK (tp_sensor IN ('NIVEL', 'PLUVIOMETRO'));
ALTER TABLE T_RS_SENSOR ADD CONSTRAINT CK_RS_SENSOR_STATUS
    CHECK (st_sensor IN ('ATIVO', 'MANUTENCAO', 'INATIVO'));

-- ---------------------------------------------------------------------
-- 5) T_RS_LEITURA - medicoes enviadas pelos sensores
--    nr_valor: cm de lamina d'agua (NIVEL) ou mm de chuva na ultima hora (PLUVIOMETRO)
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_LEITURA (
    id_leitura  NUMBER(12)   GENERATED ALWAYS AS IDENTITY,
    id_sensor   NUMBER(10)   NOT NULL,
    dt_leitura  TIMESTAMP    NOT NULL,
    nr_valor    NUMBER(7,2)  NOT NULL
);

ALTER TABLE T_RS_LEITURA ADD CONSTRAINT PK_RS_LEITURA PRIMARY KEY (id_leitura);
ALTER TABLE T_RS_LEITURA ADD CONSTRAINT FK_RS_LEITURA_SENSOR
    FOREIGN KEY (id_sensor) REFERENCES T_RS_SENSOR (id_sensor);
ALTER TABLE T_RS_LEITURA ADD CONSTRAINT CK_RS_LEITURA_VALOR CHECK (nr_valor >= 0);

-- ---------------------------------------------------------------------
-- 6) T_RS_CIDADAO - moradores cadastrados para receber alertas e relatar
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_CIDADAO (
    id_cidadao        NUMBER(10)    GENERATED ALWAYS AS IDENTITY,
    id_bairro         NUMBER(10)    NOT NULL,
    nm_cidadao        VARCHAR2(100) NOT NULL,
    ds_email          VARCHAR2(120) NOT NULL,
    nr_telefone       VARCHAR2(20),
    tp_perfil         VARCHAR2(12)  NOT NULL,
    st_recebe_alerta  CHAR(1)       DEFAULT 'S' NOT NULL,
    dt_cadastro       DATE          DEFAULT SYSDATE NOT NULL
);

ALTER TABLE T_RS_CIDADAO ADD CONSTRAINT PK_RS_CIDADAO PRIMARY KEY (id_cidadao);
ALTER TABLE T_RS_CIDADAO ADD CONSTRAINT FK_RS_CIDADAO_BAIRRO
    FOREIGN KEY (id_bairro) REFERENCES T_RS_BAIRRO (id_bairro);
ALTER TABLE T_RS_CIDADAO ADD CONSTRAINT UN_RS_CIDADAO_EMAIL UNIQUE (ds_email);
ALTER TABLE T_RS_CIDADAO ADD CONSTRAINT CK_RS_CIDADAO_PERFIL
    CHECK (tp_perfil IN ('MORADOR', 'IDOSO', 'PCD', 'TRABALHADOR', 'RESPONSAVEL', 'AGENTE'));
ALTER TABLE T_RS_CIDADAO ADD CONSTRAINT CK_RS_CIDADAO_ALERTA CHECK (st_recebe_alerta IN ('S', 'N'));

-- ---------------------------------------------------------------------
-- 7) T_RS_OCORRENCIA - relatos de alagamento feitos pelos moradores
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_OCORRENCIA (
    id_ocorrencia      NUMBER(10)    GENERATED ALWAYS AS IDENTITY,
    id_cidadao         NUMBER(10)    NOT NULL,
    id_ponto           NUMBER(10)    NOT NULL,
    dt_ocorrencia      TIMESTAMP     NOT NULL,
    nr_altura_agua_cm  NUMBER(5)     NOT NULL,
    ds_ocorrencia      VARCHAR2(300),
    st_ocorrencia      VARCHAR2(12)  DEFAULT 'ABERTA' NOT NULL
);

ALTER TABLE T_RS_OCORRENCIA ADD CONSTRAINT PK_RS_OCORRENCIA PRIMARY KEY (id_ocorrencia);
ALTER TABLE T_RS_OCORRENCIA ADD CONSTRAINT FK_RS_OCORRENCIA_CIDADAO
    FOREIGN KEY (id_cidadao) REFERENCES T_RS_CIDADAO (id_cidadao);
ALTER TABLE T_RS_OCORRENCIA ADD CONSTRAINT FK_RS_OCORRENCIA_PONTO
    FOREIGN KEY (id_ponto) REFERENCES T_RS_PONTO_RISCO (id_ponto);
ALTER TABLE T_RS_OCORRENCIA ADD CONSTRAINT CK_RS_OCORRENCIA_ALTURA CHECK (nr_altura_agua_cm >= 0);
ALTER TABLE T_RS_OCORRENCIA ADD CONSTRAINT CK_RS_OCORRENCIA_STATUS
    CHECK (st_ocorrencia IN ('ABERTA', 'CONFIRMADA', 'RESOLVIDA', 'DESCARTADA'));

-- ---------------------------------------------------------------------
-- 8) T_RS_ALERTA - alertas emitidos para um ponto de risco
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_ALERTA (
    id_alerta        NUMBER(10)    GENERATED ALWAYS AS IDENTITY,
    id_ponto         NUMBER(10)    NOT NULL,
    tp_nivel         VARCHAR2(12)  NOT NULL,
    ds_mensagem      VARCHAR2(300) NOT NULL,
    dt_emissao       TIMESTAMP     NOT NULL,
    dt_encerramento  TIMESTAMP
);

ALTER TABLE T_RS_ALERTA ADD CONSTRAINT PK_RS_ALERTA PRIMARY KEY (id_alerta);
ALTER TABLE T_RS_ALERTA ADD CONSTRAINT FK_RS_ALERTA_PONTO
    FOREIGN KEY (id_ponto) REFERENCES T_RS_PONTO_RISCO (id_ponto);
ALTER TABLE T_RS_ALERTA ADD CONSTRAINT CK_RS_ALERTA_NIVEL
    CHECK (tp_nivel IN ('ATENCAO', 'ALERTA', 'EMERGENCIA'));
ALTER TABLE T_RS_ALERTA ADD CONSTRAINT CK_RS_ALERTA_DATAS
    CHECK (dt_encerramento IS NULL OR dt_encerramento >= dt_emissao);

-- ---------------------------------------------------------------------
-- 9) T_RS_NOTIFICACAO - entrega de um alerta a um cidadao (N:N)
-- ---------------------------------------------------------------------
CREATE TABLE T_RS_NOTIFICACAO (
    id_notificacao  NUMBER(12)   GENERATED ALWAYS AS IDENTITY,
    id_alerta       NUMBER(10)   NOT NULL,
    id_cidadao      NUMBER(10)   NOT NULL,
    dt_envio        TIMESTAMP    NOT NULL,
    tp_canal        VARCHAR2(10) NOT NULL,
    st_lida         CHAR(1)      DEFAULT 'N' NOT NULL
);

ALTER TABLE T_RS_NOTIFICACAO ADD CONSTRAINT PK_RS_NOTIFICACAO PRIMARY KEY (id_notificacao);
ALTER TABLE T_RS_NOTIFICACAO ADD CONSTRAINT FK_RS_NOTIF_ALERTA
    FOREIGN KEY (id_alerta) REFERENCES T_RS_ALERTA (id_alerta);
ALTER TABLE T_RS_NOTIFICACAO ADD CONSTRAINT FK_RS_NOTIF_CIDADAO
    FOREIGN KEY (id_cidadao) REFERENCES T_RS_CIDADAO (id_cidadao);
ALTER TABLE T_RS_NOTIFICACAO ADD CONSTRAINT UN_RS_NOTIF_ALERTA_CID UNIQUE (id_alerta, id_cidadao);
ALTER TABLE T_RS_NOTIFICACAO ADD CONSTRAINT CK_RS_NOTIF_CANAL
    CHECK (tp_canal IN ('PUSH', 'SMS', 'WHATSAPP', 'EMAIL'));
ALTER TABLE T_RS_NOTIFICACAO ADD CONSTRAINT CK_RS_NOTIF_LIDA CHECK (st_lida IN ('S', 'N'));

-- ---------------------------------------------------------------------
-- Comentarios (dicionario de dados)
-- ---------------------------------------------------------------------
COMMENT ON TABLE T_RS_BAIRRO      IS 'Bairros atendidos pela RotaSeca';
COMMENT ON TABLE T_RS_PONTO_RISCO IS 'Trechos de via com historico de alagamento e suas cotas de alerta';
COMMENT ON TABLE T_RS_ABRIGO      IS 'Pontos de apoio para a populacao durante chuva forte';
COMMENT ON TABLE T_RS_SENSOR      IS 'Sensores de nivel de agua e pluviometros instalados nos pontos';
COMMENT ON TABLE T_RS_LEITURA     IS 'Medicoes dos sensores (cm de lamina ou mm de chuva/hora)';
COMMENT ON TABLE T_RS_CIDADAO     IS 'Moradores cadastrados para receber alertas e registrar relatos';
COMMENT ON TABLE T_RS_OCORRENCIA  IS 'Relatos de alagamento enviados pelos moradores';
COMMENT ON TABLE T_RS_ALERTA      IS 'Alertas de alagamento emitidos por ponto de risco';
COMMENT ON TABLE T_RS_NOTIFICACAO IS 'Registro de envio de cada alerta a cada cidadao';
