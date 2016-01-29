package nl.tamasja.deploy;

import nl.tamasja.tools.RunRemoteScript;
import nl.tamasja.tools.RunShellCommand;
import nl.tamasja.tools.log.ILog;

import java.io.IOException;
import java.util.Arrays;

/**
 * TIS 10-9-2014.10:09
 */
public class DeployMongoDB implements IDeploySearchProvider {

    protected String[] servers;

    public DeployMongoDB(String[] servers) {
        this.servers = servers;
    }


    @Override
    public void deploy(ILog log) throws IOException, InterruptedException {


        // Fetch mongo packages for all servers
        for (String hostname : this.servers) {
            RunRemoteScript.run("mdb_install.sh", "", hostname, log);
        }

        Thread.sleep(30000);

        // Set first 3 servers as config server
        for (String hostname : Arrays.copyOfRange(this.servers,0,3)) {
            RunRemoteScript.run("mdb_make_configserver.sh", "", hostname, log);
        }

        Thread.sleep(180000);

        // Start the mongos instance on every server. mongos connects to the config servers.
        for (String hostname : this.servers) {
            RunRemoteScript.run("mdb_make_mongos.sh", "", hostname, log);
        }

        Thread.sleep(60000);

        // Finally, start the MongoDB instances
        for (String hostname : this.servers) {
            RunRemoteScript.run("mdb_start_mongo.sh", "", hostname, log);
        }

        Thread.sleep(60000);

        // Add shards to the mongo cluster

        for (String hostname : this.servers) {
            String shellCmd = "mongo --host localhost --port 27017 --eval \"sh.addShard( \\\""+hostname+":27018\\\" )\"";
            String fServer = this.servers[0];
            log.write(fServer+" > "+shellCmd);
            RunShellCommand.executeRemoteHost(shellCmd,fServer);
        }


        log.write("DeployMongoDB - MongoDB deploy completed.");
    }
}
