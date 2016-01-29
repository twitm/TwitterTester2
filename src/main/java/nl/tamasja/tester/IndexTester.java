package nl.tamasja.tester;

import nl.tamasja.tools.TestProfiler;
import nl.tamasja.tweetprovider.TweetScanner;
import nl.tamasja.runnable.RunIndexTweet;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TIS 28-8-2014.13:09
 */
public class IndexTester implements ITester {

    protected ILog log;
    protected ILog resultLog;
    protected ISearchProvider searchProvider;
    protected TweetScanner tweetScanner;

    protected int threads;

    protected int preloadN = 500000;
    protected int runTime = 60000;

    public IndexTester(ILog log, ILog resultLog, ISearchProvider searchProvider, int threads, int runTime, int stepIncreaseNumber, TweetScanner tweetScanner) {
        this.log = log;
        this.resultLog = resultLog;
        this.searchProvider = searchProvider;
        this.tweetScanner = tweetScanner;
        this.threads = threads;
        this.runTime = runTime;

        if(this.preloadN >= stepIncreaseNumber) {
            this.preloadN = stepIncreaseNumber-1;
        }

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


            this.log.write("Starting IndexTest..");

            List<Tweet> preloadTweetList = this.tweetScanner.fetchTweets(preloadN);


            if(preloadTweetList.size() != preloadN) {
                throw new Exception("PreloadTweetList is "+preloadTweetList.size()+". Expected: "+preloadN);
            }

            this.log.write("IndexTester - Preload finished. Starting test..");

            profiler.start();

            long startTimeCorrection = System.currentTimeMillis();

            for (Tweet tweet : preloadTweetList) {
                executorService.submit(new RunIndexTweet(this.log,this.searchProvider,tweet,testProfiler));
            }

            Thread.sleep(this.runTime - (System.currentTimeMillis()-startTimeCorrection));
            testProfiler.setEnabled(false);
            profiler.stop();

            this.log.write("IndexTester Finished. Awaiting Tasks.. N is " + testProfiler.getN().get());

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);


            if(testProfiler.getN().get() >= this.preloadN) {
                this.log.write("[WARNING] IndexTester N is preload N, bad results.");
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


            this.log.write("IndexTester Complete: " + ops + " ops in " + profiler.getRuntime() + " (" + profiler.getRunTimeSeconds() + "s)");

        } catch (Exception e) {
            this.log.write("Exception in IndexTester: "+e.getMessage()+" - "+e.toString());
            e.printStackTrace();
        }
    }


}
