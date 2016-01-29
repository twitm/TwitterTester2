package nl.tamasja.tools;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

/**
 * TIS 17-6-2014.12:43
 */
public class Profiler {

    protected long startTime, runTime, runTimeSeconds;


    public Profiler() {

    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {

        this.runTime = System.currentTimeMillis() - this.startTime;
        this.runTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(this.runTime);

    }

    public long getRuntime() {
        return this.runTime;
    }

    public long getRunTimeSeconds() {
        return this.runTimeSeconds;
    }

    public String outputInfo() {

        NumberFormat format = NumberFormat.getInstance();
        Runtime runtime = Runtime.getRuntime();
        String freeMemory = format.format(runtime.freeMemory() / 1024);
        String maxMemory = format.format(runtime.maxMemory() / 1024);
        String totalMemory = format.format(runtime.totalMemory() / 1024);

        return "Runtime: " + this.runTimeSeconds + "s (" + this.runTime + "ms), " +
                "Memory: Free: " + freeMemory + ", Max: " + maxMemory + ", Total: " + totalMemory;
    }

}
