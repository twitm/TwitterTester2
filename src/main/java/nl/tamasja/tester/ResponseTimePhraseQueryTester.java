package nl.tamasja.tester;

import nl.tamasja.data.ProfiledQueryResult;
import nl.tamasja.runnable.RunExecuteRandomPhraseQuery;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.TestProfiler;
import nl.tamasja.tools.log.ILog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TIS 1-9-2014.18:02
 */
public class ResponseTimePhraseQueryTester implements ITester {


    protected ILog log;
    protected ILog resultLog;
    protected ISearchProvider searchProvider;



    public ResponseTimePhraseQueryTester(ILog log, ILog resultLog, ISearchProvider searchProvider) {
        this.log = log;
        this.resultLog = resultLog;
        this.searchProvider = searchProvider;

    }

    public void writeHeader() {
        this.resultLog.write("date,label,count,query,hits,totalRunTimeMs,avgRunTimeMs,lastRunTimeMs");
    }

    protected String getTestName() {
        return "ResponseTimePhraseQueryTester";
    }

    protected List<ProfiledQueryResult> runProfiledQueries() {
        return this.searchProvider.runTimedPhraseQueries();
    }


    @Override
    public void runTest(String label) {

        // Do not limit results
        // Run each phrase query a few times to warm caches
        // Run for 60 seconds, get response times
        // Calc average

        try {


            Profiler profiler = new Profiler();

            this.searchProvider.commit();
            Thread.sleep(1000);

            long count = this.searchProvider.countTotal();

            this.log.write("Starting "+this.getTestName()+"..");

            Hashtable<String, Long> queryResultsHashTable = new Hashtable<String,Long>();

            profiler.start();

            //Warm caches
            for (int i = 1; i <= 5; i++) {
                this.runProfiledQueries();
            }

            Thread.sleep(1000);

            this.log.write("Warmed caches. starting test..");


            // Run for test
            for (int i = 1; i <= 10; i++) {


                List<ProfiledQueryResult> profiledQueryResultList = this.runProfiledQueries();

                for (ProfiledQueryResult profiledQueryResult : profiledQueryResultList) {

                    long v = queryResultsHashTable.containsKey(profiledQueryResult.getQueryString()) ? queryResultsHashTable.get(profiledQueryResult.getQueryString()) : 0;

                    v+= profiledQueryResult.getRunDuration();

                    queryResultsHashTable.put(profiledQueryResult.getQueryString(), v);


                    // Write final results
                    if(i == 10) {

                        long totalRunTime = queryResultsHashTable.get(profiledQueryResult.getQueryString());
                        double averageRunTime = totalRunTime / (double)10;

                        this.resultLog.write(
                                ""
                                        +new SimpleDateFormat("dd/MM/yyyy H:mm:ss").format(new Date())
                                        +","+label
                                        +","+String.format("%d",(long)count)
                                        +","+profiledQueryResult.getQueryString().replace(",",".")
                                        +","+profiledQueryResult.getNumFound()
                                        +","+totalRunTime
                                        +","+averageRunTime
                                        +","+profiledQueryResult.getRunDuration()
                        );
                    }

                }

            }

            profiler.stop();


            this.log.write(this.getTestName()+" Complete in " + profiler.getRuntime() + " (" + profiler.getRunTimeSeconds() + "s)");

        } catch (Exception e) {
            this.log.write("Exception in "+this.getTestName()+": "+e.getMessage()+" - "+e.toString());
            e.printStackTrace();
        }

    }
}
