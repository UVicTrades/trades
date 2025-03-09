#!/bin/bash

echo "Reseting Containers"
docker compose down -v
docker compose up -d

echo "Waiting Until Containers Set Up"
sleep 10

rm -f ./Config/stockIds.csv
touch ./Config/stockIds.csv
chmod +rwx ./Config/stockIds.csv

rm -rf results-initial
rm -rf results-initial.log

echo "Running InitialSetup.jmx"
jmeter -n -t InitialSetup.jmx -l results-initial.log -e -o ./results-initial

rm -rf results-5000
rm -f results-5000.log

echo "Running UserThreadTest.jmx"
jmeter -n -t UserThreadTest.jmx -l results-5000.log -e -o ./results-5000 -Jjvm_args="-Xmx4g -Xms1g -XX:+UseG1GC" 
 


