package nl.tamasja.tools;

import nl.tamasja.tools.log.ILog;

import java.io.IOException;

/**
 * TIS 27-7-2014.20:10
 *
 * Runs shell script remotely
 *
 */
public class RunRemoteScript {

    public static void run(String resource, String params, String host, ILog log) throws IOException, InterruptedException {


        String hostname = RunShellCommand.executeRemoteHost("hostname",host);

        log.write("Executing script "+resource+" on "+hostname+" ("+host+")");

        WriteResource.write(resource);

        //Copy file
        RunShellCommand.sendFile(resource, host);

        //Set Permissions
        RunShellCommand.executeRemoteHost("chmod 755 "+resource,host);

        //Execute
        RunShellCommand.executeRemoteHost("./"+resource+" "+params+" > "+resource+".execute.log",host);


        //Cleanup
        RunShellCommand.executeRemoteHost("rm "+resource,host);
        WriteResource.clean(resource);


    }

}
