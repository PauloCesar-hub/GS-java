"""Testes automatizados: python -m unittest discover -s tests -v"""
import random
import unittest

from rotaseca_dp.dados import CANDIDATOS_SENSOR, CENARIO_SECO, CENARIO_TEMPORAL, RUAS
from rotaseca_dp.floyd import INF, abrigo_mais_proximo, floyd_warshall
from rotaseca_dp.grafo import PERFIS, Grafo
from rotaseca_dp.sensores import Candidato, forca_bruta, memoizacao, tabulacao

GRAFO = Grafo(RUAS)


class TestGrafo(unittest.TestCase):

    def test_dia_seco_usa_o_caminho_mais_curto(self):
        rota = GRAFO.rota_mais_segura("CASA_CIDA", "TERMINAL", CENARIO_SECO)
        self.assertEqual(rota.caminho, ["CASA_CIDA", "ESQ_CORREGO", "PASSAGEM", "TERMINAL"])
        self.assertEqual(rota.metros, 700)

    def test_temporal_desvia_dos_trechos_bloqueados(self):
        rota = GRAFO.rota_mais_segura("CASA_CIDA", "TERMINAL", CENARIO_TEMPORAL, "PEDESTRE")
        self.assertNotIn("ESQ_CORREGO", rota.caminho)
        self.assertEqual(rota.caminho[0], "CASA_CIDA")
        self.assertEqual(rota.caminho[-1], "TERMINAL")

    def test_mobilidade_reduzida_nao_passa_em_atencao(self):
        pedestre = GRAFO.rota_mais_segura("CASA_CIDA", "TERMINAL", CENARIO_TEMPORAL, "PEDESTRE")
        cadeirante = GRAFO.rota_mais_segura("CASA_CIDA", "TERMINAL", CENARIO_TEMPORAL, "MOBILIDADE_REDUZIDA")
        self.assertIn("ESCOLA_EST", pedestre.caminho)          # pedestre aceita o trecho em ATENCAO (P4)
        self.assertNotIn("ESCOLA_EST", cadeirante.caminho)     # cadeirante nao
        self.assertGreaterEqual(cadeirante.custo, pedestre.custo)

    def test_sem_rota_quando_ilhado(self):
        tudo_alagado = {p: "EMERGENCIA" for p in range(1, 7)}
        self.assertIsNone(GRAFO.rota_mais_segura("ESQ_CORREGO", "TERMINAL", tudo_alagado))
        self.assertEqual(GRAFO.alcancaveis("ESQ_CORREGO", tudo_alagado), {"ESQ_CORREGO"})

    def test_origem_igual_destino(self):
        rota = GRAFO.rota_mais_segura("PRACA", "PRACA", CENARIO_TEMPORAL)
        self.assertEqual(rota.caminho, ["PRACA"])
        self.assertEqual(rota.metros, 0)

    def test_local_ou_perfil_invalido(self):
        with self.assertRaises(KeyError):
            GRAFO.rota_mais_segura("LUA", "TERMINAL", CENARIO_SECO)
        with self.assertRaises(ValueError):
            GRAFO.rota_mais_segura("PRACA", "TERMINAL", CENARIO_SECO, "BICICLETA")
        with self.assertRaises(ValueError):
            Grafo([("A", "B", 0, None)])

    def test_bfs_veiculo_alcanca_tudo_no_temporal(self):
        self.assertEqual(GRAFO.alcancaveis("CASA_CIDA", CENARIO_TEMPORAL, "VEICULO"), set(GRAFO.vertices))


class TestFloydWarshall(unittest.TestCase):

    def test_floyd_igual_a_dijkstra_para_todos_os_pares(self):
        for cenario in (CENARIO_SECO, CENARIO_TEMPORAL):
            for perfil in PERFIS:
                tabela = floyd_warshall(GRAFO, cenario, perfil)
                for o in GRAFO.vertices:
                    for d in GRAFO.vertices:
                        rota = GRAFO.rota_mais_segura(o, d, cenario, perfil)
                        esperado = INF if rota is None else rota.custo
                        self.assertAlmostEqual(tabela.distancia(o, d), esperado, places=6, msg=f"{perfil} {o}->{d}")
                        caminho = tabela.caminho(o, d)
                        self.assertEqual(caminho is None, rota is None)
                        if caminho:
                            self.assertEqual((caminho[0], caminho[-1]), (o, d))

    def test_abrigo_mais_proximo(self):
        tabela = floyd_warshall(GRAFO, CENARIO_SECO)
        self.assertEqual(abrigo_mais_proximo(tabela, "ESQ_ALTA", ["ABR_ESCOLA", "ABR_PAROQUIA"]),
                         ("ABR_PAROQUIA", 140.0))
        tudo_alagado = floyd_warshall(GRAFO, {p: "EMERGENCIA" for p in range(1, 7)})
        self.assertIsNone(abrigo_mais_proximo(tudo_alagado, "ESQ_CORREGO", ["ABR_ESCOLA"]))


class TestMochila(unittest.TestCase):

    def setUp(self):
        self.cands = [Candidato(*c) for c in CANDIDATOS_SENSOR]

    def test_tres_abordagens_chegam_ao_mesmo_otimo(self):
        for orcamento in (0, 1000, 1200, 5000, 10000, 15000, 100000):
            bf = forca_bruta(self.cands, orcamento)
            memo, _ = memoizacao(self.cands, orcamento)
            tab, _ = tabulacao(self.cands, orcamento)
            self.assertEqual(memo.pessoas_protegidas, bf.pessoas_protegidas, orcamento)
            self.assertEqual(tab.pessoas_protegidas, bf.pessoas_protegidas, orcamento)
            for plano in (memo, tab):
                self.assertLessEqual(plano.custo_total, orcamento)

    def test_orcamento_padrao(self):
        tab, tabela = tabulacao(self.cands, 10000)
        self.assertEqual(tab.pessoas_protegidas, 4700)
        self.assertEqual(len(tabela), len(self.cands) + 1)
        self.assertEqual(len(tabela[0]), 101)

    def test_aleatorio_contra_forca_bruta(self):
        rnd = random.Random(7)
        for _ in range(60):
            n = rnd.randint(0, 11)
            cands = [Candidato(f"C{i}", "", rnd.randint(1, 40) * 100, rnd.randint(0, 2000)) for i in range(n)]
            orc = rnd.randint(0, 120) * 100
            esperado = forca_bruta(cands, orc).pessoas_protegidas
            self.assertEqual(memoizacao(cands, orc)[0].pessoas_protegidas, esperado)
            self.assertEqual(tabulacao(cands, orc)[0].pessoas_protegidas, esperado)

    def test_memoizacao_aguenta_muitos_itens(self):
        cands = [Candidato(f"C{i}", "", 100, 1) for i in range(1500)]
        plano, _ = memoizacao(cands, 50000)
        self.assertEqual(plano.pessoas_protegidas, 500)

    def test_entradas_invalidas(self):
        with self.assertRaises(ValueError):
            tabulacao(self.cands, -1)
        with self.assertRaises(ValueError):
            memoizacao([Candidato("X", "", 150, 10)], 1000)
        with self.assertRaises(ValueError):
            forca_bruta([Candidato("X", "", 0, 10)], 1000)


if __name__ == "__main__":
    unittest.main()
