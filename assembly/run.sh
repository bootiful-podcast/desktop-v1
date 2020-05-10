#!/usr/bin/env bash


do_run(){
    export JAVA_HOME=${HOME}/.sdkman/candidates/java/current
    DIRECTORY_OF_APP=$(dirname $0)
    LOG=$HOME/Desktop/bootiful-podcast.log
    env
    echo "the current directory is ${DIRECTORY_OF_APP}."
    echo "starting.."
    echo $0
    export SPRING_PROFILES_ACTIVE="$(cat bp_mode)"
    ${DIRECTORY_OF_APP}/desktop.jar
}
do_run > $LOG 2>&1
disown
