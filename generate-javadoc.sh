#!/bin/bash

# JavaDoc Generation Script for Activerse
# This script generates JavaDoc HTML documentation for ActiverseEngine and ActiverseUtils packages

echo "Generating JavaDoc documentation for Activerse..."
echo ""

# Check if javadoc command exists
if ! command -v javadoc &> /dev/null; then
    echo "Error: javadoc command not found."
    echo "Please ensure you have the JDK installed (not just JRE)."
    exit 1
fi

# Get the project root directory (where this script is located)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Create docs directory if it doesn't exist
mkdir -p docs

# Generate JavaDoc
javadoc \
    -d docs \
    -sourcepath . \
    -subpackages ActiverseEngine:ActiverseUtils \
    -author \
    -version \
    -windowtitle "Activerse API Documentation" \
    -doctitle "Activerse API Documentation" \
    -header "Activerse API" \
    -footer "Activerse © 2025 by Knivier / Agniva" \
    -bottom "Licensed under CC BY-NC-SA 4.0" \
    -charset UTF-8 \
    -encoding UTF-8 \
    -docencoding UTF-8

# Check if generation was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✓ JavaDoc generation completed successfully!"
    echo "  Documentation is available at: docs/index.html"
    echo ""
    echo "To view the documentation, open docs/index.html in your web browser."
else
    echo ""
    echo "✗ JavaDoc generation failed. Please check the error messages above."
    exit 1
fi
