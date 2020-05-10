#!/usr/bin/env bash
#export SPRING_PROFILES_ACTIVE=production
export JAVA_HOME=${HOME}/.sdkman/candidates/java/current
D=$(dirname $0)
LOG=$HOME/Desktop/bootiful-podcast.log

do_run(){
    env
    echo "the current directory is ${D}."
    echo "starting.."
    echo $0
    SPRING_PROFILES_ACTIVE=production ${D}/desktop.jar
}
do_run > $LOG 2>&1
disown
