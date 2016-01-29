package nl.tamasja.tester;

import nl.tamasja.data.ProfiledQueryResult;
import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.log.ILog;

import java.util.List;

/**
 * TIS 1-9-2014.18:02
 */
public class ResponseTimeFilteredPhraseQueryTester extends ResponseTimePhraseQueryTester {

    public ResponseTimeFilteredPhraseQueryTester(ILog log, ILog resultLog, ISearchProvider searchProvider) {
        super(log, resultLog, searchProvider);
    }


    protected String getTestName() {
        return "ResponseTimeFilteredPhraseQueryTester";
    }

    protected List<ProfiledQueryResult> runProfiledQueries() {
        return this.searchProvider.runTimedFilteredPhraseQueries();
    }


}
