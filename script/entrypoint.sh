#!/bin/bash
set -e

echo "[entrypoint] Fetching ECS container metadata..."

# ECS_CONTAINER_METADATA_URI_V4 을 쓰는게 최신
ECS_INSTANCE_IP_ADDRESS=$(curl -s "$ECS_CONTAINER_METADATA_URI_V4" | jq -r '.Networks[0].IPv4Addresses[0]')

if [ -z "$ECS_INSTANCE_IP_ADDRESS" ]; then
  echo "[entrypoint] Failed to retrieve ECS instance IP address"
  exit 1
fi

echo "[entrypoint] Resolved IP: $ECS_INSTANCE_IP_ADDRESS"
export ECS_INSTANCE_IP_ADDRESS

exec java $JAVA_OPTS -jar /app.jar