"""Gera diagramas de classes PlantUML a partir do codigo Java (para refletir o codigo real)."""
import re
import subprocess
from pathlib import Path

RAIZ = Path(__file__).resolve().parent
BASE = RAIZ.parent / "java-api" / "src" / "main" / "java" / "br" / "com" / "fiap" / "rotaseca"
OUT = RAIZ / "img"
OUT.mkdir(exist_ok=True)


def ler(arquivo):
    return arquivo.read_text(encoding="utf-8")


def campos(src):
    return re.findall(r"^\s{4}private (?:final )?(?!static)([\w<>, ]+) (\w+);", src, re.M)


def metodos_publicos(src, nome_classe):
    res = []
    for m in re.finditer(r"^\s{4}public (?!class|enum|record|abstract class)(?:static )?([\w<>, ?]+) (\w+)\((.*?)\)\s*(?:throws [\w, ]+)?\{", src, re.M | re.S):
        ret, nome, params = m.groups()
        if nome.startswith(("get", "set")) or nome == nome_classe:
            continue
        params = re.sub(r"@\w+(\([^)]*\))?\s*", "", params)   # remove anotacoes dos parametros
        ps = ", ".join(p.strip().split()[-1] for p in params.split(",") if p.strip()) if params.strip() else ""
        res.append(f"+{nome}({ps}) : {ret.strip()}")
    return res


def modelo():
    linhas = ["@startuml", "skinparam classAttributeIconSize 0", "skinparam shadowing false",
              "skinparam classBackgroundColor #FFFFFF", "skinparam classBorderColor #0b3c5d", "skinparam classHeaderBackgroundColor #e6f0f7",
              "skinparam defaultFontName Helvetica", "skinparam linetype ortho", "left to right direction",
              'title Camada model (br.com.fiap.rotaseca.model)']
    for f in sorted((BASE / "model").glob("*.java")):
        src = ler(f)
        nome = f.stem
        if "public enum" in src:
            valores = re.search(r"enum \w+ \{\s*([^;}]*)", src).group(1)
            itens = [v.strip() for v in valores.replace("\n", " ").split(",") if v.strip()]
            linhas.append(f"enum {nome} {{\n" + "\n".join(itens) + "\n}")
            continue
        corpo = [f"-{n} : {t}" for t, n in campos(src)]
        corpo += [m for m in metodos_publicos(src, nome) if not m.startswith("+is") or True]
        linhas.append(f"class {nome} {{\n" + "\n".join(corpo) + "\n}")
    rel = [
        ("Bairro", "PontoRisco", "1", "0..*"), ("Bairro", "Abrigo", "1", "0..*"), ("Bairro", "Cidadao", "1", "0..*"),
        ("PontoRisco", "Sensor", "1", "0..*"), ("Sensor", "Leitura", "1", "0..*"),
        ("PontoRisco", "Ocorrencia", "1", "0..*"), ("Cidadao", "Ocorrencia", "1", "0..*"),
        ("PontoRisco", "Alerta", "1", "0..*"), ("Alerta", "Notificacao", "1", "0..*"),
        ("Cidadao", "Notificacao", "1", "0..*"),
    ]
    for a, b, c1, c2 in rel:
        linhas.append(f'{a} "{c1}" -- "{c2}" {b}')
    usos = [("Bairro", "Zona"), ("Sensor", "TipoSensor"), ("Sensor", "StatusSensor"), ("Cidadao", "PerfilCidadao"),
            ("Ocorrencia", "StatusOcorrencia"), ("Alerta", "NivelAlerta"), ("Notificacao", "CanalNotificacao"),
            ("PontoRisco", "NivelAlerta")]
    for a, b in usos:
        linhas.append(f"{a} ..> {b}")
    linhas.append("@enduml")
    return "\n".join(linhas)


def camadas():
    linhas = ["@startuml", "skinparam classAttributeIconSize 0", "skinparam shadowing false",
              "skinparam classBackgroundColor #FFFFFF", "skinparam classBorderColor #0b3c5d", "skinparam classHeaderBackgroundColor #e6f0f7",
              "skinparam defaultFontName Helvetica", "skinparam packageStyle rectangle",
              "left to right direction", "title Camadas controller -> BO -> DAO"]
    for pacote, sufixo in (("controller", "Controller"), ("bo", "BO"), ("dao", "Dao")):
        linhas.append(f'package "{pacote}" {{')
        for f in sorted((BASE / pacote).glob("*.java")):
            src = ler(f)
            nome = f.stem
            kw = "abstract class" if "abstract class" in src else "class"
            ms = metodos_publicos(src, nome)
            if nome == "BaseDao":
                ms = ["#listar(sql, parametros, mapeador) : List<T>", "#buscarUm(sql, parametros, mapeador) : Optional<T>",
                      "#inserir(sql, colunaId, parametros) : long", "#executar(sql, parametros) : int",
                      "#traduzir(SQLException) : RuntimeException"]
                linhas.append(f"{kw} {nome} {{\n-dataSource : DataSource\n" + "\n".join(ms) + "\n}")
                continue
            linhas.append(f"{kw} {nome} {{\n" + "\n".join(ms) + "\n}")
        linhas.append("}")
    for f in sorted((BASE / "dao").glob("*Dao.java")):
        if f.stem != "BaseDao":
            linhas.append(f"BaseDao <|-- {f.stem}")
    # dependencias reais: controller -> BO, BO -> DAO/BO (lidas do construtor)
    for pacote in ("controller", "bo"):
        for f in sorted((BASE / pacote).glob("*.java")):
            src = ler(f)
            cons = re.search(rf"public {f.stem}\(([^)]*)\)", src)
            if not cons:
                continue
            for tipo in re.findall(r"(\w+(?:BO|Dao)) \w+", cons.group(1)):
                linhas.append(f"{f.stem} --> {tipo}")
    linhas.append("@enduml")
    return "\n".join(linhas)


def excecoes():
    linhas = ["@startuml", "skinparam classAttributeIconSize 0", "skinparam shadowing false",
              "skinparam classBackgroundColor #FFFFFF", "skinparam classBorderColor #0b3c5d",
              "skinparam classHeaderBackgroundColor #e6f0f7", "skinparam defaultFontName Helvetica",
              "title Camada de tratamento de excecoes (br.com.fiap.rotaseca.exception)"]
    linhas.append('package "exception" {')
    for f in sorted((BASE / "exception").glob("*.java")):
        src = ler(f)
        nome = f.stem
        if "record" in src:
            linhas.append(f"class {nome} <<record>> {{\ntimestamp, status, erro, mensagem, caminho, detalhes\n}}")
        elif nome == "GlobalExceptionHandler":
            ms = [f"+{m}(e, req) : ResponseEntity<ErroResponse>" for m in re.findall(r"public ResponseEntity<ErroResponse> (\w+)\(", src)]
            linhas.append(f"class {nome} <<RestControllerAdvice>> {{\n" + "\n".join(ms) + "\n}")
        else:
            kw = "abstract class" if "abstract" in src else "class"
            linhas.append(f"{kw} {nome}")
    linhas.append("}")
    for e in ("RecursoNaoEncontradoException", "DadosInvalidosException", "RegraNegocioException",
              "ConflitoException", "PersistenciaException"):
        linhas.append(f"RotaSecaException <|-- {e}")
    linhas.append("RuntimeException <|-- RotaSecaException")
    linhas.append("GlobalExceptionHandler ..> ErroResponse")
    linhas.append("@enduml")
    return "\n".join(linhas)


for nome, conteudo in (("classes_model", modelo()), ("classes_camadas", camadas()), ("classes_excecoes", excecoes())):
    (OUT / f"{nome}.puml").write_text(conteudo, encoding="utf-8")
    r = subprocess.run(["plantuml", "-DPLANTUML_LIMIT_SIZE=12000", "-tpng", "-Sdpi=150", str(OUT / f"{nome}.puml")], capture_output=True, text=True)
    print(nome, r.returncode, r.stderr[-300:])
