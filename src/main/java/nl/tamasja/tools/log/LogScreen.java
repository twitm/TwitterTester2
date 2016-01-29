package nl.tamasja.tools.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TIS 7-6-2014.9:38
 */
public class LogScreen implements ILog {
    @Override
    public void write(String message) {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
        String formattedDate = sdf.format(date);

        System.out.println("[" + formattedDate + "] " + message);
    }

    public void write(Object message) {
        this.write(String.valueOf(message));
    }
}
