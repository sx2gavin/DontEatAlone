package com.example.gavinluo.donteatalone;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;


public class PreferenceActivity extends ActionBarActivity {

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
}
