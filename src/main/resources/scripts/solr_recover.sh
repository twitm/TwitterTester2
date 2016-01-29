#!/bin/bash
# Write Config (IPs need to be configured!)

# Solr Recover from a crashed node
echo "Solr + Zookeeper Install Script"
if [ -z "$1" ]; then
    echo "ERROR: Zookeeper ID missing"
	exit 0;
fi

#read zooID
zooID=$1

echo "Zookeeper ID: $zooID"

# Stop possible Zookeeper instance (can throw error if not installed but doesnt matter)
/etc/zookeeper/zookeeper-3.4.6/bin/zkServer.sh stop

# Stop Running Solr instances
pkill -f 'start.jar'


cat > /etc/zookeeper/zookeeper-3.4.6/conf/zoo.cfg <<EOL
tickTime=2000
dataDir=/var/zookeeper
clientPort=2181
initLimit=120
syncLimit=10
server.1=10.0.101.13:2888:3888
server.2=10.0.101.14:2888:3888
server.3=10.0.101.21:2888:3888
server.4=10.0.101.23:2888:3888
server.5=10.0.101.24:2888:3888
EOL

# Write ZooID
echo $zooID > /var/zookeeper/myid

# Start Zookeeper
/etc/zookeeper/zookeeper-3.4.6/bin/zkServer.sh start zoo.cfg