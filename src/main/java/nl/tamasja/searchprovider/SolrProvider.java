package nl.tamasja.searchprovider;

import nl.tamasja.connector.SolrConnector;
import nl.tamasja.data.ProfiledQueryResult;
import nl.tamasja.deploy.DeploySolr;
import nl.tamasja.queryfactory.SolrQueryFactory;
import nl.tamasja.statcollector.RemoteStatFetcher;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TIS 21-8-2014.14:47
 */
public class SolrProvider implements ISearchProvider {

    protected ILog log;

    protected SolrConnector solrConnector;
    protected SolrQueryFactory solrQueryFactory;

    protected String[] servers = {
            "10.0.101.13", //1
            "10.0.101.14", //2
            "10.0.101.21", //3
            "10.0.101.23", //4
            "10.0.101.24", //5
    };

    public SolrProvider(ILog log) {
        this.log = log;
        this.solrConnector = new SolrConnector(this.log, this.servers);
        this.solrQueryFactory = new SolrQueryFactory();
    }


    public String getSearchProviderName() {
        return "Solr";
    }

    public void indexTweet(Tweet tweet) {
        try {
            this.solrConnector.indexTweet(tweet);
        } catch (Exception e) {
            this.log.write("SolrProvider indexTweet Exception: " + e.getMessage() + ", " + e.toString());
        }
    }

    public long countTotal() {
        try {
            return this.solrConnector.count();
        } catch (Exception e) {
            this.log.write("SolrProvider count Exception: " + e.getMessage() + ", " + e.toString());
        }

        return 0;

    }

    public void commit() {
        try {
            this.solrConnector.commit();
        } catch (Exception e) {
            this.log.write("SolrProvider commit Exception: " + e.getMessage() + ", " + e.toString());
        }
    }

    @Override
    public void clearAll() {
        try {
            this.solrConnector.clearAll();
        } catch (Exception e) {
            this.log.write("SolrProvider clearAll Exception: " + e.getMessage() + ", " + e.toString());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public long executeRandomPhraseQuery(boolean limitResults) {
        try {

            SolrQuery q = this.solrQueryFactory.getRandomPhraseQuery();

            if (limitResults) {
                q.setRows(1000);
            }

            QueryResponse queryResponse = this.solrConnector.searchQuery(q);
            return queryResponse.getResults().getNumFound();

        } catch (Exception e) {
            this.log.write("SolrProvider executeRandomPhraseQuery Exception: " + e.getMessage() + ", " + e.toString());
        }

        return 0;
    }

    @Override
    public long executeRandomFilteredPhraseQuery(boolean limitResults) {
        try {
            SolrQuery q = this.solrQueryFactory.getRandomFilteredPhraseQuery();

            if (limitResults) {
                q.setRows(1000);
            }

            QueryResponse queryResponse = this.solrConnector.searchQuery(q);
            return queryResponse.getResults().getNumFound();

        } catch (Exception e) {
            this.log.write("SolrProvider executeRandomFilteredPhraseQuery Exception: " + e.getMessage() + ", " + e.toString());
        }

        return 0;

    }

    @Override
    public List<ProfiledQueryResult> runTimedPhraseQueries() {
        List<ProfiledQueryResult> profiledQueryResultList = new ArrayList<ProfiledQueryResult>();

        List<SolrQuery> solrQueryList = this.solrQueryFactory.getPhraseQueryList();

        for (SolrQuery solrQuery : solrQueryList) {
            try {
                profiledQueryResultList.add(this.runTimedSearchQuery(solrQuery));
            } catch (Exception e) {
                this.log.write("SolrProvider runTimedPhraseQueries Exception: " + e.getMessage() + ", " + e.toString());
            }
        }

        return profiledQueryResultList;
    }

    @Override
    public List<ProfiledQueryResult> runTimedFilteredPhraseQueries() {
        List<ProfiledQueryResult> profiledQueryResultList = new ArrayList<ProfiledQueryResult>();

        List<SolrQuery> solrQueryList = this.solrQueryFactory.getFilteredPhraseQueryList();

        for (SolrQuery solrQuery : solrQueryList) {
            try {
                profiledQueryResultList.add(this.runTimedSearchQuery(solrQuery));
            } catch (Exception e) {
                this.log.write("SolrProvider runTimedFilteredPhraseQueries Exception: " + e.getMessage() + ", " + e.toString());
            }
        }

        return profiledQueryResultList;
    }

    protected ProfiledQueryResult runTimedSearchQuery(SolrQuery solrQuery) throws MalformedURLException, SolrServerException {

        Profiler p = new Profiler();
        p.start();
        QueryResponse queryResponse = this.solrConnector.searchQuery(solrQuery);
        p.stop();

        long duration = p.getRuntime();
        String queryString = solrQuery.toString();
        long numFound = queryResponse.getResults().getNumFound();

        return new ProfiledQueryResult(numFound, duration, queryString);

    }

    @Override
    public void deploy() {
        DeploySolr deploySolr = new DeploySolr(this.servers);
        try {
            deploySolr.deploy(this.log);
        } catch (Exception e) {
            this.log.write("Exception deploying Solr: " + e.getMessage() + ": " + e.toString());
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
                this.log.write("SolrProvider Fetch Memory for " + host + ": " + usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in SolrProvider.getTotalDiskUsage for node " + host + ": " + e.getMessage() + ": " + e.toString());
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
                this.log.write("SolrProvider Fetch Swap for " + host + ": " + usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in SolrProvider.getTotalSwapUsage for node " + host + ": " + e.getMessage() + ": " + e.toString());
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
                //long usage = remoteStatFetcher.getDirectorySize(host, "/etc/solr/solr-4.8.0/example/solr/collection1/data/index/");
                long usage = remoteStatFetcher.getDirectorySize(host, "/etc/solr/solr-4.8.1/example/solr/");
                this.log.write("SolrProvider Fetch Disk for " + host + ": " + usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in SolrProvider.getTotalDiskUsage for node " + host + ": " + e.getMessage() + ": " + e.toString());
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
        return 0;
    }

}