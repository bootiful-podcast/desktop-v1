#!/bin/bash 

TAG_NAME=${1:-$(date +%s)}
echo "creating tag ${TAG_NAME}... "
git tag $TAG_NAME
git push origin $TAG_NAME 