package com.example.gavinluo.donteatalone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class WaitingActivity extends ActionBarActivity {
    private final String TAG = "Waitings";
    private final int user_id = 2;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        context = this;
        Thread timer = new Thread() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_waiting, menu);
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

    public void cancelSearching(View view) {
        // TODO: remove user in the searching table
        // TODO: stop the thread
        finish();

        /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Request received");

        // set dialog message
        alertDialogBuilder
                .setMessage("A user want to have a meal with you, do you accept this request")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, idk
                        Log.d(TAG, "positive");
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();*/

    }

    private Handler uiCallback = new Handler () {
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

                            // the match return some result, go to matches page
                            String json = response.toString();
                            String target = "\"total_count\":";
                            String countString = json.substring(json.indexOf(target));
                            int spaceIndex = countString.indexOf(":");
                            int commaIndex = countString.indexOf(",");
                            String numberString = countString.substring(spaceIndex + 1, commaIndex);
                            int number = Integer.parseInt(numberString);
                            // the match return some result, go to matches page
                            if (number != 0) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        context);

                                // set title
                                alertDialogBuilder.setTitle("Request received");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("A user want to have a meal with you, do you accept this request")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, idk
                                                Log.d(TAG, "positive");
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, just close
                                                // the dialog box and do nothing
                                                dialog.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                                Thread.currentThread().interrupt(); //TODO: This should move to somewhere else
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "ERROR: " + error.getMessage()+"");
                            // Keep waiting
                        }
                    });

            // Access the RequestQueue through your singleton class.
            queue.add(jsObjRequest);
        }
    };
}
