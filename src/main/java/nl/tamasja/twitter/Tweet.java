package nl.tamasja.twitter;

import java.util.Date;

/**
 * TIS 17-6-2014.11:34
 */
public class Tweet {

    protected String id;
    protected String text;
    protected Date createdAt;
    protected String[] hashTags;
    protected double[] coordinates;

    protected int favCount = 0;

    protected long userId;
    protected String userName;
    protected String userScreenName;

    protected long tweetN = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String[] getHashTags() {
        return hashTags;
    }
    public void setHashTags(String[] hashTags) {
        this.hashTags = hashTags;
    }

    public double[] getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public long getTweetN() { return tweetN; }
    public void setTweetN(long tweetN) { this.tweetN = tweetN; }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }
}
