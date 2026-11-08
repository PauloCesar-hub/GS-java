"""Grafo das ruas do bairro e busca da rota mais segura (Dijkstra) e de quem fica ilhado (BFS)."""
from __future__ import annotations

import heapq
from collections import deque
from dataclasses import dataclass

NIVEIS = ["NORMAL", "ATENCAO", "ALERTA", "EMERGENCIA"]

# Para cada perfil: multiplicador de custo por nivel do trecho. None = trecho bloqueado.
# Ex.: para quem anda a pe, um trecho em ATENCAO "custa" 60% a mais (passa, mas so se valer a pena).
PERFIS = {
    "PEDESTRE": {"NORMAL": 1.0, "ATENCAO": 1.6, "ALERTA": None, "EMERGENCIA": None},
    "MOBILIDADE_REDUZIDA": {"NORMAL": 1.0, "ATENCAO": None, "ALERTA": None, "EMERGENCIA": None},
    "VEICULO": {"NORMAL": 1.0, "ATENCAO": 1.2, "ALERTA": 2.0, "EMERGENCIA": None},
}


@dataclass(frozen=True)
class Rota:
    caminho: list[str]
    metros: int
    custo: float


class Grafo:
    """Lista de adjacencia: vertice -> lista de (vizinho, metros, id_ponto_risco)."""

    def __init__(self, ruas: list[tuple[str, str, int, int | None]]):
        self.adj: dict[str, list[tuple[str, int, int | None]]] = {}
        for origem, destino, metros, ponto in ruas:
            if metros <= 0:
                raise ValueError(f"Distancia invalida entre {origem} e {destino}: {metros}")
            self.adj.setdefault(origem, []).append((destino, metros, ponto))
            self.adj.setdefault(destino, []).append((origem, metros, ponto))

    @property
    def vertices(self) -> list[str]:
        return sorted(self.adj)

    def peso(self, metros: int, ponto: int | None, situacao: dict[int, str], perfil: str) -> float | None:
        """Custo de atravessar a rua para o perfil; None se o trecho estiver bloqueado."""
        if perfil not in PERFIS:
            raise ValueError(f"Perfil desconhecido: {perfil}. Use {list(PERFIS)}")
        nivel = "NORMAL" if ponto is None else situacao.get(ponto, "NORMAL")
        fator = PERFIS[perfil][nivel]
        return None if fator is None else metros * fator

    def rota_mais_segura(self, origem: str, destino: str, situacao: dict[int, str],
                         perfil: str = "PEDESTRE") -> Rota | None:
        """Dijkstra com fila de prioridade (heap). O(E log V).

        Ignora trechos bloqueados para o perfil e penaliza trechos em atencao.
        Retorna None se o destino ficar inalcancavel (pessoa ilhada).
        """
        self._validar(origem, destino)
        dist = {origem: 0.0}
        metros = {origem: 0}
        anterior: dict[str, str] = {}
        heap = [(0.0, origem)]
        visitados = set()
        while heap:
            custo, atual = heapq.heappop(heap)
            if atual in visitados:
                continue
            visitados.add(atual)
            if atual == destino:
                break
            for vizinho, m, ponto in self.adj[atual]:
                p = self.peso(m, ponto, situacao, perfil)
                if p is None or vizinho in visitados:
                    continue
                novo = custo + p
                if novo < dist.get(vizinho, float("inf")):
                    dist[vizinho] = novo
                    metros[vizinho] = metros[atual] + m
                    anterior[vizinho] = atual
                    heapq.heappush(heap, (novo, vizinho))
        if destino not in visitados:
            return None
        caminho = [destino]
        while caminho[-1] != origem:
            caminho.append(anterior[caminho[-1]])
        caminho.reverse()
        return Rota(caminho, metros[destino], round(dist[destino], 1))

    def alcancaveis(self, origem: str, situacao: dict[int, str], perfil: str = "PEDESTRE") -> set[str]:
        """BFS: todos os locais que a pessoa consegue alcancar sem passar por trecho bloqueado. O(V + E)."""
        self._validar(origem)
        vistos = {origem}
        fila = deque([origem])
        while fila:
            atual = fila.popleft()
            for vizinho, m, ponto in self.adj[atual]:
                if vizinho not in vistos and self.peso(m, ponto, situacao, perfil) is not None:
                    vistos.add(vizinho)
                    fila.append(vizinho)
        return vistos

    def _validar(self, *vertices: str) -> None:
        for v in vertices:
            if v not in self.adj:
                raise KeyError(f"Local desconhecido: {v}")
