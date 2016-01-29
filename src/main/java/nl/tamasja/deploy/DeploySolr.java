package nl.tamasja.deploy;

import nl.tamasja.tools.RunRemoteScript;
import nl.tamasja.tools.RunShellCommand;
import nl.tamasja.tools.log.ILog;

import java.io.IOException;

/**
 * TIS 28-7-2014.21:54
 */
public class DeploySolr implements IDeploySearchProvider {


    protected String[] servers;

    public DeploySolr(String[] servers) {
        this.servers = servers;
    }

    public void deploy(ILog log) throws IOException, InterruptedException {


        // Uninstall All Zookeeper and Solr instances
        for (String hostname : this.servers) {
            RunRemoteScript.run("solr_uninstall.sh", "", hostname, log);
        }

        Thread.sleep(5000);



        // Restart
        for (String hostname : this.servers) {
            RunRemoteScript.run("shutdown.sh", "", hostname, log);
        }

        Thread.sleep(60000);


        //Install Zookeeper + Solr

        int i = 1;
        for (String hostname : this.servers) {
            log.write("DeploySolr - Running Solr Installer on "+hostname+": "+i);

            String n = i+"";
            RunRemoteScript.run("solr_install.sh", n , hostname, log);
            i++;

        }

        log.write("DeploySolr - Going to sleep (Waiting for Zookeeper).");
        //Wait for init
        Thread.sleep(120000);

        log.write("DeploySolr - Starting Solr instances..");

        // Start Solr Instances
        for (String hostname : this.servers) {
            RunRemoteScript.run("solr_start.sh", "" , hostname, log);
        }

        log.write("DeploySolr - Going to sleep (Waiting for Solr nodes).");

        Thread.sleep(180000);

        log.write("DeploySolr - Solr deploy completed.");


    }

    public void recover(ILog log) throws IOException, InterruptedException {

        // Stop Solr Instances
        for (String hostname : this.servers) {
            RunRemoteScript.run("solr_stop.sh", "" , hostname, log);
        }

        Thread.sleep(5000);

        int i = 1;
        for (String hostname : this.servers) {
            String n = i+"";
            RunRemoteScript.run("solr_recover.sh", n, hostname, log);
            i++;
        }

        Thread.sleep(300000);

        // Start Solr Instances
        for (String hostname : this.servers) {
            RunRemoteScript.run("solr_start.sh", "" , hostname, log);
        }

        log.write("DeploySolr - Solr recovery completed.");

    }

}
