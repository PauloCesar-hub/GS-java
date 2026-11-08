# RotaSeca API — Domain Driven Design (Java)

API RESTful em **Spring Boot 3.5 / Java 21** com banco **Oracle (FIAP)**. Recebe leituras de sensores e relatos de moradores, emite alertas de alagamento por ponto e notifica os moradores do bairro.

Documentação completa: `RotaSeca_DDD_Java.pdf` (especificação, diagrama de classes, telas, endpoints, DER e instruções).

## Rodar localmente (sem configurar nada)

```bash
cd java-api
mvn spring-boot:run          # ou rodar RotaSecaApplication no IntelliJ
```
- Página web: http://localhost:8080
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui.html
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:rotaseca`, usuário `sa`)

O perfil `dev` usa H2 em memória, no modo Oracle, carregado com os mesmos scripts do Database Design.

## Usar o Oracle da FIAP

1. Rodar `database/01_DDL_rotaseca.sql` e `database/02_DML_rotaseca.sql` no SQL Developer.
2. Definir as variáveis de ambiente:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_USER=rm00000
   DB_PASSWORD=******
   # opcional: DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
   ```
3. Rodar a aplicação.

## Deploy no Render

New → Web Service → repositório → **Root Directory** `java-api` → **Runtime** Docker → plano Free → variáveis acima → Deploy. Teste em `https://SEU-SERVICO.onrender.com/api` (deve responder `"status": "UP"`). Também é possível usar o `render.yaml` da raiz como Blueprint.

## Testes

```bash
mvn test
```

## Camadas

`model` (entidades e enums) → `dao` (JDBC com PreparedStatement) → `bo` (validações e regras) → `controller` (REST) + `exception` (GlobalExceptionHandler com respostas 400/404/409/422/500).
