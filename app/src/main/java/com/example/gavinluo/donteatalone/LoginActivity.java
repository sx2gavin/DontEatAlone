package com.example.gavinluo.donteatalone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {

    CallbackManager callbackManager;
    Profile profile;

    private static final String FIELDS = "fields";
    private static final String NAME = "name";
    private static final String ID = "id";
    //private static final String PICTURE = "picture";
    private static final String GENDER = "gender";
    private static final String EMAIL = "email";
    private static final String USER_FRIENDS = "friends";

    private static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{ID, NAME, GENDER, EMAIL, USER_FRIENDS});
    private JSONObject user;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = this;
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        /*profile = Profile.getCurrentProfile();
        if(profile != null){
            updateUI();
        }*/
        LoginButton loginButton = (LoginButton)findViewById(R.id.fblogin_button);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /*TextView info = (TextView) findViewById(R.id.info);
                info.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n" +
                        "AuthToken: " + loginResult.getAccessToken().getToken() + "\n" +
                        "ToString: " + loginResult.getAccessToken().toString());

                updateUI();*/

                final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject me, GraphResponse response) {
                                    user = me;
                                    profile = Profile.getCurrentProfile();

                                    String output = "";
                                    output = "ID: " + profile.getId() + "\n";
                                    output += "FirstName: " + profile.getFirstName() + "\n";
                                    output += "MiddleName: " + profile.getMiddleName() + "\n";
                                    output += "LastName: " + profile.getLastName() + "\n";
                                    output += "ProfilePic: " + profile.getProfilePictureUri(100, 100) + "\n";

                                    if (user == null) {
                                        output += "User: null \n";
                                    } else {
                                        output += "User: " + user.toString() + "\n";
                                    }

                                    DisplayMessage(output);
                                    LoginSuccessful();
                                    //ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
                                    //profilePictureView.setProfileId(profile.getId());
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString(FIELDS, REQUEST_FIELDS);
                    request.setParameters(parameters);
                    GraphRequest.executeBatchAsync(request);
                } else {
                    user = null;
                }
            }

            @Override
            public void onCancel() {
                DisplayMessage("Login attempt canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                DisplayMessage("Login attempt failed" + exception.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
        } else if (id == R.id.action_messenger) {
            startActivity(new Intent(this, MessengerActivity.class));
            return true;
        } else if (id == R.id.action_matches) {
            startActivity(new Intent(this, MatchesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void StartSigningUp(View view) {
        Intent intent = new Intent (this, SignupActivity.class);
        startActivity(intent);
    }

    public void StartLogin(View view) {
        Log.d(FacadeModule.TAG, "StartLogin called");

        if (! FacadeModule.getFacadeModule(context).isNetworkAvailable()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            // set title
            alertDialogBuilder.setTitle("Warning");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Internet connection unavailable, please turn on data service or WIFI.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(FacadeModule.TAG, "positive");
                            dialog.cancel();

                        }
                    });
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // if this button is clicked, just close
//                            // the dialog box and do nothing
//                            dialog.cancel();
//                        }
//                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return;
        }
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

        DisplayMessage("Waiting for the server to respond...");
        JSONObject response = null;
        String responseString;

        // try {
        FacadeModule.getFacadeModule(this).SendRequest(url, Request.Method.GET);

        Thread checker = new Thread() {
            public void run () {
                boolean running = true;
                while (running == true) {
                    // do stuff in a separate thread
                    try {
                        if (FacadeModule.getFacadeModule(context).LoggedIn()) {
                            // DisplayMessage("Login successfully!");
                            LoginSuccessful();
                            running = false;
                        }
                        Thread.sleep(10000);
                        Log.d(FacadeModule.TAG, "awake");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        running = false;
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        checker.start();


        // DisplayMessage(responseString);
        // Log.d(FacadeModule.TAG, responseString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return;
//        }

//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {String message = "";
//                        try {
//                            message = (String) response.get("message");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        DisplayMessage(message);
//                        if (message.equals("Login successful!") ) {
//                            LoginSuccessful();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                NetworkResponse response = error.networkResponse;
//                if(response != null && response.data != null){
//                    String message = new String(response.data);
//                    String errorMessage;
//                    try {
//                        JSONObject jsObj = new JSONObject(message);
//                        errorMessage = jsObj.getString("message");
//                    } catch(JSONException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    DisplayMessage(errorMessage);
//                }
//            }
//        });
//        // Add the request to the RequestQueue.
//        queue.add(jsObjRequest);
    }

    public void DisplayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void LoginSuccessful() {
        DisplayMessage("Login Successfully!");
        Intent intent = new Intent (this, StartMatchingActivity.class);
        startActivity(intent);
    }
}
