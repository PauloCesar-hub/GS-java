"""Gera PDFs de leitura do PASSO_A_PASSO.md e do ROTEIRO_VIDEO.md. Requer: pip install markdown-it-py"""
from markdown_it import MarkdownIt

from docgen import RAIZ, gerar

EXTRA = "<style>h1{font-size:18pt} h2{break-before:auto;margin-top:7mm} input{margin-right:2mm}</style>"
for nome, saida in (("PASSO_A_PASSO.md", "PASSO_A_PASSO.pdf"), ("ROTEIRO_VIDEO.md", "ROTEIRO_VIDEO.pdf")):
    md = (RAIZ.parent / nome).read_text(encoding="utf-8").replace("- [ ]", "- &#9744;")
    corpo = EXTRA + MarkdownIt("gfm-like", {"linkify": False}).render(md).replace("<table>", '<table class="t">')
    gerar(nome, "", [], corpo, RAIZ.parent / saida)
