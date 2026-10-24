
package com.example.twitterclient;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.twitterclient.ui.profile.ProfileFragment;
import com.example.twitterclient.auth.SessionManager;
import com.example.twitterclient.ui.auth.LoginFragment;
import com.example.twitterclient.ui.auth.RegisterFragment;
import com.example.twitterclient.ui.timeline.TimelineFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            SessionManager session =
                    new SessionManager(this);

            if (session.isLoggedIn()) {
                showTimeline();
            } else {
                showLogin();
            }
        }
    }
    public void showProfile() {
        openFragment(new ProfileFragment());
    }


    public void showUserProfile(String username) {
        openFragment(
                ProfileFragment.forUsername(username)
        );
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void showLogin() {
        openFragment(new LoginFragment());
    }

    public void showRegister() {
        openFragment(new RegisterFragment());
    }

    public void showTimeline() {
        openFragment(new TimelineFragment());
    }
}
