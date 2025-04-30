#!/bin/bash

#
# Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
# All rights reserved.
#
#

# Usage: ./build-jpackage.sh <jar-file> <java-home>

JAR_FILE="$1"
JAVA_HOME="$2"

echo "Running jpackage for $(uname)..."

# Customize these as needed
APP_NAME="SyncSyndicate"
MAIN_CLASS="com.igearfs.jnlp.Main"
VERSION="1.7.1"
DESCRIPTION="SyncSyndicate: Unified JNLP Connectivity"
VENDOR="In-Game Event, A Red Flag Syndicate LLC"
LICENSE_FILE="LICENSE.txt"
INSTALL_DIR="SyncSyndicate"
APP_CONTENT="4.5.2,javafx-sdk-17.0.14"

# Pick type based on OS
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    PKG_TYPE="deb"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    PKG_TYPE="pkg"
else
    echo "Unsupported OS: $OSTYPE"
    exit 1
fi

"$JAVA_HOME/bin/jpackage" \
  --input jarfile \
  --name "$APP_NAME" \
  --main-jar "$JAR_FILE" \
  --main-class "$MAIN_CLASS" \
  --type "$PKG_TYPE" \
  --runtime-image jre \
  --vendor "$VENDOR" \
  --app-version "$VERSION" \
  --description "$DESCRIPTION" \
  --license-file "$LICENSE_FILE" \
  --install-dir "$INSTALL_DIR" \
  --verbose \
  --app-content "$APP_CONTENT"
