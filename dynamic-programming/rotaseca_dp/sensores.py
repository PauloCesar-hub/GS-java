"""Onde instalar sensores com orcamento limitado: problema da MOCHILA 0/1.

Cada candidato tem um custo (R$) e um beneficio (pessoas que passam pelo ponto
por dia). Queremos o conjunto que protege MAIS pessoas sem passar do orcamento.

Tres implementacoes para comparar:
  * forca_bruta   - testa os 2^n subconjuntos (referencia para conferir as outras)
  * memoizacao    - top-down: recursao + cache (functools.lru_cache)
  * tabulacao     - bottom-up: tabela (n+1) x (W+1) preenchida iterativamente

Os custos sao convertidos para "unidades" de R$ 100 (UNIDADE) para a tabela
ficar pequena: W = orcamento // 100.
"""
from __future__ import annotations

import sys
from dataclasses import dataclass
from functools import lru_cache
from itertools import combinations

UNIDADE = 100


@dataclass(frozen=True)
class Candidato:
    codigo: str
    descricao: str
    custo: int
    pessoas: int


@dataclass(frozen=True)
class Plano:
    escolhidos: tuple[str, ...]
    custo_total: int
    pessoas_protegidas: int


def _preparar(candidatos: list[Candidato], orcamento: int) -> tuple[list[int], list[int], int]:
    if orcamento < 0:
        raise ValueError("Orcamento nao pode ser negativo")
    for c in candidatos:
        if c.custo <= 0 or c.pessoas < 0:
            raise ValueError(f"Candidato invalido: {c}")
        if c.custo % UNIDADE:
            raise ValueError(f"Custo de {c.codigo} deve ser multiplo de R$ {UNIDADE}")
    pesos = [c.custo // UNIDADE for c in candidatos]
    valores = [c.pessoas for c in candidatos]
    return pesos, valores, orcamento // UNIDADE


def _montar_plano(candidatos: list[Candidato], indices: list[int]) -> Plano:
    escolhidos = sorted(indices)
    return Plano(tuple(candidatos[i].codigo for i in escolhidos),
                 sum(candidatos[i].custo for i in escolhidos),
                 sum(candidatos[i].pessoas for i in escolhidos))


def forca_bruta(candidatos: list[Candidato], orcamento: int) -> Plano:
    """O(2^n * n). So serve para poucos candidatos; usada para validar as versoes de PD."""
    pesos, valores, w = _preparar(candidatos, orcamento)
    melhor: tuple[int, list[int]] = (0, [])
    n = len(candidatos)
    for r in range(n + 1):
        for combo in combinations(range(n), r):
            if sum(pesos[i] for i in combo) <= w:
                valor = sum(valores[i] for i in combo)
                if valor > melhor[0]:
                    melhor = (valor, list(combo))
    return _montar_plano(candidatos, melhor[1])


def memoizacao(candidatos: list[Candidato], orcamento: int) -> tuple[Plano, int]:
    """Top-down. melhor(i, w) = maior beneficio usando os itens i..n-1 com capacidade w.

    melhor(i, w) = max( melhor(i+1, w),                         # nao instala o sensor i
                        valor[i] + melhor(i+1, w - peso[i]) )   # instala (se couber)
    Cada par (i, w) e calculado uma unica vez: O(n * W) tempo e memoria.
    Retorna o plano e o numero de subproblemas distintos resolvidos.
    """
    pesos, valores, w_total = _preparar(candidatos, orcamento)
    n = len(candidatos)
    limite = sys.getrecursionlimit()
    sys.setrecursionlimit(max(limite, n + 100))
    try:
        @lru_cache(maxsize=None)
        def melhor(i: int, w: int) -> int:
            if i == n or w == 0:
                return 0
            sem = melhor(i + 1, w)
            if pesos[i] > w:
                return sem
            return max(sem, valores[i] + melhor(i + 1, w - pesos[i]))

        total = melhor(0, w_total)
        # reconstrucao: segue as decisoes que levaram ao otimo
        escolhidos, w = [], w_total
        for i in range(n):
            if melhor(i, w) != melhor(i + 1, w):
                escolhidos.append(i)
                w -= pesos[i]
        plano = _montar_plano(candidatos, escolhidos)
        assert plano.pessoas_protegidas == total
        return plano, melhor.cache_info().currsize
    finally:
        sys.setrecursionlimit(limite)


def tabulacao(candidatos: list[Candidato], orcamento: int) -> tuple[Plano, list[list[int]]]:
    """Bottom-up. T[i][w] = maior beneficio usando os i primeiros itens com capacidade w.

    T[0][w] = 0
    T[i][w] = max(T[i-1][w], valor[i-1] + T[i-1][w - peso[i-1]])  se peso[i-1] <= w
    Tempo e memoria O(n * W). Retorna o plano e a tabela (para exibicao).
    """
    pesos, valores, w_total = _preparar(candidatos, orcamento)
    n = len(candidatos)
    tabela = [[0] * (w_total + 1) for _ in range(n + 1)]
    for i in range(1, n + 1):
        p, v = pesos[i - 1], valores[i - 1]
        linha_ant, linha = tabela[i - 1], tabela[i]
        for w in range(w_total + 1):
            linha[w] = linha_ant[w]
            if p <= w and v + linha_ant[w - p] > linha[w]:
                linha[w] = v + linha_ant[w - p]
    escolhidos, w = [], w_total
    for i in range(n, 0, -1):
        if tabela[i][w] != tabela[i - 1][w]:
            escolhidos.append(i - 1)
            w -= pesos[i - 1]
    return _montar_plano(candidatos, escolhidos), tabela
