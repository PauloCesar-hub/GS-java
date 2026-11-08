"""Dados simulados do Parque Novo Mundo usados pelos algoritmos.

Os nomes dos locais sao ilustrativos. Os ids de ponto de risco (1 a 6) sao os
mesmos das tabelas T_RS_PONTO_RISCO do banco e da API Java, entao a situacao
de cada ponto (NORMAL, ATENCAO, ALERTA, EMERGENCIA) pode vir direto do endpoint
GET /api/pontos/situacao.
"""

# Locais (vertices do grafo): id -> (nome, eh_abrigo, acessivel_para_cadeira_de_rodas)
# (a acessibilidade dos abrigos e a mesma da coluna st_acessivel de T_RS_ABRIGO)
LOCAIS = {
    "CASA_CIDA": ("Casa da Dona Cida", False, True),
    "ESQ_CORREGO": ("Esquina do corrego", False, True),
    "ESQ_MERCADO": ("Esquina do mercado", False, True),
    "ESQ_ALTA": ("Rua de cima (parte alta)", False, True),
    "PRACA": ("Praca do bairro", False, True),
    "ESCOLA_EST": ("Escola estadual", False, True),
    "UBS": ("UBS", False, True),
    "PONTO_ONIBUS": ("Ponto de onibus da avenida", False, True),
    "PASSAGEM": ("Saida pela passagem sob a rodovia", False, True),
    "TERMINAL": ("Terminal de onibus", False, True),
    "ABR_ESCOLA": ("Escola municipal - ponto de apoio", True, True),
    "ABR_PAROQUIA": ("Salao paroquial", True, False),
    "ABR_ESPORTES": ("Centro esportivo", True, True),
}

# Ruas (arestas nao direcionadas): (origem, destino, metros, id_ponto_risco ou None)
RUAS = [
    ("CASA_CIDA", "ESQ_CORREGO", 180, 1),
    ("CASA_CIDA", "ESQ_ALTA", 260, None),
    ("ESQ_CORREGO", "ESQ_MERCADO", 150, 3),
    ("ESQ_CORREGO", "PASSAGEM", 220, 2),
    ("ESQ_ALTA", "PRACA", 210, None),
    ("ESQ_ALTA", "ABR_PAROQUIA", 140, None),
    ("PRACA", "ESQ_MERCADO", 160, None),
    ("PRACA", "ESCOLA_EST", 190, 4),
    ("PRACA", "ABR_ESCOLA", 480, None),
    ("ESQ_MERCADO", "UBS", 240, 6),
    ("ESQ_MERCADO", "PONTO_ONIBUS", 200, None),
    ("ESCOLA_EST", "ABR_ESCOLA", 120, None),
    ("UBS", "PONTO_ONIBUS", 170, None),
    ("PONTO_ONIBUS", "TERMINAL", 400, 5),
    ("PASSAGEM", "TERMINAL", 300, None),
    ("ABR_ESCOLA", "ABR_ESPORTES", 350, None),
    ("ABR_ESPORTES", "TERMINAL", 280, None),
]

# Cenario de chuva forte (situacao de cada ponto de risco)
CENARIO_TEMPORAL = {1: "ALERTA", 2: "ALERTA", 3: "EMERGENCIA", 4: "ATENCAO", 5: "ALERTA", 6: "NORMAL"}
CENARIO_SECO = {i: "NORMAL" for i in range(1, 7)}

# Candidatos a receber sensor: (id, descricao, custo em R$, pessoas que passam por dia)
# Pontos 5 e 6 nao tem sensor; o 4 esta em manutencao (sao os da consulta 3 do DQL).
CANDIDATOS_SENSOR = [
    ("P4", "Consertar sensor - Acesso a escola", 1200, 900),
    ("P5", "Sensor novo - Ponto de onibus da avenida", 3500, 2100),
    ("P6", "Sensor novo - Entorno da UBS", 3000, 1300),
    ("P7", "Sensor novo - Viela da feira", 2500, 600),
    ("P8", "Sensor novo - Rua da creche", 2800, 1100),
    ("P9", "Sensor novo - Acesso ao viaduto", 4200, 1700),
    ("P10", "Pluviometro extra - Praca", 1500, 400),
]
ORCAMENTO_PADRAO = 10000  # R$
