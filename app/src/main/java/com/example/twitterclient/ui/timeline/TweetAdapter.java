package com.example.twitterclient.ui.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.twitterclient.R;
import com.example.twitterclient.model.Tweet;
import com.example.twitterclient.repository.TweetRepository;
import java.util.ArrayList;
import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetViewHolder> {
    private final List<Tweet> tweets = new ArrayList<>();

    public interface OnAuthorClickListener {
        void onAuthorClick(String username);
    }

    private OnAuthorClickListener authorClickListener;

    public void setOnAuthorClickListener(OnAuthorClickListener listener) {
        this.authorClickListener = listener;
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        // Truyền cả tweet và sự kiện click Like vào hàm bind
        holder.bind(tweet, clickedTweet -> {
            // Gọi Repository để thực hiện API Like / Unlike
            TweetRepository repo = new TweetRepository(holder.itemView.getContext());

            repo.toggleLike(clickedTweet.getId(), response -> {
                // Cập nhật lại trạng thái Like và số đếm sau khi server phản hồi thành công
                boolean liked = response.optBoolean("liked", false);
                int count = response.optInt("likeCount", clickedTweet.getLikeCount());

                clickedTweet.setLikedByMe(liked);
                clickedTweet.setLikeCount(count);

                // Cập nhật lại giao diện nút Like
                holder.updateLikeUI(liked);
                notifyItemChanged(position);
            }, error -> {
                android.widget.Toast.makeText(holder.itemView.getContext(), "Không thể thích bài viết", android.widget.Toast.LENGTH_SHORT).show();
            });
        });

        holder.setOnAuthorClickListener(v -> {
            if (authorClickListener != null) {
                authorClickListener.onAuthorClick(tweet.getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void replaceTweets(List<Tweet> newTweets) {
        tweets.clear();
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }
}