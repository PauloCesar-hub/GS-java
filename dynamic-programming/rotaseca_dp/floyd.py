"""Floyd-Warshall: Programacao Dinamica por TABULACAO sobre o grafo.

Subproblema: D[k][i][j] = menor custo de i ate j usando apenas os k primeiros
vertices como intermediarios. Recorrencia:
    D[k][i][j] = min(D[k-1][i][j], D[k-1][i][k] + D[k-1][k][j])
A tabela e preenchida de baixo para cima (k = 0..n-1), reaproveitando a
mesma matriz (economia de memoria: O(n^2) em vez de O(n^3)). Tempo O(n^3).

Uso na RotaSeca: quando o alerta dispara, calculamos UMA vez a tabela de todos
para todos e respondemos instantaneamente "qual o ponto de apoio mais proximo"
para qualquer esquina do bairro, sem rodar um Dijkstra por usuario.
"""
from __future__ import annotations

from dataclasses import dataclass

from .grafo import Grafo

INF = float("inf")


@dataclass
class TabelaDistancias:
    vertices: list[str]
    dist: list[list[float]]
    proximo: list[list[int | None]]

    def indice(self, v: str) -> int:
        try:
            return self.vertices.index(v)
        except ValueError:
            raise KeyError(f"Local desconhecido: {v}") from None

    def distancia(self, origem: str, destino: str) -> float:
        return self.dist[self.indice(origem)][self.indice(destino)]

    def caminho(self, origem: str, destino: str) -> list[str] | None:
        i, j = self.indice(origem), self.indice(destino)
        if self.proximo[i][j] is None:
            return None if i != j else [origem]
        passos = [origem]
        while i != j:
            i = self.proximo[i][j]
            passos.append(self.vertices[i])
        return passos


def floyd_warshall(grafo: Grafo, situacao: dict[int, str], perfil: str = "PEDESTRE") -> TabelaDistancias:
    vertices = grafo.vertices
    n = len(vertices)
    idx = {v: i for i, v in enumerate(vertices)}
    dist = [[INF] * n for _ in range(n)]
    proximo: list[list[int | None]] = [[None] * n for _ in range(n)]

    # caso base (k = -1): arestas diretas
    for i in range(n):
        dist[i][i] = 0.0
        proximo[i][i] = i
    for origem, vizinhos in grafo.adj.items():
        for destino, metros, ponto in vizinhos:
            p = grafo.peso(metros, ponto, situacao, perfil)
            if p is not None and p < dist[idx[origem]][idx[destino]]:
                dist[idx[origem]][idx[destino]] = p
                proximo[idx[origem]][idx[destino]] = idx[destino]

    # tabulacao: libera o vertice k como intermediario
    for k in range(n):
        dk = dist[k]
        for i in range(n):
            dik = dist[i][k]
            if dik == INF:
                continue
            di = dist[i]
            for j in range(n):
                via_k = dik + dk[j]
                if via_k < di[j]:
                    di[j] = via_k
                    proximo[i][j] = proximo[i][k]
    return TabelaDistancias(vertices, dist, proximo)


def abrigo_mais_proximo(tabela: TabelaDistancias, origem: str, abrigos: list[str]) -> tuple[str, float] | None:
    """Consulta O(numero de abrigos) na tabela ja pronta."""
    alcancaveis = [(a, tabela.distancia(origem, a)) for a in abrigos if tabela.distancia(origem, a) < INF]
    return min(alcancaveis, key=lambda x: (x[1], x[0])) if alcancaveis else None
