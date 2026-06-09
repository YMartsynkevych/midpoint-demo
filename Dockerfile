# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN useradd -ms /bin/bash appuser
USER appuser

# Important: keep container interactive-friendly
ENTRYPOINT ["java", "-jar", "app.jar"]