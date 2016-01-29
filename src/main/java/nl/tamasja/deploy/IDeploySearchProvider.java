package nl.tamasja.deploy;

import nl.tamasja.tools.log.ILog;

import java.io.IOException;

/**
 * TIS 24-8-2014.1:04
 */
public interface IDeploySearchProvider {
    public void deploy(ILog log) throws IOException, InterruptedException;
}
