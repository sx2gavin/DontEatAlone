package com.example.gavinluo.donteatalone;

import android.app.Activity;
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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;


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
    protected Button _updateButton;

    protected boolean _update;

    protected Profile profile;

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

        _name.setEnabled(false);
        _description.setEnabled(false);

        FacadeModule.getFacadeModule(mActivity).SendRequestGetProfile(FacadeModule.getFacadeModule(this).GetUserId());
        Thread checker = new Thread() {
            public void run () {
                boolean running = true;
                while (running == true) {
                    String response = FacadeModule.getFacadeModule(mActivity).GetResponse();
                    try {
                        if (response.equals("error")) {
                            running = false;
                            DisplayMessage("Error! Please try again");
                        } else if (response.equals("")) {
                        } else {
                            running = false;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profile = FacadeModule.getFacadeModule(mActivity).GetUserProfile();
                                    _name.setText(profile.GetName());
                                    _email.setText(profile.GetEmail());
                                    _gender.setText(profile.GetGender());
                                    _age.setText(Integer.toString(profile.GetAge()));
                                    _description.setText(profile.GetDescription());
                                }
                            });
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

        _update = false;

        // Callback registration
        _loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    FacadeModule.getFacadeModule(mActivity).UpdateFacebookId(accessToken.getUserId());
                    Thread checker = new Thread() {
                        public void run () {
                            boolean running = true;
                            while (running == true) {
                                String response = FacadeModule.getFacadeModule(mActivity).GetResponseMessage();
                                try {
                                    if (response == "User was successfully updated.") {
                                        DisplayMessage(response);
                                        running = false;
                                    } else if (response != "") {
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
                } else {
                    DisplayMessage("User ID: null");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.profile_logoutDialog_message)
                    .setTitle(R.string.profile_logoutDialog_title);
            // Add the buttons
            builder.setPositiveButton(R.string.profile_logoutDialog_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO: delete everything and go back to login page
                    FacadeModule.getFacadeModule(mActivity).SendRequestLogOut();
                    DisplayMessage("Logging out");
                    DialogInterface Dialog = dialog;
                    Dialog.dismiss();
                    Thread checker = new Thread() {
                        public void run () {
                            boolean running = true;
                            while (running == true) {
                                String response = FacadeModule.getFacadeModule(mActivity).GetResponseMessage();
                                try {
                                    if (response.equals("Logout successful!")) {
                                        Intent logoutIntent = new Intent(mActivity, LoginActivity.class);
                                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(logoutIntent);
                                        running = false;
                                    } else if (!response.equals("")) {
                                        DisplayMessage("error: " + response);
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

    public void DisplayMessage(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateProfile(View view) {
        if (_update) {
            // save update
            profile.SetName(_name.getText().toString());
            profile.SetDescription(_description.getText().toString());
            FacadeModule.getFacadeModule(mActivity).UpdateUserProfile(profile);
            // TODO: call server to update the profile
            Thread checker = new Thread() {
                public void run () {
                    boolean running = true;
                    while (running == true) {
                        int response = FacadeModule.getFacadeModule(mActivity).LastRequestResult();
                        try {
                            if (response == 0) {
                                DisplayMessage("no result");
                            } else if (response == 1) {
                                DisplayMessage("successful");
                                running = false;
                            } else if (response == -1) {
                                DisplayMessage("error");
                                running = false;
                            }
                            /*if (response.equals("User's profile successfully updated.")) {
                                DisplayMessage("User's profile successfully updated.");
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        _update = false;
                                        _loginButton.setVisibility(View.GONE);
                                        _updateButton.setText(R.string.profile_button_edit);
                                        _name.setEnabled(false);
                                        _description.setEnabled(false);
                                        invalidateOptionsMenu();
                                    }
                                });

                                //updateUI(false);
                                running = false;
                            } else if (response != "") {
                                DisplayMessage(response);
                                running = false;
                            }*/
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
        else {
            // enable update: remove readonly, change button name, enable FB
            _update = true;
            _loginButton.setVisibility(View.VISIBLE);
            _updateButton.setText(R.string.profile_button_save);
            _name.setEnabled(_update);
            _description.setEnabled(_update);
            invalidateOptionsMenu();
        }
    }

    public void updateUI(boolean save) { // false: update->view; true: view->update
        _update = save;
        if (!_update) {
            _loginButton.setVisibility(View.GONE);
            _updateButton.setText(R.string.profile_button_edit);
        }
        else {
            _loginButton.setVisibility(View.VISIBLE);
            _updateButton.setText(R.string.profile_button_save);
        }
        _name.setEnabled(_update);
        _description.setEnabled(_update);
        invalidateOptionsMenu();
    }
}
