package nl.tamasja.tester;

import nl.tamasja.tools.TestProfiler;
import nl.tamasja.runnable.RunExecuteRandomPhraseQuery;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TIS 28-8-2014.12:39
 */
public class PhraseQueryTester implements ITester {

    protected ILog log;
    protected ILog resultLog;
    protected ISearchProvider searchProvider;

    protected int threads;

    protected int preloadN = 200000;
    protected int runTime = 60000;

    public PhraseQueryTester(ILog log, ILog resultLog, ISearchProvider searchProvider, int threads, int runTime) {
        this.log = log;
        this.resultLog = resultLog;
        this.searchProvider = searchProvider;
        this.threads = threads;
        this.runTime = runTime;
    }

    public void writeHeader() {
        this.resultLog.write("date,label,count,operations,runTimeMs,runTimeS,ops");
    }

    public void runTest(String label) {
        try {


            Profiler profiler = new Profiler();
            TestProfiler testProfiler = new TestProfiler();
            ExecutorService executorService = Executors.newFixedThreadPool(threads);

            this.searchProvider.commit();
            Thread.sleep(1000);

            long count = this.searchProvider.countTotal();

            this.log.write("Starting PhraseQueryTest..");

            profiler.start();

            long startTimeCorrection = System.currentTimeMillis();

            for (int i = 0; i < this.preloadN; i++) {
                executorService.submit(new RunExecuteRandomPhraseQuery(this.log,testProfiler,this.searchProvider,true));
            }
            Thread.sleep(this.runTime - (System.currentTimeMillis()-startTimeCorrection));

            testProfiler.setEnabled(false);

            profiler.stop();
            this.log.write("PhraseQueryTest Finished. Awaiting Tasks.. N is "+testProfiler.getN().get());

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);


            if(testProfiler.getN().get() >= this.preloadN) {
                this.log.write("[WARNING] FilteredPhraseQueryTester N is preload N, bad results.");
            }


            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
            String formattedDate = sdf.format(date);

            double ops = ((double) testProfiler.getN().get() / (double) profiler.getRunTimeSeconds());

            this.resultLog.write(
                    ""
                            +formattedDate
                            +","+label
                            +","+String.format("%d",(long)count)
                            +","+String.format("%d", (long) testProfiler.getN().get())
                            +","+profiler.getRuntime()
                            +","+profiler.getRunTimeSeconds()
                            +","+ops
            );

            this.log.write("PhraseQueryTest Complete: " + ops + " ops in " + profiler.getRuntime() + " (" + profiler.getRunTimeSeconds() + "s)");

        } catch (Exception e) {
            this.log.write("Exception in PhraseQueryTest: "+e.getMessage()+" - "+e.toString());
            e.printStackTrace();
        }

    }

}
