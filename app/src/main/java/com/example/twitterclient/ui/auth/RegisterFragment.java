
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

import com.example.twitterclient.MainActivity;
import com.example.twitterclient.R;
import com.example.twitterclient.repository.AuthRepository;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_register,
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
                view.findViewById(R.id.regUsername);

        EditText displayName =
                view.findViewById(R.id.regDisplayName);

        EditText email =
                view.findViewById(R.id.regEmail);

        EditText password =
                view.findViewById(R.id.regPassword);

        Button register =
                view.findViewById(R.id.buttonRegister);

        Button back =
                view.findViewById(R.id.buttonBackLogin);

        AuthRepository repository =
                new AuthRepository(requireContext());

        register.setOnClickListener(v -> {

            String user = username.getText()
                    .toString().trim();

            String name = displayName.getText()
                    .toString().trim();

            String mail = email.getText()
                    .toString().trim();

            String pass = password.getText()
                    .toString();

            if (user.isEmpty() || name.isEmpty()
                    || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(
                        requireContext(),
                        "Complete all fields",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            register.setEnabled(false);

            repository.register(
                    user,
                    name,
                    mail,
                    pass,
                    response -> {
                        if (!isAdded()) return;
                        register.setEnabled(true);

                        Toast.makeText(
                                requireContext(),
                                "Registered! Please log in.",
                                Toast.LENGTH_LONG
                        ).show();

                        ((MainActivity) requireActivity())
                                .showLogin();
                    },
                    error -> {
                        if (!isAdded()) return;
                        register.setEnabled(true);

                        String message = "Registration failed";

                        if (error.networkResponse != null) {
                            int status =
                                    error.networkResponse.statusCode;

                            if (status == 409) {
                                message = "Username or email already exists";
                            } else if (status == 400) {
                                message = "Check the registration details";
                            } else {
                                message = "Server error: HTTP " + status;
                            }
                        } else {
                            message = "Cannot reach server";
                        }

                        Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
            );
        });

        back.setOnClickListener(v ->
                ((MainActivity) requireActivity())
                        .showLogin()
        );
    }
}
