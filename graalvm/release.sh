#!/bin/bash

INFO="Installation\n\n
1. After unzipping the release file, the codegen folder was created\n
2. Copy the codegen folder to home directory ~/\n
3. Add the codegen folder to the PATH environment variable\n
4. Run the following command: codegen -v\n\n
If everything has been done correctly, you will see the following output:\n\n
Codegen command line interface (CLI)\n
Version: 2.1.5\n"

# Linux
cd output
mkdir codegen
cp codegen-linux codegen/codegen
echo $INFO | tee codegen/installation.txt > /dev/null
zip -r codegen-linux.zip codegen
rm -Rf codegen

# Macos
cd output
mkdir codegen
cp codegen-macos codegen/codegen
echo $INFO | tee codegen/installation.txt > /dev/null
zip -r codegen-macos.zip codegen
rm -Rf codegens

# Window
cd output
mkdir codegen
cp ../../target/codegen/* codegen
zip -r codegen-windows.zip codegen
rm -Rf codegen

# Finish
rm -Rf codegen
