# RotaSeca — Database Design

| Arquivo | Conteúdo |
|---|---|
| `RotaSeca_DatabaseDesign.pdf` | Capa, sumário, objetivo e escopo, funcionalidades e diagrama do modelo lógico (itens 1 e 2) |
| `01_DDL_rotaseca.sql` | CREATE/ALTER TABLE das 9 tabelas, com todas as constraints (item 3) |
| `02_DML_rotaseca.sql` | INSERTs com no mínimo 5 linhas por tabela (item 4) |
| `03_DQL_rotaseca.sql` | 3 consultas: INNER JOIN; INNER JOIN + GROUP BY; LEFT OUTER JOIN + WHERE + ORDER BY (item 5) |
| `diagrama_modelo_logico.png` | Imagem do modelo lógico |

Ordem de execução no Oracle SQL Developer: **01_DDL → 02_DML → 03_DQL** (F5 = executar script).

## Diagrama no Oracle Data Modeler

O enunciado pede o diagrama **feito no Oracle Data Modeler**. Para gerar o oficial a partir do DDL:

1. SQL Developer → *File → Data Modeler → Import → DDL File* → escolher `01_DDL_rotaseca.sql` (banco Oracle Database 19c).
2. Organizar as tabelas (*Layout → Auto Layout* ajuda) e conferir as 8 FKs e as cardinalidades.
3. *File → Data Modeler → Print Diagram → To Image File* e substituir a figura no PDF (arquivo `_docs_src/img/modelo_logico.png`, depois rodar `python _docs_src/doc_database.py`) ou colar a imagem no documento.
