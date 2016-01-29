package nl.tamasja.queryfactory;


import nl.tamasja.connector.ElasticSearchConnector;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TIS 28-8-2014.16:47
 */
public class ElasticSearchQueryFactory extends ABaseFactory {

    protected ElasticSearchConnector elasticSearchConnector;

    protected FilterBuilder[] filters = {
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "android")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "news")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "love")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "fail")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "teamfollowback")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "gameinsight")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "androidgames")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "tfbjp")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "retweet")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "nowplaying")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "ipad")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "votewhatmakesyoubeautiful")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "music")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("hashtags", "grammys")),

            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "BarackObama")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "twitter")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "YouTube")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "laura")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "love")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "david")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "alex")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "nicole")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "ArianaGrande")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "CNN")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userName", "VENETHIS")),

            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userScreenName", "Urban Dictionary")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userScreenName", "Noticias Venezuela")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userScreenName", "Robert von Heeren")),

            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userId", "395641908")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userId", "84624158")),
            FilterBuilders.boolFilter().must(FilterBuilders.termFilter("userId", "59804598")),

            FilterBuilders.boolFilter().must(FilterBuilders.rangeFilter("created_at").from("2012-01-01T00:00:00.000Z").to("2014-01-01T10:00:00.000Z")),
            FilterBuilders.boolFilter().must(FilterBuilders.rangeFilter("created_at").from("2013-03-12T18:10:30.000Z").to("2013-04-12T10:00:00.000Z")),
            FilterBuilders.boolFilter().must(FilterBuilders.rangeFilter("created_at").from("2013-01-01T00:00:00.000Z").to("2013-03-13T00:00:00.000Z")),
            FilterBuilders.boolFilter().must(FilterBuilders.rangeFilter("created_at").from("2013-02-11T12:12:12.000Z").to("2013-06-25T13:33:33.000Z")),
    };

    public ElasticSearchQueryFactory(ElasticSearchConnector elasticSearchConnector) {
        this.elasticSearchConnector = elasticSearchConnector;

    }


    protected FilterBuilder[] getFilters() {
        return this.filters;
    }

    protected FilterBuilder getRandomFilter() {
        Random random = new Random();
        return this.filters[random.nextInt(this.filters.length)];
    }

    public SearchRequestBuilder getRandomPhraseQuery() {
        return this.elasticSearchConnector.fetchRequestBuilder()
                .setQuery(QueryBuilders.matchQuery("text", this.getRandomPhrase()));
    }

    public SearchRequestBuilder getRandomFilteredPhraseQuery() {
        return this.elasticSearchConnector.fetchRequestBuilder().setQuery(
                QueryBuilders.filteredQuery(
                        QueryBuilders.matchQuery("text", this.getRandomPhrase()),
                        this.getRandomFilter()
                )
        );
    }

    public List<SearchRequestBuilder> getPhraseQueryList() {
        List<SearchRequestBuilder> searchRequestBuilderList = new ArrayList<SearchRequestBuilder>();

        String[] terms = this.getQueryStrings();

        for(String term : terms) {
            searchRequestBuilderList.add(this.elasticSearchConnector.fetchRequestBuilder()
                    .setQuery(QueryBuilders.matchQuery("text", term)));
        }

        return searchRequestBuilderList;

    }

    public List<SearchRequestBuilder> getFilteredPhraseQueryList() {
        List<SearchRequestBuilder> searchRequestBuilderList = new ArrayList<SearchRequestBuilder>();

        String[] terms = this.getQueryStrings();
        FilterBuilder[] filterBuilders = this.getFilters();

        for(String term : terms) {
            for(FilterBuilder filterBuilder : filterBuilders) {

                searchRequestBuilderList.add(this.elasticSearchConnector.fetchRequestBuilder().setQuery(
                        QueryBuilders.filteredQuery(
                                QueryBuilders.matchQuery("text", term),
                                filterBuilder
                        )
                ));


            }
        }

        return searchRequestBuilderList;

    }

}
