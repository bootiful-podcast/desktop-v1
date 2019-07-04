#!/usr/bin/env bash
export SPRING_PROFILES_ACTIVE=prod
LOG=$HOME/Desktop/bootiful-podcast.log
echo "starting.." > $LOG
echo $0 >> $LOG
echo $PWD >> $LOG
`dirname $0`/desktop.jar > $LOG 2>&1
disown
