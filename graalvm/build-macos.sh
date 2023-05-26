#!/bin/bash

# ATENTION
# Before everything, execute the follow command to generate a reflect-config.json file
# java -agentlib:native-image-agent=config-output-dir=graalvm -jar target/codegen-2.1.2-SNAPSHOT-jar-with-dependencies.jar

ARTIFACT=codegen
VERSION=2.1.2-SNAPSHOT

INPUT_DIR=$(pwd)/input
OUTPUT_DIR=$(pwd)/output

cp ../target/$ARTIFACT-$VERSION-jar-with-dependencies.jar $INPUT_DIR/$ARTIFACT.jar

cd $OUTPUT_DIR
native-image --enable-http --enable-https --no-server --no-fallback -H:ReflectionConfigurationFiles=../reflect-config.json -H:ResourceConfigurationFiles=../resource-config.json -jar $INPUT_DIR/$ARTIFACT.jar

mv $ARTIFACT $ARTIFACT-macos
rm $ARTIFACT.build_artifacts.txt
