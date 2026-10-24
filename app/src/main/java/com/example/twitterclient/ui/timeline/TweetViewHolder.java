package com.example.twitterclient.ui.timeline;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.twitterclient.R;
import com.example.twitterclient.model.Tweet;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    private final TextView displayName;
    private final TextView username;
    private final TextView content;

    public TweetViewHolder(@NonNull View itemView) {
        super(itemView);
        displayName = itemView.findViewById(R.id.tweet_display_name);
        username = itemView.findViewById(R.id.tweet_username);
        content = itemView.findViewById(R.id.tweet_content);

    }

    public void bind(Tweet tweet) {
        displayName.setText(tweet.getDisplayName());
        username.setText("@" + tweet.getUsername());
        content.setText(tweet.getText());
    }

    public void setOnAuthorClickListener(
            View.OnClickListener listener
    ) {
        displayName.setOnClickListener(listener);
        username.setOnClickListener(listener);
    }

}
