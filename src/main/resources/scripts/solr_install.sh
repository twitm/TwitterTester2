#!/bin/bash
# Solr + zookeeper
echo "Solr + Zookeeper Install Script"
if [ -z "$1" ]; then
    echo "ERROR: Zookeeper ID missing"
	exit 0;
fi

#read zooID
zooID=$1

echo "Zookeeper ID: $zooID"

# Stop Running Solr instances
pkill -f 'start.jar'

# Stop possible Zookeeper instance (can throw error if not installed but can be ignored)
/etc/zookeeper/zookeeper-3.4.6/bin/zkServer.sh stop

cd ~

## Configure Iptables
cat > ~/iptables_rules <<EOL
*filter
:INPUT ACCEPT [0:0]
:FORWARD ACCEPT [0:0]
:OUTPUT ACCEPT [4:560]
-A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT
-A INPUT -p icmp -j ACCEPT
-A INPUT -i lo -j ACCEPT
-A INPUT -p tcp -m tcp --dport 80 -j ACCEPT
-A INPUT -p tcp -m tcp --dport 443 -j ACCEPT
-A INPUT -p tcp -m tcp --dport 8983 -j ACCEPT
-A INPUT -p tcp -m tcp --dport 2888 -j ACCEPT
-A INPUT -p tcp -m tcp --dport 3888 -j ACCEPT
-A INPUT -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
-A INPUT -j REJECT --reject-with icmp-host-prohibited
-A FORWARD -j REJECT --reject-with icmp-host-prohibited
COMMIT
EOL

iptables-restore < iptables_rules
iptables-save > /etc/sysconfig/iptables
rm -f iptables_rules

## Install Java
yum -y install java-1.7.0-openjdk.x86_64

## Install Zookeeper
rm -rf /etc/zookeeper
mkdir /etc/zookeeper

wget --quiet --no-clobber apache.xl-mirror.nl/zookeeper/stable/zookeeper-3.4.6.tar.gz
tar zxf zookeeper-3.4.6.tar.gz -C /etc/zookeeper
#rm -f ~/zookeeper-3.4.6.tar.gz

# Write Config (IPs need to be configured!)
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

# Remove any old data
rm -rf /var/zookeeper
mkdir /var/zookeeper


# Write ZooID
echo $zooID > /var/zookeeper/myid

# Start Zookeeper
/etc/zookeeper/zookeeper-3.4.6/bin/zkServer.sh start zoo.cfg


## Install Solr
rm -rf /etc/solr
mkdir /etc/solr

wget --quiet --no-clobber http://apache.proserve.nl/lucene/solr/4.8.1/solr-4.8.1.tgz
tar zxf solr-4.8.1.tgz -C /etc/solr
# rm -f ~/solr-4.8.0.tgz


