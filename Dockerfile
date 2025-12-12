FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# JAR 파일 복사
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]