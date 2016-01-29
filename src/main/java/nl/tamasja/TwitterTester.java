package nl.tamasja;

import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.log.ILog;

/**
 * TIS 28-8-2014.13:42
 */
public class TwitterTester {

    protected ILog log;
    protected String tweetStatusFilePath;

    protected boolean runDeploy;
    protected ISearchProvider[] searchProviders;

    public TwitterTester(ILog log, String tweetStatusFilePath, ISearchProvider[] searchProviders, boolean runDeploy) {
        this.log = log;
        this.tweetStatusFilePath = tweetStatusFilePath;
        this.searchProviders = searchProviders;
        this.runDeploy = runDeploy;
    }

    public void run() {
        log.write("=============== TwitterTester2 ===============");


        for (ISearchProvider searchProvider : this.searchProviders) {
            log.write("-------------------- " + searchProvider.getSearchProviderName() + " --------------------");


            if (this.runDeploy) {
                searchProvider.deploy();
            }

            TwitterProviderTester twitterProviderTester = new TwitterProviderTester(this.log, searchProvider, this.tweetStatusFilePath);
            twitterProviderTester.run();


            searchProvider.close();
        }

        log.write("=============== TwitterTester2 Done ===============");

    }

    public void deployAll() {
        for (ISearchProvider searchProvider : this.searchProviders) {
            searchProvider.deploy();
            searchProvider.close();
        }
    }
}
