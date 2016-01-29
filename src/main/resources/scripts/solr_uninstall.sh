#!/bin/bash

# Stop Running Solr instances
pkill -f 'start.jar'

# Stop possible Zookeeper instance (can throw error if not installed but doesnt matter)
/etc/zookeeper/zookeeper-3.4.6/bin/zkServer.sh stop


# Remove dirs
rm -rf /etc/zookeeper
rm -rf /var/zookeeper
rm -rf /etc/solr