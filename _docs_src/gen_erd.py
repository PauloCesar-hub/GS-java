"""Gera um diagrama relacional (estilo Oracle Data Modeler) a partir do DDL.
Uso: python3 gen_erd.py <ddl.sql> <saida_sem_extensao>
"""
import re
import subprocess
import sys

ddl = open(sys.argv[1], encoding="utf-8").read()
out = sys.argv[2]

tables = {}
order = []
for m in re.finditer(r"CREATE TABLE (\w+) \((.*?)\n\);", ddl, re.S):
    name = m.group(1)
    cols = []
    for line in m.group(2).strip().splitlines():
        line = line.strip().rstrip(",")
        if not line:
            continue
        parts = line.split()
        col = parts[0]
        typ = parts[1]
        notnull = "NOT NULL" in line or "IDENTITY" in line
        cols.append([col, typ, notnull])
    tables[name] = {"cols": cols, "pk": set(), "fk": {}, "uk": set(), "pkname": "", "fks": [], "uks": []}
    order.append(name)

for m in re.finditer(r"ALTER TABLE (\w+) ADD CONSTRAINT (\w+) PRIMARY KEY \((\w+)\)", ddl):
    tables[m.group(1)]["pk"].add(m.group(3))
    tables[m.group(1)]["pkname"] = m.group(2)
for m in re.finditer(r"ALTER TABLE (\w+) ADD CONSTRAINT (\w+)\s+FOREIGN KEY \((\w+)\) REFERENCES (\w+)", ddl):
    tables[m.group(1)]["fk"][m.group(3)] = m.group(4)
    tables[m.group(1)]["fks"].append((m.group(2), m.group(3), m.group(4)))
for m in re.finditer(r"ALTER TABLE (\w+) ADD CONSTRAINT (\w+) UNIQUE \(([\w, ]+)\)", ddl):
    for c in m.group(3).split(","):
        tables[m.group(1)]["uk"].add(c.strip())
    tables[m.group(1)]["uks"].append(m.group(2))

HEAD = "#E8EEF7"
lines = [
    "digraph ERD {",
    'graph [rankdir=LR, splines=spline, nodesep=0.5, ranksep=1.3, fontname="Helvetica", bgcolor="white", pad=0.3];',
    'node [shape=plaintext, fontname="Helvetica", fontsize=10];',
    'edge [color="#3d5a80", penwidth=1.3, arrowsize=0.9];',
]
for t in order:
    info = tables[t]
    rows = [f'<TR><TD COLSPAN="4" BGCOLOR="{HEAD}" ALIGN="LEFT"><B>{t}</B></TD></TR>']
    for col, typ, nn in info["cols"]:
        flag = ""
        if col in info["pk"]:
            flag += "P"
        if col in info["fk"]:
            flag += "F"
        if col in info["uk"] and col not in info["pk"]:
            flag += "U"
        star = "*" if nn else ""
        name = f"<U>{col}</U>" if col in info["pk"] else col
        flag_cell = f'<TD ALIGN="LEFT"><FONT COLOR="#b00020"><B>{flag}</B></FONT></TD>' if flag else "<TD> </TD>"
        star_cell = f'<TD ALIGN="LEFT">{star}</TD>' if star else "<TD> </TD>"
        rows.append(
            "<TR>" + flag_cell + star_cell
            + f'<TD ALIGN="LEFT" PORT="{col}">{name}</TD>'
            + f'<TD ALIGN="LEFT"><FONT COLOR="#555555">{typ}</FONT></TD></TR>'
        )
    rows.append('<TR><TD COLSPAN="4" BGCOLOR="#f6f6f6" ALIGN="LEFT"><FONT POINT-SIZE="8" COLOR="#333333">'
                + f'{info["pkname"]} (PK)'
                + "".join(f"<BR ALIGN=\"LEFT\"/>{n} (FK)" for n, _, _ in info["fks"])
                + "".join(f"<BR ALIGN=\"LEFT\"/>{n} (UK)" for n in info["uks"])
                + '<BR ALIGN="LEFT"/></FONT></TD></TR>')
    label = '<<TABLE BORDER="1" CELLBORDER="0" CELLSPACING="0" CELLPADDING="3" COLOR="#3d5a80">' + "".join(rows) + "</TABLE>>"
    lines.append(f"{t} [label={label}];")

for t in order:
    for col, ref in tables[t]["fk"].items():
        # pai -> filho : 1 (barra dupla) para N (pe de galinha)
        lines.append(f'{ref} -> {t} [dir=both, arrowtail=teetee, arrowhead=crowodot];')
lines.append("}")
open(out + ".dot", "w").write("\n".join(lines))
subprocess.run(["dot", "-Tpng", "-Gdpi=170", out + ".dot", "-o", out + ".png"], check=True)
subprocess.run(["dot", "-Tsvg", out + ".dot", "-o", out + ".svg"], check=True)
print("ok", len(order), "tabelas")
