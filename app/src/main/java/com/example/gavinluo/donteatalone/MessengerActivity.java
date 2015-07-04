package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


public class MessengerActivity extends ActionBarActivity
{
    private static final String TAG = "MESSENGER";

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Pick the place
    public void onPickButtonClick(View view){
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

            Intent intent = intentBuilder.build(this);

            // Start the intent by requesting a result ,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, e.toString());
        } catch (GooglePlayServicesNotAvailableException e){
            Log.d(TAG, e.toString());
        }
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
