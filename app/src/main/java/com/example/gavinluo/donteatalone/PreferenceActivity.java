package com.example.gavinluo.donteatalone;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

// Location services
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class PreferenceActivity extends ActionBarActivity
        implements ConnectionCallbacks, OnConnectionFailedListener
{

    // Provides the entry point to Google Play services.
    protected GoogleApiClient _googleApiClient;

    // The last geogrpahical location.
    protected Location _lastLocation;

    // References to UI components created in activity_preference.xml
    Spinner _ageSpinner;
    Spinner _foodSpinner;
    EditText _distanceEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // set the references
        _ageSpinner = (Spinner)findViewById(R.id.pref_age_spinner);
        _foodSpinner = (Spinner)findViewById(R.id.pref_food_spinner);
        _distanceEdit = (EditText)findViewById(R.id.pref_distance);

        initSpinnerItems(_ageSpinner, R.array.pref_age_array);
        initSpinnerItems(_foodSpinner, R.array.pref_food_array);

        buildGoogleApiClient();
    }

    // Reference: http://developer.android.com/guide/topics/ui/controls/spinner.html
    private void initSpinnerItems(Spinner spinner, int arrayNum){
        // Create an ArrayAdapter using hte string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayNum, android.R.layout.simple_spinner_item);

        // Specificy the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preference, menu);
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

    // Builds a GoogleApiClient. Uses the addApi() method to request hte LocationServices API.
    protected synchronized void buildGoogleApiClient() {
        _googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        _googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(_googleApiClient.isConnected()) {
            _googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // retrieves the most recent location currently available
        _lastLocation = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);

        if(_lastLocation != null){
            // TODO: Remove after testing (create toast to show longitude and latitude)
            Toast.makeText(this, "Latitude: " + _lastLocation.getLatitude() +
                    "\nLongitude: " + _lastLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No location detected. Make sure location is enabled on your device",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("GoogleMap", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play service was lost. Call connect() to
        // re-establish the connection
        Log.i("GoogleMap", "Connection suspended" );
        _googleApiClient.connect();
    }


    /*
     * Test code
     */
    public void startSearching(View view){
        // Re-retrieve the location if google play service is still connected
        if(_googleApiClient.isConnected()){
            _lastLocation = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        }

        if(_lastLocation != null){
            // TODO: Remove after testing (create toast to show longitude and latitude)
            Toast.makeText(this, "Latitude: " + _lastLocation.getLatitude() +
                    "\nLongitude: " + _lastLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No location detected. Make sure location is enabled on your device",
                    Toast.LENGTH_LONG).show();
        }
    }
}
