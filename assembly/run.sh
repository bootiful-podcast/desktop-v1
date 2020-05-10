#!/usr/bin/env bash

LOG=$HOME/Desktop/bootiful-podcast.log

do_run(){

    export JAVA_HOME=${JAVA_HOME:-${HOME}/.sdkman/candidates/java/current}
    DIRECTORY_OF_APP=$(cd $(dirname $0) && pwd )
    echo "the current directory is ${DIRECTORY_OF_APP}."
    echo "starting.."
    echo $0
    export SPRING_PROFILES_ACTIVE="$(cat $DIRECTORY_OF_APP/bp_mode)"
    echo "the BP_MODE for this build is $SPRING_PROFILES_ACTIVE "
    ${DIRECTORY_OF_APP}/desktop.jar
    disown

}

do_run > $LOG 2>&1