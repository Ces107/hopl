# Stage 1: Build frontend
FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci --silent || npm install --silent
COPY frontend/ ./
RUN npm run build

# Stage 2: Build backend
FROM maven:3.9-eclipse-temurin-17-alpine AS backend-build
WORKDIR /app
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q
COPY src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN ./mvnw clean package -DskipTests -q

# Stage 3: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S hopl && adduser -S hopl -G hopl
COPY --from=backend-build /app/target/hopl-1.0.0.jar app.jar
RUN chown hopl:hopl app.jar

USER hopl

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/documents/types || exit 1

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
