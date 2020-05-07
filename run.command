#!/bin/bash

cd "$(dirname "$0")"
./custom.jdk/bin/java -jar "$(pwd)/compiler.jar"
