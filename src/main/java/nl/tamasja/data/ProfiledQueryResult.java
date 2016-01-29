package nl.tamasja.data;

/**
 * TIS 2-9-2014.16:36
 */
public class ProfiledQueryResult {


    protected long numFound;
    protected long runDuration;
    protected String queryString;

    public ProfiledQueryResult(long numFound, long runDuration, String queryString) {
        this.numFound = numFound;
        this.runDuration = runDuration;
        this.queryString = queryString;
    }

    public long getNumFound() {
        return numFound;
    }

    public long getRunDuration() {
        return runDuration;
    }

    public String getQueryString() {
        return queryString;
    }
}
