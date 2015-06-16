package com.example.gavinluo.donteatalone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_preference){
            startActivity(new Intent(this, PreferenceActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_matchlist) {
            startActivity(new Intent(this, MatchListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void StartSigningUp(View view) {
        Intent intent = new Intent (this, SignupActivity.class);
        startActivity(intent);
    }

    public void StartLogin(View view) {

        EditText email = (EditText)findViewById(R.id.login_email);
        String email_text = email.getText().toString();

        EditText password = (EditText)findViewById(R.id.login_password);
        String password_text = password.getText().toString();

        if (email_text.isEmpty()) {
            Toast.makeText(this, "Please input your email", Toast.LENGTH_LONG).show();
            return;
        } else if (password_text.isEmpty()) {
            Toast.makeText(this, "Please input your password", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://donteatalone.paigelim.com/api/v1/login?" +
                "email=" + email_text +
                "&password=" + password_text;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {String message = "";
                        try {
                            message = (String) response.get("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        DisplayMessage(message);
                        if (message.equals("Login successful!") ) {
                            LoginSuccessful();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    String message = new String(response.data);
                    String errorMessage;
                    try {
                        JSONObject jsObj = new JSONObject(message);
                        errorMessage = jsObj.getString("message");
                    } catch(JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    DisplayMessage(errorMessage);
                }
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    public void DisplayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void LoginSuccessful() {
        Intent intent = new Intent (this, StartMatchingActivity.class);
        startActivity(intent);
    }
}
