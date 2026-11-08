"""Regera diagramas e PDFs. Edite equipe.json (RMs, turma, links, escuta) e rode: python build_all.py --so-pdfs

Requisitos: pip install playwright pypdf && playwright install chromium
            graphviz (dot) e plantuml no PATH para os diagramas.
"""
import subprocess
import sys
from pathlib import Path

AQUI = Path(__file__).resolve().parent
passos = [
    [sys.executable, "gen_erd.py", "../database/01_DDL_rotaseca.sql", "img/modelo_logico"],
    [sys.executable, "gen_uml.py"],
    ["plantuml", "-charset", "UTF-8", "-tpng", "-Sdpi=150", "img/casos_de_uso.puml"],
    [sys.executable, "doc_database.py"],
    [sys.executable, "doc_ddd.py"],
    [sys.executable, "doc_agile.py"],
    [sys.executable, "doc_guias.py"],
]
if "--so-pdfs" in sys.argv:   # pula os diagramas (nao precisa de graphviz/plantuml)
    passos = passos[3:]
for cmd in passos:
    print(">>", " ".join(cmd))
    subprocess.run(cmd, cwd=AQUI, check=True)
print("Tudo gerado.")
