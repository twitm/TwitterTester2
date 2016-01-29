package nl.tamasja;

import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tester.*;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.TestProfiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.tools.log.LogFile;
import nl.tamasja.tweetprovider.TweetScanner;

/**
 * TIS 28-8-2014.13:44
 */
public class TwitterProviderTester {

    // How many threads to use?
    protected int threads = 32;

    // At what index values do we want to test?
    //protected int stepIncrease = 1000000; //1M
    protected int stepIncrease = 5000000; //5M
    //protected int stepIncrease = 500000; //5kk

    // When to stop?
    protected int maxLimit = 300000000;

    // How long do we want to run our tests (in ms)?
    protected int runTime = 60000;

    // How many Tweets do we want to preload in memory before starting a test? Should be less than stepIncrease.
    //protected int preloadNumber = 100000;


    // Short delay between running tests
    protected int testSleepDelay = 500;

    protected ILog log;
    protected ISearchProvider searchProvider;
    protected TweetScanner tweetScanner;

    protected int currentStepNumber = 0;

    public TwitterProviderTester(ILog log, ISearchProvider searchProvider, String filesPath) {

        this.log = log;
        this.searchProvider = searchProvider;
        this.tweetScanner = new TweetScanner(log,filesPath,searchProvider);
    }

    public void run() {

        try {
            log.write("Starting TwitterProviderTester for "+this.searchProvider.getSearchProviderName()+" with "+this.threads+" thread(s). Step is "+this.stepIncrease+". Limit it "+this.maxLimit+".");

            //Logs

            ILog logResourceUsageTestResults = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-resourceUsageTestResults.log.csv",false);

            ILog logPhraseTestResults = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-phraseSearchResults.log.csv",false);
            ILog logFilteredPhraseTestResults = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-filteredPhraseSearchResults.log.csv",false);

            ILog logIndexTest = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-indexResults.log.csv",false);

            ILog logLoadedPhraseTestResults = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-loadedPhraseSearchResults.log.csv",false);
            ILog logLoadedFilteredPhraseTestResults = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-loadedFilteredPhraseSearchResults.log.csv",false);

            ILog logResponseTimePhraseTestResults = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-responseTimePhraseResults.log.csv",false);
            ILog logResponseTimeFilteredPhraseTestResults = new LogFile("twitterTester2-"+this.searchProvider.getSearchProviderName()+"-responseTimeFilteredPhraseResults.log.csv",false);


            //Testers

            ResourceUsageTester resourceUsageTester = new ResourceUsageTester(log,logResourceUsageTestResults,this.searchProvider);

            ResponseTimePhraseQueryTester responseTimePhraseQueryTester = new ResponseTimePhraseQueryTester(log,logResponseTimePhraseTestResults,this.searchProvider);
            ResponseTimeFilteredPhraseQueryTester responseTimeFilteredPhraseQueryTester = new ResponseTimeFilteredPhraseQueryTester(log,logResponseTimeFilteredPhraseTestResults,this.searchProvider);

            PhraseQueryTester phraseQueryTester = new PhraseQueryTester(log,logPhraseTestResults,this.searchProvider,this.threads,this.runTime);
            FilteredPhraseQueryTester filteredPhraseQueryTester = new FilteredPhraseQueryTester(log,logFilteredPhraseTestResults,this.searchProvider,this.threads,this.runTime);

            IndexTester indexTester = new IndexTester(log,logIndexTest,this.searchProvider,this.threads,this.runTime,this.stepIncrease,this.tweetScanner);

            LoadedPhraseQueryTester loadedPhraseQueryTester = new LoadedPhraseQueryTester(log,logLoadedPhraseTestResults,this.searchProvider,this.threads,this.runTime,this.stepIncrease,this.tweetScanner);
            LoadedFilteredPhraseQueryTester loadedFilteredPhraseQueryTester = new LoadedFilteredPhraseQueryTester(log,logLoadedFilteredPhraseTestResults,this.searchProvider,this.threads,this.runTime,this.stepIncrease,this.tweetScanner);


            // Write Headers

            resourceUsageTester.writeHeader();

            responseTimePhraseQueryTester.writeHeader();
            responseTimeFilteredPhraseQueryTester.writeHeader();

            phraseQueryTester.writeHeader();
            filteredPhraseQueryTester.writeHeader();

            indexTester.writeHeader();

            loadedPhraseQueryTester.writeHeader();
            loadedFilteredPhraseQueryTester.writeHeader();

            Profiler stepProfiler = new Profiler();
            Profiler globalProfiler = new Profiler();
            globalProfiler.start();



            log.write("Clearing index..");
            this.searchProvider.clearAll();
            Thread.sleep(1000);

            this.currentStepNumber = 0;
            int loopN = 1;

            while(true) {
                stepProfiler.start();
                this.log.write("Starting Test Run " + loopN + ". Step: " + this.currentStepNumber);

                //Make sure index is up to date and good to go.
                this.searchProvider.commit();
                Thread.sleep(1000);

                this.tweetScanner.indexUp(this.currentStepNumber);

                float count = this.searchProvider.countTotal();


                if (count != this.currentStepNumber) {

                    if(count < this.currentStepNumber*0.95 || count > this.currentStepNumber*1.05) {
                        this.log.write("[ERROR]: Index count is " + (int)count + ", expected " + this.currentStepNumber+". Aborting test.");
                        return;
                    } else {
                        this.log.write("[WARNING]: Index count is " + (int)count + ", expected " + this.currentStepNumber+". Count is within bounds.");
                    }


                }

                this.log.write("Running test with " +  (int)count + " tweets in index.");

                String label = ""+this.currentStepNumber;

                Thread.sleep(this.testSleepDelay);




                this.log.write("-- TEST0: RESOURCE USAGE");
                resourceUsageTester.runTest(label);
                Thread.sleep(this.testSleepDelay);

                this.log.write("-- TEST1: PHRASE QUERY");
                phraseQueryTester.runTest(label);
                Thread.sleep(this.testSleepDelay);

                this.log.write("-- TEST2: FILTERED PHRASE QUERY");
                filteredPhraseQueryTester.runTest(label);
                Thread.sleep(this.testSleepDelay);

                this.log.write("-- TEST3: QUERY RESPONSE TIME");
                responseTimePhraseQueryTester.runTest(label);
                Thread.sleep(this.testSleepDelay);

                this.log.write("-- TEST4: FILTERED QUERY RESPONSE TIME");
                responseTimeFilteredPhraseQueryTester.runTest(label);
                Thread.sleep(this.testSleepDelay);

                this.log.write("-- TEST5: INDEXING");
                indexTester.runTest(label);
                Thread.sleep(this.testSleepDelay);

                this.log.write("-- TEST6: LOADED PHRASE QUERY");
                loadedPhraseQueryTester.runTest(label);
                Thread.sleep(this.testSleepDelay);

                this.log.write("-- TEST7: LOADED FILTERED PHRASE QUERY");
                loadedFilteredPhraseQueryTester.runTest(label);
                Thread.sleep(this.testSleepDelay);



                stepProfiler.stop();
                this.log.write("Test Run " + loopN + " completed. "+stepProfiler.outputInfo());


                if(this.currentStepNumber >= this.maxLimit) {
                    break;
                }

                // Increase Step
                this.currentStepNumber += this.stepIncrease;

                // Increase loop counter
                loopN++;
            }

            globalProfiler.stop();

            this.log.write("TwitterProviderTester for "+this.searchProvider.getSearchProviderName()+" finished. "+globalProfiler.outputInfo());

        } catch (Exception e) {
            this.log.write("Exception in TwitterProviderTester: "+e.getMessage()+" - "+e.toString());
            e.printStackTrace();
        }
    }
}
