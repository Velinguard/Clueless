#!/usr/bin/env bash
cd /tmp
sudo kill -9 `sudo lsof -t -i:8080`

#sudo java -jar ../target/api-0.0.1-SNAPSHOT.jar
sudo java -jar ../target/api-0.0.1-SNAPSHOT.jar --fs=local --email=false > log/log-`date +%F_%R`.txt 2>&1 &
