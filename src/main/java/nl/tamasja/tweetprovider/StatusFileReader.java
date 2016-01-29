package nl.tamasja.tweetprovider;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import nl.tamasja.tools.log.ILog;
import nl.tamasja.twitter.Tweet;
import twitter4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * TIS 7-8-2014.21:25
 */
public class StatusFileReader {

    protected ILog log;

    public StatusFileReader(ILog log) {
        this.log = log;
    }

    public List<Tweet> readFile(String fileLocation, int limit, String fromTweetId) {

        long startTime = System.currentTimeMillis();

        int errors = 0;
        int added = 0;
        int ignored = 0;
        int skipped = 0;

        List<Tweet> tweets = new ArrayList<Tweet>();

        try {

            //Read gzFile
            InputStream inputStream = new GZIPInputStream(new FileInputStream(fileLocation));
            Reader decoder = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(decoder);

            String status;

            while ((status = br.readLine()) != null) {

                // Ignore garbage and log output lines, all statuses start with JSON bracket
                if (!status.equals("") && status.charAt(0) == '{') {
                    try {
                        JSONObject jsonObject = new JSONObject(status);

                        //We use created_at as an indicator that this is a Tweet.
                        if (jsonObject.has("created_at")) {

                            Status statusObject = TwitterObjectFactory.createStatus(status);

                            Tweet tweet = this.getTweetObjectFromStatus(statusObject);

                            if(fromTweetId != null) {

                                if(fromTweetId.equals(tweet.getId())) {
                                    this.log.write("StatusFileReader - Scanner pickup from "+fromTweetId);
                                    fromTweetId = null;
                                } else {
                                    skipped++;
                                }

                                continue;
                            }


                            added++;
                            tweets.add(tweet);

                            if(limit > 0 && added >= limit) {
                                break;
                            }


                        } else {
                            ignored++;
                        }

                    } catch (JSONException e) {
                        this.log.write("Exception in StatusFileReader: Json Parse Failure on: " + status+", "+e.getMessage());
                    }
                } else {
                    ignored++;
                }
            }

            br.close();
            decoder.close();
            inputStream.close();



        } catch (Exception e) {
            this.log.write( "Exception in StatusFileReader: Error Reading File: "+e.getClass().getName() + ": " + e.getMessage() );
        }

        long runTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-startTime);

        double ops = ((double) added / (double) Math.max(runTimeSeconds,1));

        this.log.write("StatusFileReader - "+fileLocation+" processed in "+runTimeSeconds+"s. "+added+" ok / "+errors+" errors / "+ignored+" ignored / "+skipped+" skipped. "+ops+" ops. Limit: "+limit+", Fetch: "+tweets.size());

        return tweets;
    }

    private Tweet getTweetObjectFromStatus(Status status) {

        Tweet tweet = new Tweet();
        tweet.setId(Long.toString(status.getId()));
        tweet.setText(status.getText());
        tweet.setCreatedAt(status.getCreatedAt());

        tweet.setFavCount(status.getFavoriteCount());


        User user = status.getUser();

        tweet.setUserId(user.getId());
        tweet.setUserName(user.getName());
        tweet.setUserScreenName(user.getScreenName());


        HashtagEntity[] hashtagEntities = status.getHashtagEntities();
        List<String> hashtags = new ArrayList<String>();

        for (HashtagEntity hashtagEntity : hashtagEntities) {
            hashtags.add(hashtagEntity.getText());
        }

        tweet.setHashTags(hashtags.toArray(new String[hashtags.size()]));

        GeoLocation geoLocation = status.getGeoLocation();
        if (geoLocation != null) {
            double[] coordinates = {geoLocation.getLongitude(), geoLocation.getLatitude()};
            tweet.setCoordinates(coordinates);
        }

        return tweet;
    }
}
