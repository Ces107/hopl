#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

RELEASE_PATH="/mnt/c/Users/cpereiro/Documents/hoplRelease.jar"

echo "================================================="
echo "  HOPL - Website Compliance Scanner"
echo "  Building release JAR..."
echo "================================================="
echo ""

# Step 1: Build frontend
echo "[1/4] Building React frontend..."
cd frontend
npm ci --silent 2>/dev/null || npm install --silent
npm run build
echo "  Frontend built successfully."
cd ..

# Step 2: Copy frontend to Spring Boot static resources
echo "[2/4] Copying frontend assets..."
rm -rf src/main/resources/static
mkdir -p src/main/resources/static
cp -r frontend/dist/* src/main/resources/static/
echo "  Assets copied."

# Step 3: Build Spring Boot JAR
echo "[3/4] Building Spring Boot JAR..."
./mvnw clean package -DskipTests -q
echo "  JAR built successfully."

# Step 4: Copy to release location
echo "[4/4] Copying to release location..."
mkdir -p "$(dirname "$RELEASE_PATH")"
cp target/hopl-1.0.0.jar "$RELEASE_PATH"

echo ""
echo "================================================="
echo "  BUILD COMPLETE!"
echo "  JAR: $RELEASE_PATH"
echo ""
echo "  To run:"
echo "    java -jar \"C:\\Users\\cpereiro\\Documents\\hoplRelease.jar\""
echo ""
echo "  Then open: http://localhost:8080"
echo "================================================="
