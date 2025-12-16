FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && apt-get install -y curl jq

# entrypoint 복사
COPY script/entrypoint.sh /script/entrypoint.sh
RUN chmod +x /script/entrypoint.sh

# JAR 복사
COPY build/libs/*.jar /app.jar

ENTRYPOINT ["/script/entrypoint.sh"]