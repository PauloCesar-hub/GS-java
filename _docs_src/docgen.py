"""Gera os PDFs de documentacao a partir de HTML (Chromium via Playwright).

Uso interno dos scripts doc_*.py. Requer: pip install playwright pypdf && playwright install chromium
"""
from __future__ import annotations

import asyncio
import html
import json
import re
from pathlib import Path

from pypdf import PdfReader

RAIZ = Path(__file__).resolve().parent
IMG = RAIZ / "img"
EQUIPE = json.loads((RAIZ / "equipe.json").read_text(encoding="utf-8"))

CSS = """
@page { size: A4; margin: 18mm 17mm 18mm 17mm; }
* { box-sizing: border-box; }
body { font-family: "Inter", "Helvetica Neue", Arial, sans-serif; font-size: 10.3pt; color: #1b1f24; line-height: 1.5; margin: 0; }
h1 { font-size: 20pt; color: #0b3c5d; margin: 0 0 6mm; }
h2 { font-size: 15pt; color: #0b3c5d; border-bottom: 2px solid #0b3c5d; padding-bottom: 2mm; margin: 0 0 4mm; break-after: avoid; }
h3 { font-size: 11.5pt; color: #0b3c5d; margin: 5mm 0 2mm; break-after: avoid; }
p { margin: 0 0 3mm; text-align: justify; }
ul, ol { margin: 0 0 3mm 5mm; padding-left: 4mm; }
li { margin-bottom: 1mm; }
.secao { break-before: page; }
.capa { height: 255mm; display: flex; flex-direction: column; justify-content: space-between; }
.capa .topo { color: #0b3c5d; font-weight: 700; letter-spacing: .5px; }
.capa .titulo { margin-top: 30mm; }
.capa .titulo .disc { font-size: 12pt; color: #5b6670; text-transform: uppercase; letter-spacing: 1px; }
.capa .titulo h1 { font-size: 30pt; margin: 3mm 0 2mm; }
.capa .titulo .sub { font-size: 13pt; color: #333; }
.capa table { width: 100%; border-collapse: collapse; margin-top: 4mm; }
.capa td { padding: 2mm 0; border-bottom: 1px solid #d5dbe0; font-size: 11pt; }
.capa .rodape { color: #5b6670; font-size: 10pt; }
table.t { width: 100%; border-collapse: collapse; margin: 2mm 0 4mm; font-size: 8.8pt; break-inside: auto; }
table.t th { background: #0b3c5d; color: #fff; text-align: left; padding: 1.6mm 2mm; font-weight: 600; }
table.t td { border-bottom: 1px solid #dde3e8; padding: 1.5mm 2mm; vertical-align: top; }
table.t tr { break-inside: avoid; }
table.t tr.mvp td { background: #eef6ee; }
.toc { list-style: none; margin: 0; padding: 0; }
.toc li { display: flex; border-bottom: 1px dotted #9aa5ae; padding: 2mm 0; font-size: 11pt; }
.toc li span:first-child { flex: 1; }
.fig { text-align: center; margin: 3mm 0 4mm; break-inside: avoid; }
.fig img { max-width: 100%; border: 1px solid #dde3e8; }
.fig .leg { font-size: 8.5pt; color: #5b6670; margin-top: 1.5mm; }
.box { background: #e6f0f7; border-left: 4px solid #0b3c5d; padding: 3mm 4mm; margin: 2mm 0 4mm; break-inside: avoid; }
.alerta { background: #fff4e5; border-left: 4px solid #d97706; padding: 3mm 4mm; margin: 2mm 0 4mm; break-inside: avoid; }
code, pre { font-family: "DejaVu Sans Mono", Consolas, monospace; font-size: 8.4pt; }
pre { background: #f4f6f8; border: 1px solid #dde3e8; padding: 3mm; white-space: pre-wrap; break-inside: avoid; margin: 0 0 3mm; }
.grid2 { display: grid; grid-template-columns: 1fr 1fr; gap: 5mm; }
.grid3 { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 4mm; }
.card { border: 1px solid #dde3e8; border-radius: 3mm; padding: 3mm 4mm; break-inside: avoid; }
.tag { display: inline-block; padding: .3mm 2mm; border-radius: 2mm; font-size: 8pt; font-weight: 700; color: #fff; }
.M { background: #1e7b34; } .S { background: #2563eb; } .C { background: #8a6d00; } .W { background: #6b7280; }
.tela { break-inside: avoid; display: grid; grid-template-columns: 50mm 1fr; gap: 6mm; margin-bottom: 6mm; align-items: start; }
.tela img { width: 50mm; border: 1px solid #c5ced5; border-radius: 3mm; }
"""


def esc(t: str) -> str:
    return html.escape(str(t))


def capa(disciplina: str, subtitulo: str = "") -> str:
    membros = sorted(EQUIPE["integrantes"], key=lambda m: m["nome"])
    linhas = "".join(f"<tr><td>{esc(m['nome'])}</td><td style='text-align:right'>{esc(m['rm'])}</td></tr>" for m in membros)
    return f"""
<section class="capa">
  <div class="topo">FIAP &middot; ENGENHARIA DE SOFTWARE &middot; 2&ordm; ANO (FEV)<br>
  <span style="font-weight:400;color:#5b6670">Global Solution 2026.2 &middot; Desafio CitizenTech</span></div>
  <div class="titulo">
    <div class="disc">{esc(disciplina)}</div>
    <h1>RotaSeca</h1>
    <div class="sub">{esc(subtitulo or EQUIPE['solucao'])}</div>
  </div>
  <div>
    <p style="margin-bottom:1mm"><strong>Equipe:</strong> {esc(EQUIPE['nome_equipe'])} &nbsp;&middot;&nbsp; <strong>Turma:</strong> {esc(EQUIPE['turma'])}</p>
    <p style="margin-bottom:1mm"><strong>Integrantes</strong> (ordem alfab&eacute;tica)</p>
    <table>{linhas}</table>
  </div>
  <div class="rodape">{esc(EQUIPE['data'])}</div>
</section>"""


def figura(arquivo: str, legenda: str, largura: str = "100%") -> str:
    caminho = (IMG / arquivo).resolve()
    if not caminho.exists():
        caminho = (RAIZ.parent / arquivo).resolve()
    if not caminho.exists():
        raise FileNotFoundError(arquivo)
    return f'<div class="fig"><img src="{caminho.as_uri()}" style="max-width:{largura};max-height:228mm;width:auto;height:auto"><div class="leg">{legenda}</div></div>'


def _toc_html(secoes: list[tuple[str, str]], paginas: dict[str, int] | None) -> str:
    itens = "".join(
        f"<li><span>{esc(titulo)}</span><span>{paginas.get(sid, '') if paginas else ''}</span></li>"
        for sid, titulo in secoes)
    return f'<section class="secao"><h2>Sum&aacute;rio</h2><ul class="toc">{itens}</ul></section>'


def _documento(titulo, capa_html, secoes, corpo, paginas):
    toc = _toc_html(secoes, paginas) if secoes else ""
    return f"""<!DOCTYPE html><html lang="pt-BR"><head><meta charset="utf-8"><title>{esc(titulo)}</title>
<style>{CSS}</style></head><body>{capa_html}{toc}{corpo}</body></html>"""


async def _pdf(html_doc: str, destino: Path) -> None:
    from playwright.async_api import async_playwright
    tmp = destino.with_suffix(".tmp.html")
    tmp.write_text(html_doc, encoding="utf-8")
    try:
        async with async_playwright() as p:
            navegador = await p.chromium.launch()
            pagina = await navegador.new_page()
            await pagina.goto(tmp.as_uri(), wait_until="networkidle")
            await pagina.pdf(path=str(destino), format="A4", print_background=True, display_header_footer=True,
                             header_template="<span></span>",
                             footer_template='<div style="font-size:7.5pt;color:#6b7280;width:100%;padding:0 17mm;'
                                             'display:flex;justify-content:space-between"><span>RotaSeca &middot; '
                                             'Global Solution 2026.2 &middot; FIAP</span><span class="pageNumber"></span></div>',
                             margin={"top": "18mm", "bottom": "18mm", "left": "17mm", "right": "17mm"})
            await navegador.close()
    finally:
        tmp.unlink(missing_ok=True)


def _normalizar(t: str) -> str:
    return re.sub(r"\s+", " ", t).strip().lower()


def gerar(titulo: str, capa_html: str, secoes: list[tuple[str, str]], corpo: str, destino: Path) -> Path:
    """secoes = [(id, titulo_h2)]. Gera duas vezes: a primeira descobre as paginas de cada secao."""
    destino.parent.mkdir(parents=True, exist_ok=True)
    asyncio.run(_pdf(_documento(titulo, capa_html, secoes, corpo, None), destino))
    paginas: dict[str, int] = {}
    if secoes:
        textos = [_normalizar(pg.extract_text() or "") for pg in PdfReader(str(destino)).pages]
        inicio = 2  # pula capa e sumario
        for sid, tit in secoes:
            alvo = _normalizar(tit)
            for i in range(inicio, len(textos)):
                if alvo in textos[i]:
                    paginas[sid] = i + 1
                    inicio = i
                    break
        faltando = [t for s, t in secoes if s not in paginas]
        if faltando:
            raise RuntimeError(f"Secoes nao localizadas no PDF: {faltando}")
        asyncio.run(_pdf(_documento(titulo, capa_html, secoes, corpo, paginas), destino))
    print(f"PDF gerado: {destino} ({len(PdfReader(str(destino)).pages)} paginas)")
    return destino
