package com.example.gavinluo.donteatalone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;


public class ProfileActivity extends ActionBarActivity {

    CallbackManager callbackManager;
    // References to UI components created in activity_profile
    protected EditText _name;
    protected TextView _email;
    protected TextView _gender;
    protected TextView _age;
    protected EditText _description;
    protected LoginButton _loginButton;
    protected TextView _fbloginMsg;
    protected Button _updateButton;

    protected boolean _update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_profile);

        // set the references
        _name = (EditText)findViewById(R.id.profile_name);
        _email = (TextView)findViewById(R.id.profile_email);
        _gender = (TextView)findViewById(R.id.profile_gender);
        _age = (TextView)findViewById(R.id.profile_age);
        _description = (EditText)findViewById(R.id.profile_description);
        _updateButton = (Button)findViewById(R.id.profile_update_button);
        _loginButton = (LoginButton)findViewById(R.id.fblogin_button);
        _fbloginMsg = (TextView)findViewById(R.id.fblogin_msg);

        _name.setEnabled(false);
        _description.setEnabled(false);

        // TODO: call the server to return the user's profile
        // initialize items
        _name.setText("angela");
        _email.setText("abcd@gmail.com");
        _gender.setText("female");
        _age.setText("18");
        _description.setText("hello world");

        _update = false;

        //LoginButton loginButton = (LoginButton)findViewById(R.id.fblogin_button);
        // Callback registration
        _loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                _fbloginMsg.setText("User ID: ");

                final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    _fbloginMsg.setText("User ID: " + accessToken.getUserId());
                } else {
                    //user = null;
                    _fbloginMsg.setText("User ID: null");
                }
            }

            @Override
            public void onCancel() {
                _fbloginMsg.setText("Login attempt canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                _fbloginMsg.setText("Login attempt failed" + exception.toString());
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
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //TODO:Logout and go back to login page
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.profile_logoutDialog_message)
                    .setTitle(R.string.profile_logoutDialog_title);
            // Add the buttons
            builder.setPositiveButton(R.string.profile_logoutDialog_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO: delete everything and go back to login page
                }
            });
            builder.setNegativeButton(R.string.profile_logoutDialog_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            // Create the AlertDialog
            builder.create();
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateProfile(View view) {
        if (_update) {
            // save update
            _update = false;
            _loginButton.setVisibility(View.GONE);
            _updateButton.setText(R.string.profile_button_edit);
            // TODO: call server to update the profile
        }
        else {
            // enable update: remove readonly, change button name, enable FB
            _update = true;
            _loginButton.setVisibility(View.VISIBLE);
            _updateButton.setText(R.string.profile_button_save);
        }
        // change readonly
        _name.setEnabled(_update);
        _description.setEnabled(_update);
    }
}

/*

public class ProfileActivity extends ActionBarActivity {

    Activity mActivity;

    CallbackManager callbackManager;
    // References to UI components created in activity_profile
    protected EditText _name;
    protected TextView _email;
    protected TextView _gender;
    protected TextView _age;
    protected EditText _description;
    protected LoginButton _loginButton;
    protected TextView _fbloginMsg;
    protected Button _updateButton;

    protected boolean _update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mActivity = this;
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_profile);

        // set the references
        _name = (EditText)findViewById(R.id.profile_name);
        _email = (TextView)findViewById(R.id.profile_email);
        _gender = (TextView)findViewById(R.id.profile_gender);
        _age = (TextView)findViewById(R.id.profile_age);
        _description = (EditText)findViewById(R.id.profile_description);
        _updateButton = (Button)findViewById(R.id.profile_update_button);
        _loginButton = (LoginButton)findViewById(R.id.fblogin_button);
        _fbloginMsg = (TextView)findViewById(R.id.fblogin_msg);

        _name.setEnabled(false);
        _description.setEnabled(false);

        // TODO: call the server to return the user's profile
        Profile profile = FacadeModule.getFacadeModule(this).GetUserProfile();
        // initialize items
        _name.setText(profile.GetName());
        _email.setText(profile.GetEmail());
        _gender.setText(profile.GetGender());
        _age.setText(profile.GetAge());
        _description.setText(profile.GetDescription());

        _update = false;

        //LoginButton loginButton = (LoginButton)findViewById(R.id.fblogin_button);
        // Callback registration
        _loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                _fbloginMsg.setText("User ID: ");

                final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    _fbloginMsg.setText("User ID: " + accessToken.getUserId());
                } else {
                    //user = null;
                    _fbloginMsg.setText("User ID: null");
                }
            }

            @Override
            public void onCancel() {
                _fbloginMsg.setText("Login attempt canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                _fbloginMsg.setText("Login attempt failed" + exception.toString());
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
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (_update) {
            menu.getItem(0).setEnabled(false);
        }
        else {
            menu.getItem(0).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //TODO:Logout and go back to login page
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.profile_logoutDialog_message)
                    .setTitle(R.string.profile_logoutDialog_title);
            // Add the buttons
            builder.setPositiveButton(R.string.profile_logoutDialog_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //ArrayList<User> matchList = FacadeModule.getFacadeModule(context).GetMatchList();
                    Intent logoutIntent = new Intent(mActivity, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                }
            });
            builder.setNegativeButton(R.string.profile_logoutDialog_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            // Create the AlertDialog
            builder.create();
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateProfile(View view) {
        if (_update) {
            // save update
            _update = false;
            _loginButton.setVisibility(View.GONE);
            _updateButton.setText(R.string.profile_button_edit);
            // TODO: call server to update the profile

        }
        else {
            // enable update: remove readonly, change button name, enable FB
            _update = true;
            _loginButton.setVisibility(View.VISIBLE);
            _updateButton.setText(R.string.profile_button_save);
        }
        // change readonly
        _name.setEnabled(_update);
        _description.setEnabled(_update);
        invalidateOptionsMenu();
    }
}



*/
