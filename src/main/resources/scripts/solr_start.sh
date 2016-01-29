#!/bin/bash

# Stop Running Solr instances
pkill -f 'start.jar'


rm -f  /etc/solr/solr-4.8.1/example/solr/collection1/data/index/write.lock

# Go to directory (prevents config not found error)
cd /etc/solr/solr-4.8.1/example/

# Start Solr in background
nohup java -DnumShards=5 -Dbootstrap_confdir=./solr/collection1/conf -Dcollection.configName=myconf -DzkHost=localhost:2181 -jar start.jar > ~/solr.log 2>&1 &