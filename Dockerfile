FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y curl jq

# entrypoint 복사
COPY script/entrypoint.sh /script/entrypoint.sh
RUN chmod +x /script/entrypoint.sh

# JAR 복사
COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["/script/entrypoint.sh"]