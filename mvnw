#!/bin/sh
BASEDIR="$(dirname "$0")"
java -jar "$BASEDIR/.mvn/wrapper/maven-wrapper.jar" "$@"
