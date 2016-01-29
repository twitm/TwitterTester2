package nl.tamasja;

import nl.tamasja.searchprovider.ElasticSearchProvider;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.searchprovider.MongoDBProvider;
import nl.tamasja.searchprovider.SolrProvider;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.tools.log.LogFile;

/**
 * TIS 17-6-2014.11:32
 */
public class Main {

    public static void main(String[] args) {


        try {
            //ILog log = new LogScreen();
            ILog log = new LogFile("twitterTester2-log.log");

            try {

                //String tweetStatusFilePath = "D:\\twitterStatuses\\twitter-tools-master";
                String tweetStatusFilePath = "/mnt/tmaster/twitter-tools-master/STATUSES";

                ISearchProvider[] searchProviders = new ISearchProvider[]{
                        new ElasticSearchProvider(log),
                        new SolrProvider(log),
                        new MongoDBProvider(log),
                };

                TwitterTester twitterTester = new TwitterTester(log, tweetStatusFilePath, searchProviders, true);
                twitterTester.run();

            } catch (Exception e) {
                log.write("Main Exception: " + e.getMessage() + " - " + e.toString());
                e.printStackTrace();
            }

            log.write("EXIT");
            System.exit(0);

        } catch (Exception e) {
            System.out.println("Main Fatal Exception:");
            e.printStackTrace();
        }

    }


}
