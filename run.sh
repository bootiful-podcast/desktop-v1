#!/usr/bin/env bash
export SPRING_PROFILES_ACTIVE=prod
export JAVA_HOME=${HOME}/.sdkman/candidates/java/current
LOG=$HOME/Desktop/bootiful-podcast.log
echo "starting.." > $LOG
echo $0 >> $LOG
echo $PWD >> $LOG
`dirname $0`/desktop.jar > $LOG 2>&1
disown
