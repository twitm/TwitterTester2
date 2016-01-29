package nl.tamasja.runnable;

import nl.tamasja.tools.TestProfiler;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;

/**
 * TIS 28-8-2014.13:19
 */
public class RunIndexTweet implements Runnable {

    protected Tweet tweet;

    protected ILog log;
    protected TestProfiler testProfiler;
    protected ISearchProvider searchProvider;

    public RunIndexTweet(ILog log, ISearchProvider searchProvider, Tweet tweet, TestProfiler testProfiler) {
        this.tweet = tweet;
        this.log = log;
        this.searchProvider = searchProvider;
        this.testProfiler = testProfiler;
    }

    public RunIndexTweet(ILog log, ISearchProvider searchProvider, Tweet tweet) {
        this.tweet = tweet;
        this.log = log;
        this.searchProvider = searchProvider;
    }

    @Override
    public void run() {
        try {


            if(this.testProfiler == null) {
                this.searchProvider.indexTweet(tweet);
            } else {
                //Go into test mode
                if (this.testProfiler.getEnabled().get()) {

                    Profiler p = new Profiler();
                    p.start();
                    this.searchProvider.indexTweet(tweet);
                    p.stop();
                    this.testProfiler.addResult(p.getRuntime());

                } else {
                    //Index anyways
                    this.searchProvider.indexTweet(tweet);
                }
            }
        } catch (Exception e) {
            this.log.write("[WARNING] RunIndexTweet Exception: "+e.getMessage()+", "+e.toString());
        }
    }
}
