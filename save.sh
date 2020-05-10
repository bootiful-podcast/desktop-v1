#!/usr/bin/env bash
mvn spring-javaformat:apply && git commit -am "development $(date)" && git push