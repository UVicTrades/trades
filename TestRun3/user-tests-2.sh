#~/bin/bash

echo "Resetting Containers"
docker compose down -v
docker compose up -d

echo "Waiting Until Containers Set Up"
sleep 10

rm -rf ./Config/stockIds.csv
touch ./Config/stockIds.csv
chmod +rwx ./Config/stockIds.csv

rm -rf results-initial
rm -rf results-initial.log

echo "Running InitialSetup.jmx"
jmeter -n -t InitialSetup.jmx -l results-initial.log -e -o ./results-initial

rm -rf results-10000
rm -rf results-10000.log

echo "Running UserThreadTest.jmx"
jmeter -n -t UserThreadTest.jmx -l results-10000.log -e -o ./results-10000 -Jjvm_arg"-Xmx14g -Xms14g -XX:+UseG1GC"
