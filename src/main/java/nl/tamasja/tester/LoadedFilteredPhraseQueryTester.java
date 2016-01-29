package nl.tamasja.tester;

import nl.tamasja.runnable.RunIndexTweet;
import nl.tamasja.runnable.RunTester;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.tools.log.PrependLog;
import nl.tamasja.tweetprovider.TweetScanner;
import nl.tamasja.twitter.Tweet;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TIS 28-8-2014.15:40
 */
public class LoadedFilteredPhraseQueryTester implements ITester {

    protected ILog log;
    protected ILog resultLog;
    protected ISearchProvider searchProvider;
    protected TweetScanner tweetScanner;

    protected int threads;
    protected int stepIncreaseNumber;

    protected int runTime = 60000;
    protected int preloadN = 500000;

    public LoadedFilteredPhraseQueryTester(ILog log, ILog resultLog, ISearchProvider searchProvider, int threads, int runTime, int stepIncreaseNumber, TweetScanner tweetScanner) {
        this.log = log;
        this.resultLog = resultLog;
        this.searchProvider = searchProvider;
        this.threads = threads;
        this.runTime = runTime;
        this.tweetScanner = tweetScanner;
        this.stepIncreaseNumber = stepIncreaseNumber;
    }

    public void writeHeader() {

        this.resultLog.write("date,label,count,operations,runTimeMs,runTimeS,ops");
    }

    public void runTest(String label) {

        try {

            Profiler profiler = new Profiler();
            profiler.start();
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            ExecutorService indexerExecutorService = Executors.newFixedThreadPool(this.threads);

            this.log.write("Starting LoadedFilteredPhraseQueryTester..");

            this.searchProvider.commit();
            Thread.sleep(1000);

            ILog childLog = new PrependLog(log, "> (LoadedFilteredPhraseQueryTester) - ");

            FilteredPhraseQueryTester filteredPhraseQueryTester = new FilteredPhraseQueryTester(childLog, this.resultLog, this.searchProvider, this.threads, this.runTime);

            List<Tweet> preloadTweetList = this.tweetScanner.fetchTweets(this.preloadN);
            for (Tweet tweet : preloadTweetList) {
                indexerExecutorService.submit(new RunIndexTweet(this.log, this.searchProvider, tweet));
            }

            // Sleep a moment to make sure all processes are finished
            Thread.sleep(1000);

            this.log.write("LoadedFilteredPhraseQueryTester - Preload finished. Starting test..");

            executorService.submit(new RunTester(filteredPhraseQueryTester, label));

            this.log.write("LoadedFilteredPhraseQueryTest - Waiting for PhraseQueryTester..");

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

            this.log.write("LoadedFilteredPhraseQueryTest - Waiting for index tasks..");

            indexerExecutorService.shutdown();
            indexerExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

            profiler.stop();
            this.log.write("LoadedFilteredPhraseQueryTest Complete in " + profiler.getRuntime() + " (" + profiler.getRunTimeSeconds() + "s)");

        } catch (Exception e) {
            this.log.write("Exception in LoadedFilteredPhraseQueryTester: " + e.getMessage() + " - " + e.toString());
            e.printStackTrace();
        }

    }

}
