#!/usr/bin/env bash
mvn spring-javaformat:apply && git commit -am "polish $(date)" && git push