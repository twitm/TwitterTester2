#!/bin/bash
#nohup mongos --port 27016 --configdb \
#10.0.101.64:27019,\
#10.0.101.65:27019,\
#10.0.101.66:27019 \
#> /var/log/mongodb/mongos.log 2>&1 &

# IPs of first 3 servers

mongos --port 27017 --fork --logpath=/var/log/mongodb/mongos.log --logappend \
--configdb \
10.0.101.11:27019,\
10.0.101.12:27019,\
10.0.101.13:27019