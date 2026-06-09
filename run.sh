#!/bin/bash

# Default values for environment variables if not set
export MIDPOINT_USER=${MIDPOINT_USER:-administrator}
export MIDPOINT_PASS=${MIDPOINT_PASS:-IGA4ever}

# Build the docker image if it doesn't exist or to ensure it's up to date
echo "Building Docker image..."
docker build -t midpoint-cli .

# Run the docker container with the passed arguments
echo "Running midpoint-cli with arguments: $@"
docker run --rm \
  -e MIDPOINT_USER="$MIDPOINT_USER" \
  -e MIDPOINT_PASS="$MIDPOINT_PASS" \
  midpoint-cli "$@"
