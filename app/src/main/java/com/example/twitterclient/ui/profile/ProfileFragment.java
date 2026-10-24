
package com.example.twitterclient.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.twitterclient.MainActivity;
import com.example.twitterclient.R;
import com.example.twitterclient.model.Tweet;
import com.example.twitterclient.repository.ProfileRepository;
import com.example.twitterclient.ui.timeline.TweetAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private ProfileRepository repository;
    private TweetAdapter tweetAdapter;

    private TextView profileTitle;
    private TextView postCount;
    private TextView displayName;
    private TextView username;
    private TextView bio;
    private TextView joinedDate;
    private TextView emptyMessage;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );
    }


    private static final String ARG_USERNAME = "profile_username";

    public static ProfileFragment forUsername(String username) {
        ProfileFragment fragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        repository = new ProfileRepository(requireContext());

        // Find the views defined in fragment_profile.xml
        profileTitle = view.findViewById(R.id.textProfileTitle);
        postCount = view.findViewById(R.id.textProfilePostCount);
        displayName = view.findViewById(R.id.textDisplayName);
        username = view.findViewById(R.id.textUsername);
        bio = view.findViewById(R.id.textBio);
        joinedDate = view.findViewById(R.id.textJoinedDate);
        emptyMessage = view.findViewById(R.id.textProfileEmpty);

        // Reuse the existing timeline TweetAdapter.
        RecyclerView recycler =
                view.findViewById(R.id.recyclerProfilePosts);

        tweetAdapter = new TweetAdapter();
        tweetAdapter.setOnAuthorClickListener(handle -> {
            ((MainActivity) requireActivity())
                    .showUserProfile(handle);
        });

        recycler.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recycler.setAdapter(tweetAdapter);
        recycler.setNestedScrollingEnabled(false);

        // Back button returns to Home.
        view.findViewById(R.id.buttonBackProfile)
                .setOnClickListener(v -> {
                    ((MainActivity) requireActivity())
                            .showTimeline();
                });

        // Editing will be implemented with PATCH /api/users/me.
        Button editButton =
                view.findViewById(R.id.buttonEditProfile);

        editButton.setOnClickListener(v -> showEditProfileDialog());

        // Load data from the actual Express backend.

        String selectedUsername = null;

        if (getArguments() != null) {
            selectedUsername =
                    getArguments().getString(ARG_USERNAME);
        }

        if (selectedUsername == null) {
            // Opened using the Profile button:
            // show our own authenticated profile.
            loadMyProfile();
        } else {
            // Opened by tapping a tweet's author.
            loadProfileByUsername(selectedUsername);
        }

    }

    private void loadMyProfile() {

        repository.getMyProfile(
                response -> {
                    if (!isAdded()) return;

                    try {
                        JSONObject user =
                                response.getJSONObject("user");

                        int userId = user.getInt("id");

                        String name =
                                user.optString("displayName", "");

                        String handle =
                                user.optString("username", "");

                        String description =
                                user.optString("bio", "");

                        String createdAt =
                                user.optString("createdAt", "");

                        // Display profile information.
                        profileTitle.setText(name);
                        displayName.setText(name);
                        username.setText("@" + handle);

                        // Only show biography if one exists.
                        if (!description.isEmpty()
                                && !description.equals("null")) {
                            bio.setText(description);
                            bio.setVisibility(View.VISIBLE);
                        } else {
                            bio.setVisibility(View.GONE);
                        }

                        joinedDate.setText(
                                formatJoinedDate(createdAt)
                        );

                        // Now retrieve this user's posts.
                        loadUserPosts(userId, name, handle);

                    } catch (Exception error) {
                        showError("Invalid profile response");
                    }
                },
                error -> {
                    if (!isAdded()) return;

                    if (error.networkResponse != null
                            && error.networkResponse.statusCode == 401) {
                        showError("Session expired. Please log in again.");
                    } else {
                        showError("Cannot load profile from server");
                    }
                }
        );
    }

    private void loadUserPosts(
            int userId,
            String name,
            String handle
    ) {
        repository.getUserPosts(
                userId,
                response -> {
                    if (!isAdded()) return;

                    try {
                        JSONArray posts =
                                response.getJSONArray("posts");

                        List<Tweet> tweets = new ArrayList<>();

                        for (int i = 0; i < posts.length(); i++) {

                            JSONObject post =
                                    posts.getJSONObject(i);

                            String postUsername =
                                    post.getString("username");

                            String content =
                                    post.getString("content");

                            // Your existing Tweet model constructor:
                            // Tweet(displayName, username, text)
                            tweets.add(new Tweet(
                                    name,
                                    postUsername,
                                    content
                            ));
                        }

                        tweetAdapter.replaceTweets(tweets);

                        int count = tweets.size();

                        postCount.setText(
                                count + (count == 1 ? " post" : " posts")
                        );

                        emptyMessage.setVisibility(
                                tweets.isEmpty()
                                        ? View.VISIBLE
                                        : View.GONE
                        );

                    } catch (Exception error) {
                        showError("Invalid posts response");
                    }
                },
                error -> {
                    if (!isAdded()) return;
                    showError("Cannot load user posts");
                }
        );



    }

    private String formatJoinedDate(String dateString) {
        try {
            SimpleDateFormat input = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                    Locale.US
            );

            Date date = input.parse(dateString);

            if (date != null) {
                SimpleDateFormat output = new SimpleDateFormat(
                        "MMMM yyyy",
                        Locale.US
                );

                return "Joined " + output.format(date);
            }

        } catch (Exception ignored) {
            // Fall through to a neutral message.
        }

        return "Joined date unavailable";
    }

    private void showError(String message) {
        if (!isAdded()) return;

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    private void showEditProfileDialog() {

        // Container for the two input fields
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 8);

        // Display name input
        EditText nameInput = new EditText(requireContext());
        nameInput.setHint("Display name");
        nameInput.setSingleLine(true);
        nameInput.setText(displayName.getText());
        nameInput.setFilters(new android.text.InputFilter[]{
                new android.text.InputFilter.LengthFilter(100)
        });

        // Bio input
        EditText bioInput = new EditText(requireContext());
        bioInput.setHint("Bio");
        bioInput.setMinLines(3);
        bioInput.setMaxLines(5);
        bioInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );
        bioInput.setText(bio.getText());
        bioInput.setFilters(new android.text.InputFilter[]{
                new android.text.InputFilter.LengthFilter(280)
        });

        layout.addView(nameInput);
        layout.addView(bioInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Edit profile")
                .setView(layout)
                .setNegativeButton("Cancel", (d, which) -> {})
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String newName =
                                nameInput.getText().toString().trim();

                        String newBio =
                                bioInput.getText().toString().trim();

                        if (newName.isEmpty()) {
                            nameInput.setError("Display name is required");
                            return;
                        }

                        // Prevent duplicate save requests
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(false);

                        repository.updateMyProfile(
                                newName,
                                newBio,
                                response -> {
                                    if (!isAdded()) {
                                        dialog.dismiss();
                                        return;
                                    }

                                    // Refresh profile using server data
                                    dialog.dismiss();
                                    loadMyProfile();

                                    Toast.makeText(
                                            requireContext(),
                                            "Profile updated",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                },
                                error -> {
                                    if (!isAdded()) {
                                        dialog.dismiss();
                                        return;
                                    }

                                    dialog.getButton(
                                            AlertDialog.BUTTON_POSITIVE
                                    ).setEnabled(true);

                                    int status = error.networkResponse == null
                                            ? 0
                                            : error.networkResponse.statusCode;

                                    String message = status == 401
                                            ? "Session expired. Please log in again."
                                            : status == 400
                                            ? "Invalid profile information"
                                            : "Unable to update profile";

                                    Toast.makeText(
                                            requireContext(),
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                        );
                    });
        });

        dialog.show();
    }



    private void loadProfileByUsername(String selectedUsername) {

        repository.getUserByUsername(
                selectedUsername,
                response -> {
                    if (!isAdded()) return;

                    try {
                        JSONObject user =
                                response.getJSONObject("user");

                        int userId = user.getInt("id");

                        String name =
                                user.optString("displayName", "");

                        String handle =
                                user.optString("username", "");

                        String description =
                                user.optString("bio", "");

                        String createdAt =
                                user.optString("createdAt", "");

                        profileTitle.setText(name);
                        displayName.setText(name);
                        username.setText("@" + handle);

                        if (!description.isEmpty()
                                && !description.equals("null")) {
                            bio.setText(description);
                            bio.setVisibility(View.VISIBLE);
                        } else {
                            bio.setVisibility(View.GONE);
                        }

                        joinedDate.setText(
                                formatJoinedDate(createdAt)
                        );

                        // Visitors cannot edit this profile.
                        requireView()
                                .findViewById(R.id.buttonEditProfile)
                                .setVisibility(View.GONE);

                        loadUserPosts(userId, name, handle);

                    } catch (Exception error) {
                        showError("Invalid profile response");
                    }
                },
                error -> {
                    if (!isAdded()) return;

                    if (error.networkResponse != null
                            && error.networkResponse.statusCode == 404) {
                        showError("This user does not have an account");
                    } else {
                        showError("Cannot load this profile");
                    }
                }
        );
    }


}
