"""Gera a base SINTETICA rotaseca_chuvas_alagamentos.csv usada no notebook.

Por que sintetica: os combinados da GS proibem dados pessoais reais, e os dados
publicos de alagamento de Sao Paulo (CGE/CET) nao trazem, por ponto e por hora,
a altura da lamina d'agua junto com chuva e bueiros. Por isso simulamos registros
horarios de chuva para os 6 pontos de risco da RotaSeca (os mesmos do banco
Oracle e da API Java), com regras fisicas simples e ruido aleatorio.

Calibracao (ordem de grandeza, nao valores oficiais):
  * intensidade de chuva: fraca < 5 mm/h, moderada 5-25 mm/h, forte 25-50 mm/h,
    muito forte > 50 mm/h (faixas usuais em boletins meteorologicos, ex.: CGE-SP);
  * estacao chuvosa de outubro a marco, com mais temporais no fim da tarde;
  * via considerada intransitavel quando a lamina passa da cota critica do ponto
    (as mesmas cotas da tabela T_RS_PONTO_RISCO).

Execucao: python gerar_dados.py  (semente fixa -> sempre o mesmo arquivo)
"""
from pathlib import Path

import numpy as np
import pandas as pd

SEMENTE = 2026
N_EVENTOS_HORA = 3200  # horas com chuva registradas por ponto ao longo de 2 anos (total = 6 x)

# id_ponto: (nome, cota_critica_cm, area_impermeavel_pct, distancia_corrego_m, altitude_relativa_m)
PONTOS = {
    1: ("Baixada do corrego", 30, 88, 15, 0.5),
    2: ("Passagem sob a rodovia", 40, 95, 120, -1.5),
    3: ("Rua do mercado", 25, 82, 160, 1.0),
    4: ("Acesso a escola", 25, 70, 240, 2.0),
    5: ("Ponto de onibus da avenida", 30, 90, 300, 1.5),
    6: ("Entorno da UBS", 20, 75, 200, 2.5),
}


def gerar() -> pd.DataFrame:
    rng = np.random.default_rng(SEMENTE)
    # horas de chuva espalhadas em 2024-2025, com mais chance na estacao chuvosa
    inicio = pd.Timestamp("2024-01-01")
    horas_totais = int((pd.Timestamp("2025-12-31 23:00") - inicio) / pd.Timedelta(hours=1))
    todas = inicio + pd.to_timedelta(np.arange(horas_totais), unit="h")
    mes = todas.month
    hora = todas.hour
    peso = np.where(np.isin(mes, [10, 11, 12, 1, 2, 3]), 3.0, 1.0) * np.where((hora >= 14) & (hora <= 20), 2.0, 1.0)
    escolhidas = np.sort(rng.choice(len(todas), size=N_EVENTOS_HORA, replace=False, p=peso / peso.sum()))
    datas = todas[escolhidas]

    chuvoso = np.isin(datas.month, [10, 11, 12, 1, 2, 3]).astype(int)
    fim_tarde = ((datas.hour >= 14) & (datas.hour <= 20)).astype(int)
    # chuva da hora: mistura de garoa (gamma pequena) e temporal (gamma grande)
    temporal = rng.random(N_EVENTOS_HORA) < (0.10 + 0.18 * chuvoso + 0.10 * fim_tarde)
    chuva_1h = np.where(temporal, rng.gamma(3.0, 11.0, N_EVENTOS_HORA), rng.gamma(1.3, 2.2, N_EVENTOS_HORA))
    chuva_1h = np.clip(chuva_1h, 0.2, 120)
    chuva_3h = chuva_1h + rng.gamma(1.5, 3.0 + 6.0 * temporal, N_EVENTOS_HORA)          # acumulado 3 horas
    chuva_24h = chuva_3h + rng.gamma(1.4, 5.0 + 10.0 * chuvoso, N_EVENTOS_HORA)         # acumulado 24 horas

    linhas = []
    for id_ponto, (nome, cota, imperm, dist, alt) in PONTOS.items():
        # bueiros com relato de obstrucao (zeladoria) perto do ponto naquele dia
        bueiros = rng.poisson(0.8 + 0.6 * chuvoso, N_EVENTOS_HORA).clip(0, 6)
        efeito_local = 0.18 * (imperm - 80) - 0.035 * dist - 3.2 * alt
        lamina = (0.62 * chuva_1h + 0.11 * (chuva_3h - chuva_1h) + 0.045 * chuva_24h
                  + 2.4 * bueiros + efeito_local + 6.0
                  + rng.normal(0, 4.5, N_EVENTOS_HORA))
        lamina = np.clip(lamina, 0, None).round(1)
        # interdicao registrada por agente: lamina >= cota critica, com 1,5% de erro de registro
        intransitavel = (lamina >= cota).astype(int)
        erro = rng.random(N_EVENTOS_HORA) < 0.015
        intransitavel = np.where(erro, 1 - intransitavel, intransitavel)
        linhas.append(pd.DataFrame({
            "data_hora": datas,
            "id_ponto": id_ponto,
            "nome_ponto": nome,
            "cota_critica_cm": cota,
            "area_impermeavel_pct": imperm,
            "distancia_corrego_m": dist,
            "altitude_relativa_m": alt,
            "estacao_chuvosa": chuvoso,
            "hora": datas.hour,
            "chuva_1h_mm": chuva_1h.round(1),
            "chuva_3h_mm": chuva_3h.round(1),
            "chuva_24h_mm": chuva_24h.round(1),
            "bueiros_obstruidos": bueiros,
            "lamina_agua_cm": lamina,
            "intransitavel": intransitavel,
        }))
    df = pd.concat(linhas, ignore_index=True).sort_values(["data_hora", "id_ponto"]).reset_index(drop=True)
    # alguns registros com falha de sensor (valores ausentes) para exercitar a limpeza
    falhas = rng.choice(len(df), size=60, replace=False)
    df.loc[falhas[:30], "chuva_24h_mm"] = np.nan
    df.loc[falhas[30:], "bueiros_obstruidos"] = np.nan
    return df


if __name__ == "__main__":
    destino = Path(__file__).with_name("rotaseca_chuvas_alagamentos.csv")
    base = gerar()
    base.to_csv(destino, index=False)
    print(f"{len(base)} linhas salvas em {destino.name}")
    print(base.describe().T[["mean", "min", "max"]].round(2))
    print("Taxa de intransitavel:", base["intransitavel"].mean().round(3))
