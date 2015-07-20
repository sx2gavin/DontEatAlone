package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


public class MessengerActivity extends ActionBarActivity
{
    private static final String TAG = "MESSENGER";
    private final Context context = this;

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
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
                    Meeting meeting = FacadeModule.getFacadeModule(context).GetMeeting();
                    if (meeting != null) {
                        FacadeModule.getFacadeModule(context).SendRequestLikeUser(meeting.mToUserId);
                    }
                }
            });
            builder.setNegativeButton(R.string.messenger_endDialog_dislike, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO: dislike
                    Meeting meeting = FacadeModule.getFacadeModule(context).GetMeeting();
                    if (meeting != null) {
                        FacadeModule.getFacadeModule(context).SendRequestDislikeUser(meeting.mToUserId);
                    }
                }
            });
            // Create the AlertDialog
            builder.create();
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            TextView tv1 = (TextView)findViewById(R.id.mess_debug1);
            TextView tv2 = (TextView)findViewById(R.id.mess_debug2);
            TextView tv3 = (TextView)findViewById(R.id.mess_debug3);
            tv1.setText(name);
            tv2.setText(address);
            tv3.setText(Html.fromHtml(attributions));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
