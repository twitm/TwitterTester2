package nl.tamasja.connector;

import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TIS 30-7-2014.16:36
 */
public class SolrConnector {


    protected ILog log;
    protected SolrServer solrServer;

    protected String[] servers;

    public SolrConnector(ILog log, String[] servers) {
        this.log = log;
        this.servers = servers;
    }

    protected SolrServer getSolrServer() throws MalformedURLException {
        if(this.solrServer == null) {

            List<String> addresses = new ArrayList<String>();


            for (String host : this.servers) {
                addresses.add("http://"+host+":8983/solr");
            }

            this.solrServer = new LBHttpSolrServer(addresses.toArray(new String[addresses.size()]));

        }

        return this.solrServer;
    }

    public void indexTweet(Tweet tweet) throws IOException, SolrServerException {

        SolrServer solrServer = this.getSolrServer();

        SolrInputDocument document = new SolrInputDocument();

        document.addField("id", tweet.getId());
        document.addField("text_t", tweet.getText());

        document.addField("createdAt_dt", tweet.getCreatedAt());
        document.addField("hashtags_ss", tweet.getHashTags());

        document.addField("favCount_i",tweet.getFavCount());

        document.addField("userId_l",tweet.getUserId());
        document.addField("userName_s",tweet.getUserName());
        document.addField("userScreenName_s",tweet.getUserScreenName());


        UpdateResponse response = solrServer.add(document);

        if(response.getStatus() != 0) {
            this.log.write("[WARNING] indexTweet got non-zero status "+response.getStatus()+": "+response.getResponse());
        }

        //solrServer.commit();

    }

    public void commit() throws IOException, SolrServerException {
        this.getSolrServer().commit();
    }

    public void clearAll() throws IOException, SolrServerException {
        this.getSolrServer().deleteByQuery("*:*");
    }

    public void clearUntil(int n) throws IOException, SolrServerException {
        this.getSolrServer().deleteByQuery("tweetN_i:[0 TO "+n+"]");
    }

    public long count() throws MalformedURLException, SolrServerException {
        SolrQuery q = new SolrQuery("*:*");
        q.setRows(0);
        return this.getSolrServer().query(q).getResults().getNumFound();
    }

    public QueryResponse searchQuery(SolrQuery solrQuery) throws MalformedURLException, SolrServerException {
        return this.getSolrServer().query(solrQuery);
    }




}
