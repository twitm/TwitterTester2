package nl.tamasja.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * TIS 27-7-2014.18:58
 */
public class RunShellCommand {

    public static String execute(String cmd) throws IOException, InterruptedException {


        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }


    public static String executeRemoteHost(String cmd, String host) throws IOException, InterruptedException {
        String cmd2 = "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o LogLevel=ERROR root@" + host + " " + cmd;
        return RunShellCommand.execute(cmd2);
    }

    public static String sendFile(String file, String host) throws IOException, InterruptedException {
        String cmd2 = "scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o LogLevel=ERROR " + file + " root@" + host + ":";
        return RunShellCommand.execute(cmd2);
    }

}
