package nl.tamasja.tweetprovider;

import nl.tamasja.runnable.RunIndexTweet;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;
import org.elasticsearch.search.aggregations.metrics.max.Max;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TIS 28-8-2014.13:11
 */
public class TweetScanner {

    protected File[] listFiles;

    private StatusFileReader statusFileReader;

    protected File lastFile;
    protected String lastTweetId;

    protected ILog log;
    protected ISearchProvider searchProvider;

    protected int threads = 16;

    protected int tweetZ = 1;

    protected String path;

    public TweetScanner(ILog log, String path, ISearchProvider searchProvider) {
        this.log = log;
        this.searchProvider = searchProvider;
        this.statusFileReader = new StatusFileReader(log);
        this.path = path;

        this.populateFiles();
    }

    protected void populateFiles() {

        ILog log = this.log;
        String path = this.path;



        //log.write("TweetScanner - Searching for files in ["+path+"]..");

        File gzDirectory = new File(path);
        File[] listFiles = gzDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".gz");
            }});

        if (listFiles != null) {
            log.write("TweetScanner - Found "+listFiles.length+" files in ["+path+"].");
            this.listFiles = listFiles;
        } else {
            log.write("TweetScanner - Nothing found for "+path);
            //throw new Exception("Nothing found for "+path);
        }
    }

    public void indexUp(int countRequired) {

        try {
            this.searchProvider.commit();
            Thread.sleep(500);

            int loopGuard = 0;

            while (true) {


                long count = this.searchProvider.countTotal();
                int delta = countRequired - (int) count;

                if (delta > 0) {

                    loopGuard++;

                    if (loopGuard >= 500000) {
                        this.log.write("TweetScanner - [WARNING]: indexUp max loop. Exiting.");
                        break;
                    }

                    Profiler profiler = new Profiler();
                    profiler.start();

                    this.log.write("TweetScanner - IndexUp delta: " + delta + ". CurrentIndexCount: " + count + ", need " + countRequired + ". L:" + loopGuard);

                    boolean skipFiles = false;
                    int n = 0;

                    String lastTweetId = this.lastTweetId;

                    ExecutorService executorService = Executors.newFixedThreadPool(this.threads);

                    if (this.lastFile != null) {
                        skipFiles = true;
                    }


                    for (File filePath : this.listFiles) {

                        if (skipFiles) {
                            if (this.lastFile == filePath) {
                                skipFiles = false;
                            }
                        }

                        if (skipFiles) continue;

                        //todo: test this
                        int loadTweets = Math.max(1000, delta - n);

                        List<Tweet> statusFileTweetList = this.statusFileReader.readFile(filePath.getAbsolutePath(), loadTweets, lastTweetId);
                        this.lastFile = filePath;
                        lastTweetId = null;

                        for (Tweet tweet : statusFileTweetList) {

                            executorService.submit(new RunIndexTweet(this.log, this.searchProvider, tweet));

                            this.lastTweetId = tweet.getId();
                            n++;

                            if (n >= delta) break;

                        }
                        if (n >= delta) break;
                    }

                    this.log.write("TweetScanner - Waiting for tasks..");
                    executorService.shutdown();
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

                    this.log.write("TweetScanner - Indexing complete, lastFile: " + this.lastFile + ", lastTweetID: " + this.lastTweetId);
                    Thread.sleep(100);
                    this.searchProvider.commit();
                    Thread.sleep(300);

                    long count2 = this.searchProvider.countTotal();

                    profiler.stop();

                    long added = count2 - count;

                    double ops = ((double) added / (double) Math.max(profiler.getRunTimeSeconds(),1));

                    this.log.write("TweetScanner - New count " + count2 + ", needed: " + countRequired + ". IndexUp finished in " + profiler.getRunTimeSeconds() + "s. "+ops+" ops");
                    if (count2 != countRequired) {
                        this.log.write("TweetScanner - Count mismatch detected.");
                    }
                } else if(delta < 0) {
                    this.log.write("TweetScanner - [WARNING]: Delta is below zero. delta: "+delta);
                    break;
                } else {
                    break;
                }
            }

        } catch (Exception e) {
            this.log.write("[WARNING] Exception in TweetScanner: "+e.getMessage());
        }
    }

    public List<Tweet> fetchTweets(int numTweets) {

        List<Tweet> tweetList = new ArrayList<Tweet>();

        boolean skipFiles = false;
        String lastTweetId = this.lastTweetId;
        int n = 0;

        if(this.lastFile != null) {
            skipFiles = true;
        }



        for (File filePath : this.listFiles) {

            if(skipFiles) {
                if(this.lastFile == filePath) {
                    skipFiles = false;
                }
            }

            if(skipFiles) continue;

            int loadTweets = Math.max(1,numTweets - n);

            List<Tweet> statusFileTweetList = this.statusFileReader.readFile(filePath.getAbsolutePath(),loadTweets,lastTweetId);
            this.lastFile = filePath;
            lastTweetId = null;

            for(Tweet tweet : statusFileTweetList) {
                tweetList.add(tweet);
                this.lastTweetId = tweet.getId();
                n++;

                if(n >= numTweets) break;

            }
            if(n >= numTweets) break;
        }

        if(tweetList.size() < numTweets) {
            this.log.write("TweetScanner - [WARNING]: "+numTweets+" was requested, could only provide "+tweetList.size()+".");
        }


        return tweetList;
    }
}
