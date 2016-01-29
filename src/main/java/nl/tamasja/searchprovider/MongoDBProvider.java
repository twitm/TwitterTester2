package nl.tamasja.searchprovider;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import nl.tamasja.connector.MongoDBConnector;
import nl.tamasja.data.ProfiledQueryResult;
import nl.tamasja.deploy.DeployMongoDB;
import nl.tamasja.queryfactory.MongoQueryFactory;
import nl.tamasja.statcollector.RemoteStatFetcher;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * TIS 10-9-2014.11:33
 */
public class MongoDBProvider implements ISearchProvider {

    protected ILog log;

    protected MongoDBConnector mongoDBConnector;
    protected MongoQueryFactory mongoQueryFactory;

    protected String[] servers = {
            "10.0.101.11", //1
            "10.0.101.12", //2
            "10.0.101.13", //3
            "10.0.101.14", //4
            "10.0.101.21", //5
    };



    public MongoDBProvider(ILog log) {
        this.log = log;
        this.mongoDBConnector = new MongoDBConnector(this.log,this.servers);
        this.mongoQueryFactory = new MongoQueryFactory();
    }

    @Override
    public String getSearchProviderName() {
        return "MongoDB";
    }

    @Override
    public long countTotal() {
        return this.mongoDBConnector.count();
    }

    @Override
    public void commit() { /*return;*/ }

    @Override
    public void clearAll() {
        this.mongoDBConnector.clearAll();
    }

    @Override
    public void close() {
        this.mongoDBConnector.close();
    }

    @Override
    public void indexTweet(Tweet tweet) {
        this.mongoDBConnector.indexTweet(tweet);
    }

    @Override
    public long executeRandomPhraseQuery(boolean limitResults) {
        DBCursor dbCursor = this.mongoDBConnector.find(this.mongoQueryFactory.getRandomPhraseQuery().get()).limit(1000);
        return dbCursor.count();
    }

    @Override
    public long executeRandomFilteredPhraseQuery(boolean limitResults) {
        DBCursor dbCursor = this.mongoDBConnector.find(this.mongoQueryFactory.getRandomFilteredPhraseQuery().get()).limit(1000);
        return dbCursor.count();
    }

    @Override
    public List<ProfiledQueryResult> runTimedPhraseQueries() {
        List<ProfiledQueryResult> profiledQueryResultList = new ArrayList<ProfiledQueryResult>();

        List<BasicDBObjectBuilder> basicDBObjectBuilders = this.mongoQueryFactory.getPhraseQueryList();

        for(BasicDBObjectBuilder basicDBObjectBuilder : basicDBObjectBuilders) {
            profiledQueryResultList.add(this.runTimedSearchQuery(basicDBObjectBuilder.get()));
        }

        return profiledQueryResultList;
    }

    @Override
    public List<ProfiledQueryResult> runTimedFilteredPhraseQueries() {
        List<ProfiledQueryResult> profiledQueryResultList = new ArrayList<ProfiledQueryResult>();

        List<BasicDBObjectBuilder> basicDBObjectBuilders = this.mongoQueryFactory.getFilteredPhraseQueryList();

        for(BasicDBObjectBuilder basicDBObjectBuilder : basicDBObjectBuilders) {
            profiledQueryResultList.add(this.runTimedSearchQuery(basicDBObjectBuilder.get()));
        }

        return profiledQueryResultList;
    }

    protected ProfiledQueryResult runTimedSearchQuery(DBObject query) {
        Profiler p = new Profiler();
        p.start();
        DBCursor dbCursor = this.mongoDBConnector.find(query);
        p.stop();

        long duration = p.getRuntime();
        String queryString = dbCursor.toString();
        long numFound = dbCursor.count();

        return new ProfiledQueryResult(numFound,duration,queryString);

    }

    @Override
    public void deploy() {
        DeployMongoDB deployMongoDB = new DeployMongoDB(this.servers);
        try {
            deployMongoDB.deploy(this.log);
        } catch (Exception e) {
            this.log.write("Exception deploying MongoDB: "+e.getMessage()+": "+e.toString());
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
                this.log.write("MongoDBProvider Fetch Memory for "+host+": "+usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in MongoDBProvider.getTotalMemoryUsage for node "+host+": "+e.getMessage()+": "+e.toString());
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
                this.log.write("MongoDBProvider Fetch Swap for "+host+": "+usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in MongoDBProvider.getTotalSwapUsage for node "+host+": "+e.getMessage()+": "+e.toString());
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
                long usage = remoteStatFetcher.getDirectorySize(host, "/var/lib/mongo");
                this.log.write("MongoDBProvider Fetch Disk for "+host+": "+usage);
                total += usage;
            } catch (Exception e) {
                this.log.write("Exception in MongoDBProvider.getTotalDiskUsage for node "+host+": "+e.getMessage()+": "+e.toString());
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
        return this.servers.length;
    }


}
