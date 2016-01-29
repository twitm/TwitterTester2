package nl.tamasja.runnable;

import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.TestProfiler;
import nl.tamasja.tools.log.ILog;

/**
 * TIS 28-8-2014.12:53
 */
public class RunExecuteRandomPhraseQuery implements Runnable {


    protected ISearchProvider searchProvider;
    protected TestProfiler testProfiler;
    protected ILog log;
    protected boolean limitResults;

    public RunExecuteRandomPhraseQuery(ILog log, TestProfiler testProfiler, ISearchProvider searchProvider, boolean limitResults) {
        this.log = log;
        this.testProfiler = testProfiler;
        this.searchProvider = searchProvider;
        this.limitResults = limitResults;
    }


    @Override
    public void run() {
        try {

            if (this.testProfiler.getEnabled().get()) {

                Profiler p = new Profiler();
                p.start();
                this.searchProvider.executeRandomPhraseQuery(this.limitResults);
                p.stop();
                this.testProfiler.addResult(p.getRuntime());
            }
        } catch (Exception e) {
            this.log.write("[WARNING] Exception in QueryRunnerTask: " + e.getMessage());
        }
    }
}
