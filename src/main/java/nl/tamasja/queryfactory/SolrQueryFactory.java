package nl.tamasja.queryfactory;

import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TIS 21-8-2014.11:57
 */
public class SolrQueryFactory extends ABaseFactory {

    protected SolrQuery[] filteredQueryList;

    protected String filters[] = {
            "hashtags_ss:android",
            "hashtags_ss:news",
            "hashtags_ss:love",
            "hashtags_ss:fail",
            "hashtags_ss:teamfollowback",
            "hashtags_ss:gameinsight",
            "hashtags_ss:androidgames",
            "hashtags_ss:tfbjp",
            "hashtags_ss:retweet",
            "hashtags_ss:nowplaying",
            "hashtags_ss:ipad",
            "hashtags_ss:votewhatmakesyoubeautiful",
            "hashtags_ss:music",
            "hashtags_ss:grammys",

            "userName_s:BarackObama",
            "userName_s:twitter",
            "userName_s:YouTube",
            "userName_s:laura",
            "userName_s:love",
            "userName_s:david",
            "userName_s:alex",
            "userName_s:nicole",
            "userName_s:ArianaGrande",
            "userName_s:CNN",
            "userName_s:VENETHIS",

            "userScreenName_s:Urban Dictionary",
            "userScreenName_s:Noticias Venezuela",
            "userScreenName_s:Robert von Heeren",

            "userId_l:395641908",
            "userId_l:84624158",
            "userId_l:59804598",

            "createdAt_dt: [2012-01-01T00:00:00Z TO 2014-01-01T10:00:00Z]",
            "createdAt_dt: [2013-03-12T18:10:30Z TO 2013-04-12T10:00:00Z]",
            "createdAt_dt: [2013-01-01T00:00:00Z TO 2013-03-13T00:00:00Z]",
            "createdAt_dt: [2013-02-11T12:12:12Z TO 2013-06-25T13:33:33Z]",

    };


    public String[] getFilters() {
        return this.filters;
    }


    protected String getRandomFilter() {
        Random random = new Random();
        return this.filters[random.nextInt(this.filters.length)];
    }

    public SolrQuery getRandomPhraseQuery() {
        return new SolrQuery("text_t:"+ this.getRandomPhrase());
    }

    public SolrQuery getRandomFilteredPhraseQuery() {
        SolrQuery solrQuery = new SolrQuery("text_t:"+this.getRandomPhrase());
        solrQuery.addFilterQuery(this.getRandomFilter());
        return solrQuery;
    }
    public List<SolrQuery> getPhraseQueryList() {
        List<SolrQuery> solrQueryList = new ArrayList<SolrQuery>();

        String[] terms = this.getQueryStrings();

        for(String term : terms) {
            solrQueryList.add(new SolrQuery("text_t:"+term));
        }

        return solrQueryList;
    }

    public List<SolrQuery> getFilteredPhraseQueryList() {
        List<SolrQuery> solrQueryList = new ArrayList<SolrQuery>();

        String[] terms = this.getQueryStrings();
        String[] filters = this.getFilters();

        for(String term : terms) {
            for(String filter : filters) {
                SolrQuery solrQuery = new SolrQuery("text_t:"+term);
                solrQuery.addFilterQuery(filter);
                solrQueryList.add(solrQuery);
            }
        }

        return  solrQueryList;
    }
}
