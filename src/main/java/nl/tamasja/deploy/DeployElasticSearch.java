package nl.tamasja.deploy;

import nl.tamasja.tools.RunRemoteScript;
import nl.tamasja.tools.log.ILog;

import java.io.IOException;

/**
 * TIS 24-8-2014.1:04
 */
public class DeployElasticSearch implements IDeploySearchProvider {

    protected String[] servers;

    public DeployElasticSearch(String[] servers) {
        this.servers = servers;
    }

    public void deploy(ILog log) throws IOException, InterruptedException {


        for (String hostname : this.servers) {
            log.write("DeployElasticSearch - Running ElasticSearch Installer on "+hostname);
            RunRemoteScript.run("es_install.sh", "", hostname, log);
        }

        //Wait for init
        log.write("DeployElasticSearch - Going to sleep (Waiting for ES nodes).");
        Thread.sleep(180000);

        log.write("DeployElasticSearch - ElasticSearch deploy completed.");
    }
}
