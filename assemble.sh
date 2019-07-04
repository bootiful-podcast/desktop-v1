#!/usr/bin/env bash

export BP_DESKTOP=$HOME/Desktop/bootiful-podcast/
mkdir -p $BP_DESKTOP
mvn -DskipTests=true spring-javaformat:apply clean package
cp target/desktop.jar $BP_DESKTOP
cp run.sh $BP_DESKTOP