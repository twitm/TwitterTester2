package nl.tamasja.searchprovider;

import nl.tamasja.data.ProfiledQueryResult;
import nl.tamasja.twitter.Tweet;

import java.util.List;

/**
 * TIS 21-8-2014.14:49
 */
public interface ISearchProvider {

    public String getSearchProviderName();

    public long countTotal();
    public void commit();
    public void clearAll();
    public void close();

    public void indexTweet(Tweet tweet);
    public long executeRandomPhraseQuery(boolean limitResults);
    public long executeRandomFilteredPhraseQuery(boolean limitResults);

    List<ProfiledQueryResult> runTimedPhraseQueries();
    List<ProfiledQueryResult> runTimedFilteredPhraseQueries();
    public void deploy();
    public void remove();

    public long getTotalMemoryUsage();
    public long getTotalSwapUsage();
    public long getTotalDiskUsage();
    public long getNodeNumber();
    public long getLiveNodeNumber();
}
