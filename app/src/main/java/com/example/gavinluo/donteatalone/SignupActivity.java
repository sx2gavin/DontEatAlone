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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class SignupActivity extends ActionBarActivity {

    private final String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void SubmitSignUp(View view) {
        // Do something here.
        Log.d(TAG, "SubmitSignUp called.");

        // Getting all the values from the UI
        EditText first_name = (EditText)findViewById(R.id.signup_firstname);
        String first_name_text = first_name.getText().toString();

        EditText last_name = (EditText)findViewById(R.id.signup_lastname);
        String last_name_text = last_name.getText().toString();

        EditText age = (EditText)findViewById(R.id.signup_age);
        String age_text = age.getText().toString();

        /*EditText user_description = (EditText)findViewById(R.id.signup_user_description);
        String user_description_text = user_description.getText().toString();*/

        EditText email = (EditText)findViewById(R.id.signup_email);
        String email_text = email.getText().toString();

        EditText password = (EditText)findViewById(R.id.signup_password);
        String password_text = password.getText().toString();

        EditText password_confirm = (EditText)findViewById(R.id.signup_confirm_password);
        String password_confirm_text = password_confirm.getText().toString();

		// ErrorChecking
		if (first_name_text.isEmpty()) {
			Toast.makeText(this, "You need to fill in your first name!", Toast.LENGTH_LONG).show();
			return;
		} else if (email_text.isEmpty()) {
			Toast.makeText(this, "Please use an email address to sign up.", Toast.LENGTH_LONG).show();
			return;
		} else if (password_text.isEmpty()) {
			Toast.makeText(this, "Choose a password for your account.", Toast.LENGTH_LONG).show();
			return;
		} else if (!password_text.equals(password_confirm_text)) {
			Toast.makeText(this, "Password and confirmation need to match.", Toast.LENGTH_LONG).show();
			return;
		}	
			

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://donteatalone.paigelim.com/api/v1/users?" +
                "email=" + email_text +
                "&password=" + password_text +
                "&password_confirmation=" + password_confirm_text +
                "&name=" + first_name_text +
                "&age=" + age_text;

        // final SignupActivity signup = this;

// Request a JsonObject response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String message = "";
                        try {
                            Log.d(TAG, "Getting the message.");
                            message = (String) response.get("message");
                        } catch (JSONException e) {
                            Log.d(TAG, "There is an JSON exception.");
                            e.printStackTrace();
                        }
                        Log.d(TAG, message);
                        DisplayMessage(message);
                        LoginSuccessful();
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
                    Log.d(TAG, message);
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
