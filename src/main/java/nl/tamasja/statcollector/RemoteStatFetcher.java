package nl.tamasja.statcollector;

import nl.tamasja.tools.RunShellCommand;

import java.io.IOException;

/**
 * TIS 31-8-2014.0:54
 */
public class RemoteStatFetcher {

    public long getMemoryUsage(String host) throws IOException, InterruptedException {
        return Long.parseLong(RunShellCommand.executeRemoteHost("free | grep \"buffers/cache\" | awk '{print $3}'", host));
    }

    public long getSwapUsage(String host) throws IOException, InterruptedException {
        return Long.parseLong(RunShellCommand.executeRemoteHost("free | grep \"Swap\" | awk '{print $3}'", host));
    }

    public long getDirectorySize(String host, String directory) throws IOException, InterruptedException {
        return Long.parseLong(RunShellCommand.executeRemoteHost("du -s "+directory+" | awk '{ print $1 }'",host));
    }

}