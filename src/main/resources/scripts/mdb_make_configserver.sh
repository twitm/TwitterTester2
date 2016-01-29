#!/bin/bash
# Takes 30secs or so
# Stop service in case it was started by the packager as startup script
service mongod stop
mkdir -p /data/configdb
# nohup mongod --configsvr --dbpath /data/configdb --port 27019 > /var/log/mongodb/mongod_configsvr.log 2>&1 &
# We can use fork instead of sending to background
mongod --configsvr --fork --dbpath /data/configdb --port 27019 --logpath=/var/log/mongodb/mongod_configsvr.log --logappend