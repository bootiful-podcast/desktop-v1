#!/usr/bin/env bash
export SPRING_PROFILES_ACTIVE=prod
`dirname $0`/desktop.jar &
disown
