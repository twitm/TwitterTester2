package nl.tamasja.tools.log;

/**
 * TIS 28-8-2014.15:55
 *
 * Writes log messages with string prepended.
 *
 */
public class PrependLog implements ILog {

    protected String prependString;
    protected ILog log;

    public PrependLog(ILog log, String prependString) {
        this.log = log;
        this.prependString = prependString;
    }

    @Override
    public void write(String message) {
        this.log.write(this.prependString+message);
    }

    @Override
    public void write(Object message) {
        this.write(this.prependString+String.valueOf(message));
    }
}
