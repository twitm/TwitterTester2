#!/bin/bash
# EslasticSearch


## Stop and Remove ES
service elasticsearch stop
yum -y remove elasticsearch
#rpm -e elasticsearch

# Delete folders since yum  remove does not delete the index data
rm -rf /var/lib/elasticsearch
rm -rf /tmp/elasticsearch
rm -rf /usr/share/elasticsearch
rm -rf /etc/elasticsearch
rm -rf /var/log/elasticsearch

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
-A INPUT -p tcp -m tcp --dport 9200 -j ACCEPT
-A INPUT -p tcp -m tcp --dport 9300 -j ACCEPT
-A INPUT -p tcp -m state --state NEW -m tcp --dport 22 -j ACCEPT
-A INPUT -j REJECT --reject-with icmp-host-prohibited
-A FORWARD -j REJECT --reject-with icmp-host-prohibited
COMMIT
EOL

iptables-restore < iptables_rules
iptables-save > /etc/sysconfig/iptables
rm -f iptables_rules


# Install Java
yum -y install java-1.7.0-openjdk.x86_64

# Install ES (Not from yum because for some reason this is insanely slow)
cd ~
wget --quiet --no-clobber http://packages.elasticsearch.org/elasticsearch/1.3/centos/elasticsearch-1.3.2.noarch.rpm

#rpm -Uvh elasticsearch-1.3.2.noarch.rpm
yum -y localinstall elasticsearch-1.3.2.noarch.rpm

# Set sysctl
sysctl -w vm.max_map_count=262144

# Config ES
cat > /etc/elasticsearch/elasticsearch.yml <<EOL
cluster.name: elasticsearchtwitter
index.number_of_shards: 5
index.number_of_replicas: 0
discovery.zen.ping.multicast.enabled: false
discovery.zen.ping.unicast.hosts: ["10.0.101.22"]
EOL

# Start ES
service elasticsearch start