package nl.tamasja.connector;

import com.mongodb.*;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * TIS 11-9-2014.16:39
 */
public class MongoDBConnector {

    protected ILog log;
    protected String[] servers;

    protected MongoClient mongoClient;
    protected DB db;
    protected DBCollection dbCollection;

    public MongoDBConnector(ILog log, String[] servers) {
        this.log = log;
        this.servers = servers;

        this.connect();
    }


    protected void connect() {

        if (this.mongoClient == null) {
            try {


                List<ServerAddress> addresses = new ArrayList<ServerAddress>();


                for (String host : this.servers) {
                    addresses.add(new ServerAddress(host, 27017));
                }

                this.mongoClient = new MongoClient(addresses,
                        MongoClientOptions.builder()
                                .connectionsPerHost(30)
                                .threadsAllowedToBlockForConnectionMultiplier(20)
                                .build());


                this.db = this.mongoClient.getDB("twitter");

                this.dbCollection = this.db.getCollection("tweets");
            } catch (Exception e) {
                this.log.write("MongoDBConnector - Exception in MongoDBConnector.connect: " + e.getMessage() + ": " + e.toString());
            }
        }
    }

    public void close() {
        this.mongoClient.close();
        this.mongoClient = null;
    }

    public long count() {
        return this.dbCollection.count();
    }


    public DBCursor find(DBObject query) {
        return this.dbCollection.find(query);
    }

    public void clearAll() {
        this.connect();
        this.drop();
        this.createDatabase();
    }

    protected void createDatabase() {


        this.connect();

        log.write("MongoDBConnector - enablesharding for database..");

        log.write(this.mongoClient.getDB("admin").command(new BasicDBObject("enablesharding", ("twitter"))));


        log.write("MongoDBConnector - Creating hashed _id index..");
        dbCollection.createIndex(new BasicDBObject("_id", "hashed"));

        log.write("shardCollection twitter.tweets..");

        log.write(

                this.mongoClient.getDB("admin").command(new BasicDBObject("shardCollection", "twitter.tweets").
                        append("key", new BasicDBObject("_id", "hashed"))
                )

        );

        log.write("MongoDBConnector - Creating indexes");

        //dbCollection.createIndex(new BasicDBObject("id", 1), new BasicDBObject("unique", true));
        dbCollection.createIndex(new BasicDBObject("text", "text"));

        log.write("MongoDBConnector - database created");

    }

    protected void drop() {
        this.db.dropDatabase();
    }

    public void indexTweet(Tweet tweet) {

        this.connect();

        DBObject dbObject = BasicDBObjectBuilder.start()
                //.add("_id", status.getId())
                .add("id", tweet.getId())
                .add("text", tweet.getText())
                .add("created_at", tweet.getCreatedAt())
                .add("hashtags", tweet.getHashTags())
                .add("userId", tweet.getUserId())
                .add("userName", tweet.getUserName())
                .add("userScreenName", tweet.getUserScreenName())
                .add("coordinates", tweet.getCoordinates())
                .get();

        //WriteConcern wc = WriteConcern.UNACKNOWLEDGED;
        WriteConcern wc = WriteConcern.ACKNOWLEDGED;
        wc = wc.continueOnError(true);

        WriteResult writeResult = this.dbCollection.insert(dbObject, wc);
    }


}
