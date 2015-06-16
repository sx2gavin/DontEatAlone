package com.example.gavinluo.donteatalone;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MatchListActivity extends ActionBarActivity
        implements NoMatchFragment.OnFragmentInteractionListener,
        MatchListFragment.OnFragmentInteractionListener
{
    private static final String TAG = "MatchList";

    FragmentManager fragmentManager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        context = this;
        fragmentManager = getSupportFragmentManager();
//        setFragment();

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            NoMatchFragment noMatchFragment = new NoMatchFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            noMatchFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, noMatchFragment).commit();
        }
    }

//    protected void setFragment(){
//        Fragment newFragment = new NoMatchFragment();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//        // Replace whatever is in the fragment_container with the new fragment
//        transaction.replace(R.id.no_match_fragment, newFragment);
//        transaction.addToBackStack(null);
//
//        // Commit the transaction
//        transaction.commit();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match_list, menu);
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
            // change the fragment
            MatchListFragment matchListFragment = new MatchListFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, matchListFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onNoMatch() {
//        // do nothing for now
//
//        NoMatchFragment noMatchfragment = (NoMatchFragment)
//                getSupportFragmentManager().findFragmentById(R.id.no_match_fragment);
//
//        Toast.makeText(this, "Switched to no match", Toast.LENGTH_LONG).show();
//        Log.d(TAG, "Switched to no match");
//    }


    @Override
    public void onFragmentInteraction(Uri uri){
        // do nothing for now
    }

    public void goToPreference(View view){
        startActivity(new Intent(this, PreferenceActivity.class));
    }


}
