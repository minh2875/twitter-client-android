
package com.example.twitterclient.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.twitterclient.MainActivity;
import com.example.twitterclient.R;
import com.example.twitterclient.auth.SessionManager;
import com.example.twitterclient.repository.AuthRepository;

import org.json.JSONObject;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_login,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        EditText username =
                view.findViewById(R.id.inputUsername);

        EditText password =
                view.findViewById(R.id.inputPassword);

        Button login =
                view.findViewById(R.id.buttonLogin);

        Button register =
                view.findViewById(R.id.buttonGoRegister);

        AuthRepository repository =
                new AuthRepository(requireContext());

        SessionManager session =
                new SessionManager(requireContext());

        login.setOnClickListener(v -> {

            String user = username.getText()
                    .toString().trim();

            String pass = password.getText()
                    .toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(
                        requireContext(),
                        "Enter username and password",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            login.setEnabled(false);

            repository.login(
                    user,
                    pass,
                    response -> {
                        if (!isAdded()) return;
                        login.setEnabled(true);

                        try {
                            String token =
                                    response.getString("token");

                            JSONObject userData =
                                    response.getJSONObject("user");

                            String actualUsername =
                                    userData.getString("username");

                            session.saveSession(
                                    token,
                                    actualUsername
                            );

                            ((MainActivity) requireActivity())
                                    .showTimeline();

                        } catch (Exception error) {
                            Toast.makeText(
                                    requireContext(),
                                    "Invalid server response",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    },
                    error -> {
                        if (!isAdded()) return;
                        login.setEnabled(true);

                        String message = "Connection failed";

                        if (error.networkResponse != null) {
                            int status =
                                    error.networkResponse.statusCode;

                            if (status == 401) {
                                message = "Incorrect username or password";
                            } else {
                                message = "Login failed: HTTP " + status;
                            }
                        }

                        Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
            );
        });

        register.setOnClickListener(v ->
                ((MainActivity) requireActivity())
                        .showRegister()
        );
    }
}
