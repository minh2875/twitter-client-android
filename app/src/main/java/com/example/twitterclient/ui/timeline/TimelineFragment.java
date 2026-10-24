package com.example.twitterclient.ui.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.twitterclient.R;
import com.example.twitterclient.model.Tweet;
import com.example.twitterclient.repository.TweetRepository;
import java.util.List;
import android.widget.Button;
import com.example.twitterclient.MainActivity;
import com.example.twitterclient.auth.SessionManager;

public class TimelineFragment extends Fragment {

    private TweetAdapter adapter;
    private RecyclerView recyclerView;
    private TweetRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new TweetRepository(requireContext());
        adapter = new TweetAdapter();
        adapter.setOnAuthorClickListener(username -> {
            ((MainActivity) requireActivity())
                    .showUserProfile(username);
        });
        recyclerView = view.findViewById(R.id.timeline_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.compose_button).setOnClickListener(v -> showComposeDialog());
        loadTweets();
        Button logoutButton = view.findViewById(R.id.buttonLogout);

        logoutButton.setOnClickListener(v -> {
            // Delete the locally stored JWT and username
            SessionManager session =
                    new SessionManager(requireContext());

            session.logout();

            // Return to the login screen
            ((MainActivity) requireActivity()).showLogin();
        });
        Button profileButton =
                view.findViewById(R.id.buttonProfile);

        profileButton.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).showProfile();
        });
    }

    private void loadTweets() {
        repository.getTweets(new TweetRepository.Callback<List<Tweet>>() {
            @Override
            public void onSuccess(List<Tweet> tweets) {
                if (!isAdded() || recyclerView == null) return;
                adapter.replaceTweets(tweets);
            }
            @Override
            public void onError(String message) { showMessage(message); }
        });
    }

    private void showComposeDialog() {
        final EditText input = new EditText(requireContext());
        input.setHint(R.string.compose_hint);
        input.setMinLines(3);
        input.setMaxLines(5);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.new_tweet)
                .setView(input)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .setPositiveButton(R.string.post, null)
                .create();

        dialog.setOnShowListener(v -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(button -> {
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        input.setError(getString(R.string.empty_tweet));
                        return;
                    }
                    if (text.length() > 280) {
                        input.setError(getString(R.string.tweet_too_long));
                        return;
                    }
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    repository.createTweet(text,
                            new TweetRepository.Callback<Tweet>() {
                        @Override
                        public void onSuccess(Tweet tweet) {
                            if (!isAdded()) return;
                            dialog.dismiss();
                            showMessage(getString(R.string.post_saved));
                            loadTweets();
                            if (recyclerView != null) recyclerView.scrollToPosition(0);
                        }
                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            showMessage(message);
                        }
                    });
                }));
        dialog.show();
    }

    private void showMessage(String text) {
        if (isAdded()) Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        recyclerView = null;
        super.onDestroyView();
    }
}
