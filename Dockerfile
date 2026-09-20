# ==========================================
# WORKOUT TRACKER API - BUILD
# ==========================================

FROM maven:3.9.16-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


# ==========================================
# WORKOUT TRACKER API - RUNTIME
# ==========================================

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/workout-tracker-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]