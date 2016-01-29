package nl.tamasja.tools.log;

/**
 * TIS 28-8-2014.13:41
 */
public class LogVoid implements ILog {
    @Override
    public void write(String message) {
        //empty
    }

    @Override
    public void write(Object message) {
        //empty
    }
}
