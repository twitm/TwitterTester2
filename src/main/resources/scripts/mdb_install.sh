#!/bin/bash

## Stop and Remove
service mongod stop
pkill -f 'mongos'
pkill -f 'mongod'

yum -y remove mongodb-org

rm -rf /var/log/mongodb/
rm -rf /var/lib/mongo/
rm -rf /data/configdb/

## Configure Iptables
cd ~
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
-A INPUT -p tcp -m tcp --dport 27017 -j ACCEPT
-A INPUT -p tcp -m tcp --dport 27018 -j ACCEPT
-A INPUT -p tcp -m tcp --dport 27019 -j ACCEPT
-A INPUT -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
-A INPUT -j REJECT --reject-with icmp-host-prohibited
-A FORWARD -j REJECT --reject-with icmp-host-prohibited
COMMIT
EOL

iptables-restore < iptables_rules
iptables-save > /etc/sysconfig/iptables
rm -f iptables_rules

# Make dirs
mkdir -p /var/lib/mongo
mkdir -p /var/log/mongodb/

# Add yum repo
cat > /etc/yum.repos.d/mongodb.repo <<EOL
[mongodb]
name=MongoDB Repository
baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64/
gpgcheck=0
enabled=1
EOL

# Install Java
yum -y install java-1.7.0-openjdk.x86_64

# Install MongoDB
yum -y install mongodb-org

# Write service config
service mongod stop

cat > /etc/mongod.conf <<EOL

# mongod.conf

#where to log
logpath=/var/log/mongodb/mongod.log

logappend=true

# fork and run in background
fork=true

port=27018
shardsvr = true

dbpath=/var/lib/mongo

# location of pidfile
pidfilepath=/var/run/mongodb/mongod.pid

# Listen to local interface only. Comment out to listen on all interfaces.
#bind_ip=127.0.0.1
EOL

service mongod start