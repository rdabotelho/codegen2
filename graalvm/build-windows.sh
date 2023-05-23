#!/bin/bash

ARTIFACT=codegen
VERSION=2.1.1-SNAPSHOT

INPUT_DIR=$(pwd)/input
OUTPUT_DIR=$(pwd)/output

cp ../target/$ARTIFACT-$VERSION-jar-with-dependencies.jar $INPUT_DIR/$ARTIFACT.jar
cp reflect-config.json $OUTPUT_DIR
cp resource-config.json $OUTPUT_DIR

docker run --rm -v $INPUT_DIR/$ARTIFACT.jar:/opt/$ARTIFACT.jar -v $OUTPUT_DIR:/opt/output -it mcr.microsoft.com/windows/servercore:ltsc2019 \
bash
