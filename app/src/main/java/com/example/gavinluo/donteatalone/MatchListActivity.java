package com.example.gavinluo.donteatalone;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class MatchListActivity extends ActionBarActivity
        implements NoMatchFragment.OnFragmentInteractionListener,
        MatchListFragment.OnFragmentInteractionListener
{
    private static final int NO_MATCH_VIEW = 0;
    private static final int MATCH_LIST_VIEW = 1;
    private static final String TAG = "MatchList";

    int user_id = 1000;
    int view;
    Context context;
    NoMatchFragment noMatchFragment;
    MatchListFragment matchListFragment;

    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        view = -1;          // set the view to be an invalid number
        context = this;
        noMatchFragment = new NoMatchFragment();
        matchListFragment = new MatchListFragment();
//        setFragment();

        // on default, show the matchListView
        viewMatchListFragment();

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null) {
                return;
            }

            timer = new Thread() {
                public void run () {
                    for (;;) {
                        // do stuff in a separate thread
                        uiCallback.sendEmptyMessage(0);
                        try {
                            Thread.sleep(10000);
                            Log.d(TAG, "awake");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            };
            timer.start();

//            viewNoMatchFragment();
//            viewMatchListFragment();

            // Create a new Fragment to be placed in the activity layout
//            NoMatchFragment noMatchFragment = new NoMatchFragment();
//
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
//            noMatchFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, noMatchFragment).commit();
        }
    }

//    public void replaceToNFragment(){
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

    public void viewMatchListFragment(){
        // In case this activity was started with special isntructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        matchListFragment.setArguments(getIntent().getExtras());

        // Remove the other fragment if it's in fragment_container
        if(view == NO_MATCH_VIEW) {
            getSupportFragmentManager().beginTransaction().remove(noMatchFragment).commit();
        }

        // Add the fragment to the 'fragment' Frame Layout;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, matchListFragment).commit();

        view = MATCH_LIST_VIEW;
    }

//    public void viewMatchListFragment(){
//        // In case this activity was started with special isntructions from an
//        // Intent, pass the Intent's extras to the fragment as arguments
//        matchListFragment.setArguments(getIntent().getExtras());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//
//        getSupportFragmentManager().beginTransaction().re
//        // Add the fragment to the 'fragment' Frame Layout;
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, matchListFragment).commit();
//
//        view = MATCH_LIST_VIEW;
//    }

    // display no match fragment
    public void viewNoMatchFragment(){
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        noMatchFragment.setArguments(getIntent().getExtras());

        // Remove the other fragment if it's in fragment_container
        if(view == MATCH_LIST_VIEW) {
            getSupportFragmentManager().beginTransaction().remove(matchListFragment).commit();
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, noMatchFragment).commit();

        view = NO_MATCH_VIEW;
    }


    public void chooseFragment(){

    }

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
//            MatchListFragment matchListFragment = new MatchListFragment();
//
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//            // Replace whatever is in the fragment_container view with this fragment,
//            // and add the transaction to the back stack so the user can navigate back
//            transaction.replace(R.id.fragment_container, matchListFragment);
//            transaction.addToBackStack(null);
//
//            // Commit the transaction
//            transaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(Uri uri){
        // do nothing for now
    }

    private Handler uiCallback = new Handler() {
        public void handleMessage (Message msg) {
            //  retrieve the values about health details from server database
            Log.d(TAG, msg.toString());

            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://donteatalone.paigelim.com/api/v1/users/"+user_id+"/matches";
            Log.d(TAG, url);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // show the match list page
                            if(view != MATCH_LIST_VIEW){
                                viewMatchListFragment();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // show the no match page
                            if(view != NO_MATCH_VIEW){
                                viewNoMatchFragment();
                            }
                            Log.d(TAG, "ERROR: " + error.getMessage()+"");
                            // Keep waiting
                        }
                    });

            // Access the RequestQueue through your singleton class.
            queue.add(jsObjRequest);
        }
    };
}
