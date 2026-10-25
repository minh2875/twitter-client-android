package com.example.twitterclient.ui.timeline;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.twitterclient.R;
import com.example.twitterclient.model.Tweet;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    private final TextView displayName;
    private final TextView username;
    private final TextView content;
    private final LinearLayout btnLikeContainer;
    private final ImageView ivLikeIcon;
    private final TextView tvLikeCount;

    public TweetViewHolder(@NonNull View itemView) {
        super(itemView);
        displayName = itemView.findViewById(R.id.tweet_display_name);
        username = itemView.findViewById(R.id.tweet_username);
        content = itemView.findViewById(R.id.tweet_content);

        btnLikeContainer = itemView.findViewById(R.id.btn_like_container);
        ivLikeIcon = itemView.findViewById(R.id.iv_like_icon);
        tvLikeCount = itemView.findViewById(R.id.tv_like_count);
    }

    public void bind(Tweet tweet, OnLikeClickListener likeClickListener) {
        displayName.setText(tweet.getDisplayName());
        username.setText("@" + tweet.getUsername());
        content.setText(tweet.getText());

        tvLikeCount.setText(String.valueOf(tweet.getLikeCount()));
        updateLikeUI(tweet.isLikedByMe());

        if (btnLikeContainer != null) {
            btnLikeContainer.setOnClickListener(v -> {
                if (likeClickListener != null) {
                    likeClickListener.onLikeClick(tweet);
                }
            });
        }
    }

    public void updateLikeUI(boolean likedByMe) {
        if (likedByMe) {
            ivLikeIcon.setImageResource(R.drawable.ui_icon_heart);
            ivLikeIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F91880")));
            tvLikeCount.setTextColor(Color.parseColor("#F91880"));
        } else {
            ivLikeIcon.setImageResource(R.drawable.ui_icon_heart);
            ivLikeIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#536471")));
            tvLikeCount.setTextColor(Color.parseColor("#536471"));
        }
    }

    public void setOnAuthorClickListener(View.OnClickListener listener) {
        displayName.setOnClickListener(listener);
        username.setOnClickListener(listener);
    }

    public interface OnLikeClickListener {
        void onLikeClick(Tweet tweet);
    }
}