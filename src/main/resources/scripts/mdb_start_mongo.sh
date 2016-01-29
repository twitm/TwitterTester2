#!/bin/bash
# Starting the service does not work as it conflicts with mongod --configserver process
mongod --config /etc/mongod.conf