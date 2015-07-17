package com.example.gavinluo.donteatalone;

import android.content.Context;
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
	private final Context context = this;

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


        FacadeModule.getFacadeModule(this).SendRequestSignUp(email_text, password_text, password_confirm_text, first_name_text + last_name_text, age_text);

        DisplayMessage("Waiting for response...");

		Thread checker = new Thread() {
			public void run () {
				boolean running = true;
                while(running) {
                    try {
                        String response = FacadeModule.getFacadeModule(context).GetResponseMessage();
                        Log.d(FacadeModule.TAG, response);
                        if (response.equals("User successfully created.")) {
                            DisplayMessage("User successfully created.");
                            LoginSuccessful();
                            running = false;
                        } else if (!response.equals("")) {
							DisplayMessage(response); 
							running = false;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        running = false;
                        Thread.currentThread().interrupt();
                    }
                }
			}
		};
        checker.start();

    }

    public void DisplayMessage(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void LoginSuccessful() {
        Intent intent = new Intent (context, StartMatchingActivity.class);
        startActivity(intent);
    }
}
