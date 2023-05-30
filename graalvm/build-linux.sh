#!/bin/bash

ARTIFACT=codegen
VERSION=2.1.3

INPUT_DIR=$(pwd)/input
OUTPUT_DIR=$(pwd)/output

cp ../target/$ARTIFACT-$VERSION-jar-with-dependencies.jar $INPUT_DIR/$ARTIFACT.jar
cp reflect-config.json $OUTPUT_DIR
cp resource-config.json $OUTPUT_DIR

docker run --rm -v $INPUT_DIR/$ARTIFACT.jar:/opt/$ARTIFACT.jar -v $OUTPUT_DIR:/opt/output -it ghcr.io/graalvm/graalvm-ce:21.2.0 \
bash -c "gu install native-image && native-image --enable-http --enable-https --no-server --no-fallback -H:ReflectionConfigurationFiles=/opt/output/reflect-config.json \
-H:ResourceConfigurationFiles=/opt/output/resource-config.json -H:+AllowIncompleteClasspath -jar /opt/$ARTIFACT.jar && mv $ARTIFACT /opt/output/$ARTIFACT-linux"

rm $OUTPUT_DIR/reflect-config.json
rm $OUTPUT_DIR/resource-config.json