package com.example.twitterclient.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {
    private final int id;
    private final String username;
    private final String displayName;
    private final String text;
    private int likeCount;
    private boolean likedByMe;

    public Tweet(JSONObject json) throws JSONException {
        this.id = json.optInt("id", json.optInt("_id", 0));
        this.username = json.optString("username", "");
        this.displayName = json.optString("displayName", json.optString("display_name", username));
        this.text = json.optString("text", json.optString("content", ""));
        this.likeCount = json.optInt("likeCount", json.optInt("like_count", 0));
        this.likedByMe = json.optBoolean("likedByMe", json.optBoolean("liked_by_me", false));
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getText() {
        return text;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }
}