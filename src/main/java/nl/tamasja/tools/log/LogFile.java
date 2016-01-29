package nl.tamasja.tools.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TIS 7-6-2014.11:02
 */
public class LogFile implements ILog {

    String file;

    protected boolean prefixDate = true;

    public LogFile(String file, boolean prefixDate) {
        this(file);
        this.prefixDate = prefixDate;
    }

    public LogFile(String file) {

        try {

            this.file = file;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String message) {
        try {
            if (this.prefixDate) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
                String formattedDate = sdf.format(date);
                message = "[" + formattedDate + "] " + message;
            }

            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.file, true)));
            printWriter.println(message);
            printWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(Object message) {
        this.write(String.valueOf(message));
    }
}
