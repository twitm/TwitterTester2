package nl.tamasja.queryfactory;

import com.mongodb.BasicDBObjectBuilder;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TIS 12-9-2014.15:49
 */
public class MongoQueryFactory extends ABaseFactory {

    protected BasicDBObjectBuilder[] filters = {
            BasicDBObjectBuilder.start("hashtags", "android"),
            BasicDBObjectBuilder.start("hashtags", "news"),
            BasicDBObjectBuilder.start("hashtags", "love"),
            BasicDBObjectBuilder.start("hashtags", "fail"),
            BasicDBObjectBuilder.start("hashtags", "teamfollowback"),
            BasicDBObjectBuilder.start("hashtags", "gameinsight"),
            BasicDBObjectBuilder.start("hashtags", "androidgames"),
            BasicDBObjectBuilder.start("hashtags", "tfbjp"),
            BasicDBObjectBuilder.start("hashtags", "retweet"),
            BasicDBObjectBuilder.start("hashtags", "nowplaying"),
            BasicDBObjectBuilder.start("hashtags", "ipad"),
            BasicDBObjectBuilder.start("hashtags", "votewhatmakesyoubeautiful"),
            BasicDBObjectBuilder.start("hashtags", "music"),
            BasicDBObjectBuilder.start("hashtags", "grammys"),

            BasicDBObjectBuilder.start("userName", "BarackObama"),
            BasicDBObjectBuilder.start("userName", "twitter"),
            BasicDBObjectBuilder.start("userName", "YouTube"),
            BasicDBObjectBuilder.start("userName", "laura"),
            BasicDBObjectBuilder.start("userName", "love"),
            BasicDBObjectBuilder.start("userName", "david"),
            BasicDBObjectBuilder.start("userName", "alex"),
            BasicDBObjectBuilder.start("userName", "nicole"),
            BasicDBObjectBuilder.start("userName", "ArianaGrande"),
            BasicDBObjectBuilder.start("userName", "CNN"),
            BasicDBObjectBuilder.start("userName", "VENETHIS"),

            BasicDBObjectBuilder.start("userName", "Urban Dictionary"),
            BasicDBObjectBuilder.start("userName", "Noticias Venezuela"),
            BasicDBObjectBuilder.start("userName", "Robert von Heeren"),

            BasicDBObjectBuilder.start("userId", "395641908"),
            BasicDBObjectBuilder.start("userId", "84624158"),
            BasicDBObjectBuilder.start("userId", "59804598"),


            BasicDBObjectBuilder.start("created_at", BasicDBObjectBuilder
                    .start("$gte", DatatypeConverter.parseDateTime("2012-01-01T00:00:00.000Z").getTime())
                    .add("$lte", DatatypeConverter.parseDateTime("2014-01-01T10:00:00.000Z").getTime()).get()),
            BasicDBObjectBuilder.start("created_at", BasicDBObjectBuilder
                    .start("$gte", DatatypeConverter.parseDateTime("2013-03-12T18:10:30.000Z").getTime())
                    .add("$lte", DatatypeConverter.parseDateTime("2013-04-12T10:00:00.000Z").getTime()).get()),
            BasicDBObjectBuilder.start("created_at", BasicDBObjectBuilder
                    .start("$gte", DatatypeConverter.parseDateTime("2013-01-01T00:00:00.000Z").getTime())
                    .add("$lte", DatatypeConverter.parseDateTime("2013-03-13T00:00:00.000Z").getTime()).get()),
            BasicDBObjectBuilder.start("created_at", BasicDBObjectBuilder
                    .start("$gte", DatatypeConverter.parseDateTime("2013-02-11T12:12:12.000Z").getTime())
                    .add("$lte", DatatypeConverter.parseDateTime("2013-06-25T13:33:33.000Z").getTime()).get()),
    };

    protected BasicDBObjectBuilder getRandomFilter() {
        Random random = new Random();
        return this.filters[random.nextInt(this.filters.length)];
    }

    public BasicDBObjectBuilder[] getFilters() {
        return this.filters;
    }

    public BasicDBObjectBuilder getRandomPhraseQuery() {


        return BasicDBObjectBuilder.start("$text", BasicDBObjectBuilder.start("$search", this.getRandomPhrase()).get());

    }

    public BasicDBObjectBuilder getRandomFilteredPhraseQuery() {

        return this.getRandomFilter().add("$text", BasicDBObjectBuilder.start("$search", this.getRandomPhrase()).get());

    }

    public List<BasicDBObjectBuilder> getPhraseQueryList() {
        List<BasicDBObjectBuilder> basicDBObjectBuilderArrayList = new ArrayList<BasicDBObjectBuilder>();

        String[] terms = this.getQueryStrings();

        for (String term : terms) {
            basicDBObjectBuilderArrayList.add(BasicDBObjectBuilder.start("$text", BasicDBObjectBuilder.start("$search", term).get()));
        }

        return basicDBObjectBuilderArrayList;
    }

    public List<BasicDBObjectBuilder> getFilteredPhraseQueryList() {
        List<BasicDBObjectBuilder> basicDBObjectBuilderArrayList = new ArrayList<BasicDBObjectBuilder>();

        String[] terms = this.getQueryStrings();
        BasicDBObjectBuilder[] filters = this.getFilters();
        for (String term : terms) {
            for (BasicDBObjectBuilder filter : filters) {
                basicDBObjectBuilderArrayList.add(filter.add("$text", BasicDBObjectBuilder.start("$search", term).get()));
            }

        }

        return basicDBObjectBuilderArrayList;
    }
}
