package com.example.gavinluo.donteatalone;

import android.app.TimePickerDialog;
import android.location.Location;
import android.preference.Preference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.RadioButton;

// Location services
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

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
    Button _startTimeEdit;
    Button _endTimeEdit;
    RadioGroup _genderRadios;
    RadioButton _genderNoPrefRadio;

    // Time components
    private int _startHour = 0;
    private int _startMin = 0;
    private int _endHour = 0;
    private int _endMin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // set the references
        _ageSpinner = (Spinner)findViewById(R.id.pref_age_spinner);
        _foodSpinner = (Spinner)findViewById(R.id.pref_food_spinner);
        _distanceEdit = (EditText)findViewById(R.id.pref_distance);
        _startTimeEdit = (Button)findViewById(R.id.pref_start_time);
        _endTimeEdit = (Button) findViewById(R.id.pref_end_time);
        _genderRadios = (RadioGroup)findViewById(R.id.pref_gender);
        _genderNoPrefRadio = (RadioButton)findViewById(R.id.pref_sex_none);

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
     * OnClick Listeners
     */
    public void getStartTime(View view){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                _startHour = selectedHour;
                _startMin = selectedMinute;
                _startTimeEdit.setText(to12HourTime(selectedHour, selectedMinute));
            }
        }, hour, minute, false ); // do not use the 24 hour clock
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void getEndTime(View view){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute){
                _endHour = selectedHour;
                _endMin = selectedMinute;
                _endTimeEdit.setText(to12HourTime(selectedHour, selectedMinute));
            }
        }, hour, minute, false); // use the 12 hour clock
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public String to12HourTime(int hour, int min){
        String ending = " am";
        if(hour / 12 > 0){
            ending = " pm";
        }

        hour = hour %12 ;
        return hour + ":" + min + ending;
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

        // angela.
        //if(pref_gender.getCheckedRadioButtonId()!=-1){
        int id= _genderRadios.getCheckedRadioButtonId();
        View radioButton = _genderRadios.findViewById(id);
        RadioButton btn = (RadioButton) _genderRadios.getChildAt(_genderRadios.indexOfChild(radioButton));
        String pref_gender_selection = (String) btn.getText();
        //}

        String pref_age_string = _ageSpinner.getSelectedItem().toString();
        String pref_food_string = _foodSpinner.getSelectedItem().toString();

        EditText pref_start_time = (EditText)findViewById(R.id.pref_start_time);
        if (pref_start_time.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a start time", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer pref_start_time_int = Integer.parseInt(pref_start_time.getText().toString());


        EditText pref_end_time = (EditText)findViewById(R.id.pref_end_time);
        if (pref_end_time.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a end time ", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer pref_end_time_int = Integer.parseInt(pref_end_time.getText().toString());

        //error checking
        if ((pref_start_time_int > 23) || (pref_start_time_int < 0) || (pref_end_time_int > 23) || (pref_end_time_int < 0)) {
            Toast.makeText(this, "prefer time out of range",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (pref_start_time_int >= pref_end_time_int) {
            Toast.makeText(this, "start time cannot be earlier than end time",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (_distanceEdit.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a distance", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer pref_distance_int = Integer.parseInt(_distanceEdit.getText().toString());

        Toast.makeText(this, "pref_age_string: " + pref_age_string +
                "\npref_gender_selection: " + pref_gender_selection+
                "\npref_food_string: " + pref_food_string+
                "\npref_start_time_int: " + pref_start_time_int+
                "\npref_end_time_int: " + pref_end_time_int+
                "\npref_distance_int: " + pref_distance_int, Toast.LENGTH_LONG).show();

    }
}
