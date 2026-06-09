#!/usr/bin/env bash

set -euo pipefail

IMAGE_NAME="midpoint-cli"
CONTAINER_NAME="midpoint-cli-container"

COMMAND="${1:-start}"
shift || true

build_image() {
  echo "Building Docker image..."
  docker build -t "$IMAGE_NAME" .
}

start_container() {
  echo "Starting container..."

  docker rm -f "$CONTAINER_NAME" >/dev/null 2>&1 || true

  docker run -it \
    --name "$CONTAINER_NAME" \
    "$IMAGE_NAME" "$@"
}

stop_container() {
  echo "Stopping container..."
  docker stop "$CONTAINER_NAME" >/dev/null 2>&1 || echo "Container not running"
}

delete_container() {
  echo "Deleting container..."
  docker rm -f "$CONTAINER_NAME" >/dev/null 2>&1 || echo "Container not found"
}

case "$COMMAND" in
  start)
    build_image
    start_container "$@"
    ;;
  stop)
    stop_container
    ;;
  delete)
    delete_container
    ;;
  restart)
    stop_container
    start_container "$@"
    ;;
  *)
    echo "Usage: $0 {start|stop|delete|restart}"
    exit 1
    ;;
esac