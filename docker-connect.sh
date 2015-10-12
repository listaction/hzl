#!/bin/bash
host=${1:-172.17.0.4}
iptables -I DOCKER -s $host -j ACCEPT
iptables -I DOCKER -d $host -j ACCEPT
