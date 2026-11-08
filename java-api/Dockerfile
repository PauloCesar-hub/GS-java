# ---------- etapa 1: build com Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---------- etapa 2: imagem final so com o JRE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/rotaseca-api.jar app.jar
ENV TZ=America/Sao_Paulo
# Limite de memoria adequado ao plano gratuito do Render (512 MB)
ENV JAVA_OPTS="-Xms128m -Xmx350m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
