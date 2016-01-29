package nl.tamasja.tools;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TIS 9-8-2014.21:14
 */
public class TestProfiler {

    protected AtomicLong duration;
    protected AtomicLong n;
    protected AtomicBoolean enabled;

    public TestProfiler() {
        this.duration = new AtomicLong(0);
        this.enabled = new AtomicBoolean(true);
        this.n = new AtomicLong(0);
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public void addResult(long duration) {
        if(this.enabled.get()) {
            this.duration.addAndGet(duration);
            this.n.addAndGet(1);
        }
    }

    public AtomicBoolean getEnabled() {
        return this.enabled;
    }

    public AtomicLong getDuration() {
        return this.duration;
    }

    public AtomicLong getN() {
        return this.n;
    }

}
