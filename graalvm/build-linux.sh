#!/bin/bash

ARTIFACT=codegen
VERSION=2.1.0-SNAPSHOT

INPUT_DIR=$(pwd)/input
OUTPUT_DIR=$(pwd)/output

cp ../target/$ARTIFACT-$VERSION-jar-with-dependencies.jar $INPUT_DIR/$ARTIFACT.jar

docker run --rm -v $INPUT_DIR/$ARTIFACT.jar:/opt/$ARTIFACT.jar -v $OUTPUT_DIR:/opt/output -it ghcr.io/graalvm/graalvm-ce:21.2.0 \
bash -c "gu install native-image && native-image --no-server --no-fallback -jar /opt/$ARTIFACT.jar && mv $ARTIFACT /opt/output/$ARTIFACT-linux"
