#!/usr/bin/env bash

LOG=$HOME/Desktop/bootiful-podcast.log
export JAVA_HOME=${JAVA_HOME:-${HOME}/.sdkman/candidates/java/current}
DIRECTORY_OF_APP=$(dirname $0)
echo "the current directory is ${DIRECTORY_OF_APP}."
echo "starting.."
echo $0
export SPRING_PROFILES_ACTIVE="$(cat bp_mode)"
${DIRECTORY_OF_APP}/desktop.jar
disown
