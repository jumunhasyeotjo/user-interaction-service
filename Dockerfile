FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre-jammy
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]