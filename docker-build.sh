#!/bin/bash

mvn clean package

if [ ! -z "$1" ]; then
    tag=":$1"
fi
set -x
docker build -t hzl$tag -f docker/Dockerfile .


