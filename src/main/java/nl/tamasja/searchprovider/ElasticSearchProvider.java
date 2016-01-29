package nl.tamasja.searchprovider;

import nl.tamasja.connector.ElasticSearchConnector;
import nl.tamasja.data.ProfiledQueryResult;
import nl.tamasja.deploy.DeployElasticSearch;
import nl.tamasja.queryfactory.ElasticSearchQueryFactory;
import nl.tamasja.statcollector.RemoteStatFetcher;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TIS 24-8-2014.11:36
 */
public class ElasticSearchProvider implements ISearchProvider {
    protected ILog log;

    protected ElasticSearchConnector elasticSearchConnector;
    protected ElasticSearchQueryFactory elasticSearchQueryFactory;


    protected String[] servers = {
            "10.0.101.22", //1
            "10.0.101.23", //2
            "10.0.101.24", //3
            "10.0.101.28", //4
            "10.0.101.26", //5
    };


    public ElasticSearchProvider(ILog log) {
        this.log = log;
        this.elasticSearchConnector = new ElasticSearchConnector(this.log, this.servers);
        this.elasticSearchQueryFactory = new ElasticSearchQueryFactory(this.elasticSearchConnector);

    }

    @Override
    public String getSearchProviderName() {
        return "ElasticSearch";
    }

    @Override
    public long countTotal() {
        return this.elasticSearchConnector.count();
    }

    @Override
    public void commit() {
        try {
            this.elasticSearchConnector.refresh();
            //Some delay after refresh seems to increase reliability. Unsure about proper min value. 100ms is ok with 1 doc.
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearAll() {
        try {
            this.elasticSearchConnector.clearAll();
        } catch (Exception e) {
            this.log.write("Exception in ElasticSearchProvider.clearAll: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        this.elasticSearchConnector.closeClient();
    }

    @Override
    public void indexTweet(Tweet tweet) {
        try {
            this.elasticSearchConnector.indexTweet(tweet);
        } catch (IOException e) {
            this.log.write("Exception in ElasticSearchProvider.indexTweet: " + e.getMessage() + " - " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public long executeRandomPhraseQuery(boolean limitResults) {

        SearchRequestBuilder searchRequestBuilder = this.elasticSearchQueryFactory.getRandomPhraseQuery();

        if (limitResults) {
            searchRequestBuilder = searchRequestBuilder.setFrom(0).setSize(1000);
        }

        SearchResponse response = this.elasticSearchConnector.searchQuery(searchRequestBuilder);
        return response.getHits().getTotalHits();
    }

    @Override
    public long executeRandomFilteredPhraseQuery(boolean limitResults) {

        SearchRequestBuilder searchRequestBuilder = this.elasticSearchQueryFactory.getRandomFilteredPhraseQuery();

        if (limitResults) {
            searchRequestBuilder = searchRequestBuilder.setFrom(0).setSize(1000);
        }

        SearchResponse response = this.elasticSearchConnector.searchQuery(searchRequestBuilder);


        return response.getHits().getTotalHits();

    }


    @Override
    public void deploy() {
        DeployElasticSearch deployElasticSearch = new DeployElasticSearch(this.servers);
        try {
            deployElasticSearch.deploy(this.log);
        } catch (Exception e) {
            this.log.write("Exception deploying ElasticSearch: " + e.getMessage() + ": " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void remove() {

    }

    @Override
    public long getTotalMemoryUsage() {
        long total = 0;

        RemoteStatFetcher remoteStatFetcher = new RemoteStatFetcher();
        for (String host : this.servers) {
            try {
                long usage = remoteStatFetcher.getMemoryUsage(host);
                this.log.write("ElasticSearchProvider Fetch Memory for " + host + ": " + usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in ElasticSearchProvider.getTotalMemoryUsage for node " + host + ": " + e.getMessage() + ": " + e.toString());
                return 0;
            }
        }

        return total;
    }

    @Override
    public long getTotalSwapUsage() {
        long total = 0;

        RemoteStatFetcher remoteStatFetcher = new RemoteStatFetcher();
        for (String host : this.servers) {
            try {
                long usage = remoteStatFetcher.getSwapUsage(host);
                this.log.write("ElasticSearchProvider Fetch Swap for " + host + ": " + usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in ElasticSearchProvider.getTotalSwapUsage for node " + host + ": " + e.getMessage() + ": " + e.toString());
                return 0;
            }
        }

        return total;
    }

    @Override
    public long getTotalDiskUsage() {

        long total = 0;

        RemoteStatFetcher remoteStatFetcher = new RemoteStatFetcher();
        for (String host : this.servers) {
            try {
                long usage = remoteStatFetcher.getDirectorySize(host, "/var/lib/elasticsearch");
                this.log.write("ElasticSearchProvider Fetch Disk for " + host + ": " + usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in ElasticSearchProvider.getTotalDiskUsage for node " + host + ": " + e.getMessage() + ": " + e.toString());
                return 0;
            }
        }

        return total;

    }

    @Override
    public long getNodeNumber() {
        return this.servers.length;

    }

    @Override
    public long getLiveNodeNumber() {
        return this.elasticSearchConnector.getNumberOfNodes();
    }

    @Override
    public List<ProfiledQueryResult> runTimedPhraseQueries() {

        List<ProfiledQueryResult> profiledQueryResultList = new ArrayList<ProfiledQueryResult>();

        List<SearchRequestBuilder> searchRequestBuilders = this.elasticSearchQueryFactory.getPhraseQueryList();

        for (SearchRequestBuilder searchRequestBuilder : searchRequestBuilders) {
            profiledQueryResultList.add(this.runTimedSearchQuery(searchRequestBuilder));
        }

        return profiledQueryResultList;

    }

    @Override
    public List<ProfiledQueryResult> runTimedFilteredPhraseQueries() {
        List<ProfiledQueryResult> profiledQueryResultList = new ArrayList<ProfiledQueryResult>();

        List<SearchRequestBuilder> searchRequestBuilders = this.elasticSearchQueryFactory.getFilteredPhraseQueryList();

        for (SearchRequestBuilder searchRequestBuilder : searchRequestBuilders) {
            profiledQueryResultList.add(this.runTimedSearchQuery(searchRequestBuilder));
        }

        return profiledQueryResultList;
    }

    protected ProfiledQueryResult runTimedSearchQuery(SearchRequestBuilder searchRequestBuilder) {
        Profiler p = new Profiler();
        p.start();
        SearchResponse searchResponse = this.elasticSearchConnector.searchQuery(searchRequestBuilder);
        p.stop();

        long duration = p.getRuntime();
        String queryString = searchRequestBuilder.toString().replace("\n", "").replace("\r", "");
        long numFound = searchResponse.getHits().getTotalHits();

        return new ProfiledQueryResult(numFound, duration, queryString);

    }

}
