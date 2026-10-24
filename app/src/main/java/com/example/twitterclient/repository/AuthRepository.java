
package com.example.twitterclient.repository;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthRepository {

    private static final String BASE_URL =
            "http://10.0.2.2:3000";

    private final RequestQueue queue;

    public AuthRepository(Context context) {
        queue = Volley.newRequestQueue(
                context.getApplicationContext()
        );
    }

    public void login(
            String username,
            String password,
            Response.Listener<JSONObject> success,
            Response.ErrorListener failure
    ) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + "/api/auth/login",
                    body,
                    success,
                    failure
            );

            queue.add(request);

        } catch (JSONException error) {
            failure.onErrorResponse(
                    new com.android.volley.VolleyError(error)
            );
        }
    }

    public void register(
            String username,
            String displayName,
            String email,
            String password,
            Response.Listener<JSONObject> success,
            Response.ErrorListener failure
    ) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("displayName", displayName);
            body.put("email", email);
            body.put("password", password);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + "/api/auth/register",
                    body,
                    success,
                    failure
            );

            queue.add(request);

        } catch (JSONException error) {
            failure.onErrorResponse(
                    new com.android.volley.VolleyError(error)
            );
        }
    }
}
