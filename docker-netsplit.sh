#!/bin/bash
# Usage:
# PART1="172.17.0.34 172.17.0.36" PART2="172.17.0.37 172.17.0.38 172.17.0.39" ./docker-netsplit.sh REJECT
# PART1="172.17.0.34 172.17.0.36" PART2="172.17.0.37 172.17.0.38 172.17.0.39" ./docker-netsplit.sh ACCEPT

set -x

action=${1:-REJECT}

for p1 in $PART1
do
    for p2 in $PART2
    do
        iptables -I DOCKER -s $p1 -d $p2 -j $action
        iptables -I DOCKER -s $p2 -d $p1 -j $action
    done
done
