package com.example.gavinluo.donteatalone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;


public class MessengerActivity extends Activity
        implements messageFragment.OnFragmentInteractionListener
{
    private static final String TAG = "MessengerActivity";

    private Context _context;
    private ArrayList<Message> _messages;
    private Meeting _meeting;
    private messageFragment _messageFragment;


    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _context = this;
        setContentView(R.layout.activity_messenger);
        _messageFragment = messageFragment.newInstance("blah1", "blah2");
        _messageFragment.onAttach(this);

        this.getAllMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        this.getAllMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        this.getAllMessages();
    }

    public void checkInternetConnection() {
        if (! FacadeModule.getFacadeModule(_context).isNetworkAvailable()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);

            // set title
            alertDialogBuilder.setTitle("Warning");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Internet connection unavailable, please turn on data service or WIFI.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    public void getAllMessages() {
        Log.d(TAG, "getAllMessages called");

        this.checkInternetConnection();

        // Do request
        FacadeModule.getFacadeModule(this).SendRequestGetAllMessages();
//        if (FacadeModule.getFacadeModule(this).LastRequestResult() == 1) {
            _meeting = FacadeModule.getFacadeModule(this).GetMeeting();
            _messages = _meeting.mMessages;

//            for (int i = 0; i < _messages.size(); i++) {
//                Message mMessage = _messages.get(i);
//                Log.d(TAG, "Printing Message" + i + ": " + mMessage.mMessage);
//            }
//        } else {
//            Log.d(TAG, "LastRequestResult != 1");
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messenger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_end) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.messenger_endDialog_msg)
                    .setTitle(R.string.messenger_endDialog_title);
            // Add the buttons
            builder.setPositiveButton(R.string.messenger_endDialog_like, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO: like
                }
            });
            builder.setNegativeButton(R.string.messenger_endDialog_dislike, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO: dislike
                }
            });
            // Create the AlertDialog
            builder.create();
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSendButtonClick(View view) {
        // TODO: Get current text in message box
        String message = "hello!";
        FacadeModule.getFacadeModule(_context).SendMessageToUser(_meeting.mToUserId, message);

        this.getAllMessages();
    }

    // Pick the place
    public void onPickButtonClick(View view){
        // Google Map
//        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q=restaurants");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);

        // Change the Intent
        Intent intent = new Intent(this, RestaurantListActivity.class);
        startActivity(intent);

//        https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=API_KEY




//        mapIntent.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng point) {
//                Toast.makeText(getApplicationContext(), point.toString(), Toast.LENGTH_SHORT).show();
//            }
//        })

        // Construct an intent for the place picker
//        try {
//            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
//
//            Intent intent = intentBuilder.build(this);
//
//            // Start the intent by requesting a result ,
//            // identified by a request code.
//            startActivityForResult(intent, REQUEST_PLACE_PICKER);
//        } catch (GooglePlayServicesRepairableException e) {
//            Log.d(TAG, e.toString());
//        } catch (GooglePlayServicesNotAvailableException e){
//            Log.d(TAG, e.toString());
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PLACE_PICKER && resultCode == Activity.RESULT_OK){
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name =place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if(attributions == null) {
                attributions = "";
            }

//            TextView tv1 = (TextView)findViewById(R.id.mess_debug1);
//            TextView tv2 = (TextView)findViewById(R.id.mess_debug2);
//            TextView tv3 = (TextView)findViewById(R.id.mess_debug3);
//            tv1.setText(name);
//            tv2.setText(address);
//            tv3.setText(Html.fromHtml(attributions));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // messageFragment Methods
    @Override
    public void onFragmentInteraction(String id) {

    }
}
