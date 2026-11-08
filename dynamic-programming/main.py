"""RotaSeca - Dynamic Programming (Global Solution 2026.2 - FIAP)

Uso:
    python main.py            # menu interativo
    python main.py --demo     # executa todas as funcionalidades e mostra os resultados
    python main.py --bench    # compara forca bruta x memoizacao x tabulacao
    python main.py --api https://SEU-DEPLOY.onrender.com   # usa a situacao real dos pontos da API Java
"""
from __future__ import annotations

import argparse
import json
import random
import sys
import time
import urllib.error
import urllib.request

from rotaseca_dp.dados import (CANDIDATOS_SENSOR, CENARIO_SECO, CENARIO_TEMPORAL, LOCAIS, ORCAMENTO_PADRAO, RUAS)
from rotaseca_dp.floyd import abrigo_mais_proximo, floyd_warshall
from rotaseca_dp.grafo import PERFIS, Grafo
from rotaseca_dp.sensores import Candidato, forca_bruta, memoizacao, tabulacao

GRAFO = Grafo(RUAS)
ABRIGOS = [v for v, (_, eh_abrigo, _) in LOCAIS.items() if eh_abrigo]
ABRIGOS_ACESSIVEIS = [v for v, (_, eh_abrigo, acessivel) in LOCAIS.items() if eh_abrigo and acessivel]
CANDIDATOS = [Candidato(*c) for c in CANDIDATOS_SENSOR]


def nome(v: str) -> str:
    return LOCAIS[v][0]


def situacao_da_api(url_base: str) -> dict[int, str]:
    """Le GET /api/pontos/situacao da API Java. Pontos SEM_DADOS sao tratados como NORMAL."""
    url = url_base.rstrip("/") + "/api/pontos/situacao"
    with urllib.request.urlopen(url, timeout=15) as resp:
        dados = json.load(resp)
    return {int(p["idPonto"]): (p["situacao"] if p["situacao"] != "SEM_DADOS" else "NORMAL") for p in dados}


def mostrar_cenario(situacao: dict[int, str]) -> None:
    pontos = {p: None for _, _, _, p in RUAS if p is not None}
    print("Situacao dos pontos de risco: " + ", ".join(f"P{p}={situacao.get(p, 'NORMAL')}" for p in sorted(pontos)))


def fmt_rota(rota) -> str:
    if rota is None:
        return "SEM ROTA SEGURA - pessoa ilhada. Procure abrigo no local ou ligue 199 (Defesa Civil)."
    return " -> ".join(nome(v) for v in rota.caminho) + f"  ({rota.metros} m, custo {rota.custo})"


def demo_rotas(situacao: dict[int, str]) -> None:
    print("\n[1] ROTA MAIS SEGURA (Dijkstra) - da casa da Dona Cida ate o terminal de onibus")
    for perfil in PERFIS:
        normal = GRAFO.rota_mais_segura("CASA_CIDA", "TERMINAL", CENARIO_SECO, perfil)
        chuva = GRAFO.rota_mais_segura("CASA_CIDA", "TERMINAL", situacao, perfil)
        print(f"  {perfil}:")
        print(f"     dia seco : {fmt_rota(normal)}")
        print(f"     temporal : {fmt_rota(chuva)}")


def demo_ilhados(situacao: dict[int, str]) -> None:
    print("\n[2] QUEM FICA ILHADO (BFS) - locais alcancaveis a partir da casa da Dona Cida")
    for perfil in PERFIS:
        alc = GRAFO.alcancaveis("CASA_CIDA", situacao, perfil)
        fora = sorted(set(GRAFO.vertices) - alc)
        print(f"  {perfil}: alcanca {len(alc)}/{len(GRAFO.vertices)} locais; sem acesso: "
              + (", ".join(nome(v) for v in fora) or "nenhum"))


def demo_abrigos(situacao: dict[int, str], perfil: str = "MOBILIDADE_REDUZIDA") -> None:
    print(f"\n[3] PONTO DE APOIO MAIS PROXIMO DE CADA LOCAL (Floyd-Warshall / tabulacao) - perfil {perfil}")
    tabela = floyd_warshall(GRAFO, situacao, perfil)
    abrigos = ABRIGOS_ACESSIVEIS if perfil == "MOBILIDADE_REDUZIDA" else ABRIGOS
    if perfil == "MOBILIDADE_REDUZIDA":
        print("  (considera so pontos de apoio acessiveis para cadeira de rodas)")
    for v in GRAFO.vertices:
        if v in ABRIGOS:
            continue
        r = abrigo_mais_proximo(tabela, v, abrigos)
        if r is None:
            print(f"  {nome(v):<36} -> nenhum abrigo alcancavel")
        else:
            caminho = tabela.caminho(v, r[0])
            print(f"  {nome(v):<36} -> {nome(r[0])} (custo {r[1]:.0f}, {len(caminho) - 1} trecho(s))")


def demo_sensores(orcamento: int = ORCAMENTO_PADRAO) -> None:
    print(f"\n[4] ONDE INSTALAR SENSORES COM R$ {orcamento:,} (mochila 0/1)".replace(",", "."))
    for c in CANDIDATOS:
        print(f"  {c.codigo:<4} R$ {c.custo:>5}  {c.pessoas:>5} pessoas/dia  {c.descricao}")
    plano_memo, subproblemas = memoizacao(CANDIDATOS, orcamento)
    plano_tab, tabela = tabulacao(CANDIDATOS, orcamento)
    plano_bf = forca_bruta(CANDIDATOS, orcamento)
    print(f"  Memoizacao : {list(plano_memo.escolhidos)} -> {plano_memo.pessoas_protegidas} pessoas, "
          f"R$ {plano_memo.custo_total} ({subproblemas} subproblemas em cache)")
    print(f"  Tabulacao  : {list(plano_tab.escolhidos)} -> {plano_tab.pessoas_protegidas} pessoas, "
          f"R$ {plano_tab.custo_total} (tabela {len(tabela)}x{len(tabela[0])})")
    print(f"  Forca bruta: {list(plano_bf.escolhidos)} -> {plano_bf.pessoas_protegidas} pessoas (conferencia)")
    iguais = plano_memo.pessoas_protegidas == plano_tab.pessoas_protegidas == plano_bf.pessoas_protegidas
    print("  Resultado conferido: as tres abordagens chegam ao mesmo otimo." if iguais else "  ERRO: resultados diferentes!")


def benchmark() -> None:
    print("\n[5] DESEMPENHO - candidatos aleatorios (custos multiplos de R$ 100), orcamento R$ 30.000")
    rnd = random.Random(42)
    print(f"  {'n':>3} {'forca bruta':>13} {'memoizacao':>12} {'tabulacao':>11}")
    for n in (10, 14, 18, 20, 60, 200):
        cands = [Candidato(f"C{i}", "", rnd.randint(5, 60) * 100, rnd.randint(50, 3000)) for i in range(n)]
        tempos = []
        for nome_alg, f in (("bf", forca_bruta), ("memo", memoizacao), ("tab", tabulacao)):
            if nome_alg == "bf" and n > 20:
                tempos.append(None)
                continue
            t0 = time.perf_counter()
            f(cands, 30000)
            tempos.append(time.perf_counter() - t0)
        fmt = lambda t: "   (inviavel)" if t is None else f"{t * 1000:>10.1f} ms"
        print(f"  {n:>3} {fmt(tempos[0]):>13} {fmt(tempos[1]):>12} {fmt(tempos[2]):>11}")
    print("  Forca bruta cresce como 2^n; as versoes de PD crescem como n x W (W = orcamento / 100).")


def escolher(opcoes: list[str], titulo: str) -> str:
    print(titulo)
    for i, o in enumerate(opcoes, 1):
        print(f"  {i}. {nome(o) if o in LOCAIS else o}")
    while True:
        entrada = input("Numero: ").strip()
        if entrada.isdigit() and 1 <= int(entrada) <= len(opcoes):
            return opcoes[int(entrada) - 1]
        print(f"Opcao invalida. Digite de 1 a {len(opcoes)}.")


def menu(situacao: dict[int, str]) -> None:
    locais = [v for v in GRAFO.vertices]
    while True:
        print("\n=== RotaSeca - rotas seguras e plano de sensores ===")
        mostrar_cenario(situacao)
        print("1. Rota mais segura entre dois locais\n2. Locais alcancaveis (quem fica ilhado)\n"
              "3. Ponto de apoio mais proximo de cada local\n4. Plano de sensores com orcamento\n"
              "5. Comparar desempenho dos algoritmos\n6. Trocar cenario (temporal / dia seco)\n0. Sair")
        op = input("Opcao: ").strip()
        try:
            if op == "1":
                o = escolher(locais, "Origem:")
                d = escolher(locais, "Destino:")
                p = escolher(list(PERFIS), "Perfil:")
                print(fmt_rota(GRAFO.rota_mais_segura(o, d, situacao, p)))
            elif op == "2":
                o = escolher(locais, "Origem:")
                p = escolher(list(PERFIS), "Perfil:")
                alc = GRAFO.alcancaveis(o, situacao, p)
                print("Alcancaveis: " + ", ".join(nome(v) for v in sorted(alc)))
            elif op == "3":
                demo_abrigos(situacao, escolher(list(PERFIS), "Perfil:"))
            elif op == "4":
                valor = input(f"Orcamento em R$ (Enter = {ORCAMENTO_PADRAO}): ").strip()
                orc = ORCAMENTO_PADRAO if not valor else int(valor)
                demo_sensores(orc)
            elif op == "5":
                benchmark()
            elif op == "6":
                situacao = CENARIO_TEMPORAL if situacao is CENARIO_SECO else CENARIO_SECO
            elif op == "0":
                print("Ate logo!")
                return
            else:
                print("Opcao invalida.")
        except ValueError as e:
            print(f"Entrada invalida: {e}")


def main() -> None:
    parser = argparse.ArgumentParser(description="RotaSeca - Programacao Dinamica")
    parser.add_argument("--demo", action="store_true", help="executa todas as funcionalidades")
    parser.add_argument("--bench", action="store_true", help="compara o desempenho dos algoritmos")
    parser.add_argument("--api", help="URL base da API Java para ler a situacao real dos pontos")
    args = parser.parse_args()

    situacao = CENARIO_TEMPORAL
    if args.api:
        try:
            situacao = situacao_da_api(args.api)
            print(f"Situacao carregada da API ({args.api}).")
        except (urllib.error.URLError, TimeoutError, ValueError, KeyError) as e:
            print(f"Nao foi possivel ler a API ({e}). Usando o cenario simulado de temporal.")

    if args.demo or args.bench:
        if args.demo:
            mostrar_cenario(situacao)
            demo_rotas(situacao)
            demo_ilhados(situacao)
            demo_abrigos(situacao)
            demo_sensores()
        if args.bench:
            benchmark()
        return
    try:
        menu(situacao)
    except (KeyboardInterrupt, EOFError):
        print("\nEncerrado.")
        sys.exit(0)


if __name__ == "__main__":
    main()
