package nl.tamasja.connector;

import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;

/**
 * TIS 24-8-2014.11:37
 */
public class ElasticSearchConnector {

    protected ILog log;
    protected static TransportClient client;
    protected String[] servers;

    public ElasticSearchConnector(ILog log, String[] servers) {
        this.log = log;
        this.servers = servers;
    }


    public void clearAll() throws IOException {
        this.deleteIndex();
        this.createIndex();
    }

    public void indexTweet(Tweet tweet) throws IOException {
        IndexResponse response = this.makeClient().prepareIndex("twitter", "tweet", tweet.getId())
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", tweet.getId())
                        .field("text", tweet.getText())
                        .field("created_at", tweet.getCreatedAt())
                        .field("hashtags", tweet.getHashTags())
                        .field("userId", tweet.getUserId())
                        .field("userName", tweet.getUserName())
                        .field("userScreenName", tweet.getUserName())
                        .endObject()
                )
                .execute()
                .actionGet();
    }

    public long count() {
        CountResponse response = this.makeClient().prepareCount("twitter")
                .setQuery(QueryBuilders.termQuery("_type", "tweet"))
                .execute()
                .actionGet();

        return response.getCount();
    }


    protected Client makeClient() {
        // on startup
        if (ElasticSearchConnector.client == null) {

            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("cluster.name", "elasticsearchtwitter").build();

            TransportClient client = new TransportClient(settings);


            for (String host : this.servers) {
                client.addTransportAddress(new InetSocketTransportAddress(host, 9300));
            }

            ElasticSearchConnector.client = client;

        }

        return ElasticSearchConnector.client;
    }

    public void closeClient() {
        if (ElasticSearchConnector.client != null) {
            ElasticSearchConnector.client.close();
            ElasticSearchConnector.client = null;
        }
    }

    public void refresh() {
        this.makeClient().admin().indices().refresh(new RefreshRequest("twitter"));
    }


    protected boolean indexExist(String index) {
        ActionFuture<IndicesExistsResponse> exists = this.makeClient().admin().indices()
                .exists(new IndicesExistsRequest(index));
        IndicesExistsResponse actionGet = exists.actionGet();

        return actionGet.isExists();
    }

    protected void deleteIndex() {

        if (this.indexExist("twitter")) {
            this.makeClient().admin().indices().delete(new DeleteIndexRequest("twitter")).actionGet();
        }

    }

    protected void createIndex() throws IOException {

        if (this.indexExist("twitter")) {
            return;
        }

        this.makeClient().admin().indices().prepareCreate("twitter").execute().actionGet();

        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("tweet")
                .startObject("properties")
                .startObject("id").field("type", "long").endObject()
                .startObject("text").field("type", "string").endObject()
                .startObject("created_at").field("type", "date").field("format", "dateTime").endObject()
                .startObject("hashtags").field("type", "string").endObject()
                .startObject("userId").field("type", "long").endObject()
                .startObject("userName").field("type", "string").endObject()
                .startObject("userScreenName").field("type", "string").endObject()
                .endObject()
                .endObject()
                .endObject();


        PutMappingResponse putMappingResponse = this.makeClient().admin().indices()
                .preparePutMapping("twitter")
                .setType("tweet")
                .setSource(mapping)
                .execute().actionGet();
    }

    public SearchResponse searchQuery(SearchRequestBuilder searchRequestBuilder) {

        return searchRequestBuilder.execute().actionGet();
    }

    public SearchRequestBuilder fetchRequestBuilder() {
        return this.makeClient().prepareSearch("twitter")
                .setTypes("tweet")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
    }

    public int getNumberOfNodes() {
        return this.makeClient().admin().cluster().health(new ClusterHealthRequest()).actionGet().getNumberOfNodes();
    }

}
