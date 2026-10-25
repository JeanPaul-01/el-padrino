# ---------- Etapa 1: compilación ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Descarga las dependencias primero para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B -DskipTests package && cp target/*.war app.war

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuario sin privilegios
RUN useradd --system --create-home elpadrino
USER elpadrino

COPY --from=build /app/app.war app.war

ENV SPRING_PROFILES_ACTIVE=prod \
    TZ=America/Lima \
    PORT=8080 \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1"

EXPOSE 8080

# El war es ejecutable: Tomcat embebido sirve también las JSP
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.war"]
