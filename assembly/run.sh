#!/usr/bin/env bash
export SPRING_PROFILES_ACTIVE=prod
echo "Home is $HOME"
export JAVA_HOME=${HOME}/.sdkman/candidates/java/current
#export JAVA_HOME=${JAVA_HOME:-${HOME}/.sdkman/candidates/java/current}
D=$(dirname $0)
echo "$D is the current directory."
LOG=$HOME/Desktop/bootiful-podcast.log
echo "starting.." >> $LOG
echo `env` >> $LOG
echo "JAVA_HOME=$JAVA_HOME" >> $LOG
echo $0 >> $LOG
echo $PWD >> $LOG
${D}/desktop.jar > $LOG 2>&1
disown
