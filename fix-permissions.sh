#!/bin/bash

# Create target directories if they don't exist
mkdir -p /home/akshaj/wrangler/wrangler-core/target/classes/io/cdap/wrangler/codec

# Ensure the user has write permissions
chmod -R 755 /home/akshaj/wrangler/wrangler-core/target
chmod -R 755 /home/akshaj/wrangler/wrangler-api/target

# Clean the project first to ensure fresh compilation
cd /home/akshaj/wrangler
mvn clean

# Build and install the API module first
# This will make TokenGroup available for the core module
mvn install -pl wrangler-api -am

# Now build the core module
mvn compile -pl wrangler-core
