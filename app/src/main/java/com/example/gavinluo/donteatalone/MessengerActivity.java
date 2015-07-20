package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MessengerActivity extends ActionBarActivity
{
    private static final String TAG = "MESSENGER";

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private Context _context;
    private ArrayList<Message> _messages;
    private Meeting _meeting;

    private static final int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = this;
        setContentView(R.layout.activity_messenger);

        this.getAllMessages();
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
                    Meeting meeting = FacadeModule.getFacadeModule(_context).GetMeeting();
                    if (meeting != null) {
                        FacadeModule.getFacadeModule(_context).SendRequestLikeUser(meeting.mToUserId);
                    }
                }
            });
            builder.setNegativeButton(R.string.messenger_endDialog_dislike, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO: dislike
                    Meeting meeting = FacadeModule.getFacadeModule(_context).GetMeeting();
                    if (meeting != null) {
                        FacadeModule.getFacadeModule(_context).SendRequestDislikeUser(meeting.mToUserId);
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

    public void onSendButtonClick(View view) {
        // TODO: Get current text in message box
        String message = "hello!";
        FacadeModule.getFacadeModule(_context).SendMessageToUser(_meeting.mToUserId, message);

        this.getAllMessages();
    }

    // Pick the place
    public void onPickButtonClick(View view){
        Intent intent = new Intent(this, RestaurantListActivity.class);
        startActivity(intent);
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

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getAllMessages() {
        Log.d(TAG, "getAllMessages called");

        //this.checkInternetConnection();

        // Do request
        FacadeModule.getFacadeModule(this).SendRequestGetAllMessages();
//        if (FacadeModule.getFacadeModule(this).LastRequestResult() == 1) {
        _meeting = FacadeModule.getFacadeModule(this).GetMeeting();
        _messages = _meeting.mMessages;
        ListView listview = (ListView) findViewById(R.id.list);
        MessengerAdapter adapter = new MessengerAdapter(_context, _messages);
        listview.setAdapter(adapter);

    }

    protected void checkInternetConnection() {
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

    public class MessengerAdapter extends ArrayAdapter {
        private final Context context;
        private ArrayList<Message> messengers;

        public MessengerAdapter(Context context, ArrayList values) {
            super(context, -1, values);
            this.context = context;
            this.messengers = values;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (messengers.get(position).mUserId == FacadeModule.getFacadeModule(context).GetUserId()) ? 0 : 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            int type = getItemViewType(position);
            if (v == null) {
                // Inflate the layout according to the view type
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (type == 0) {
                    // Inflate the layout with image
                    v = inflater.inflate(R.layout.senderlayout, parent, false);
                }
                else {
                    v = inflater.inflate(R.layout.receiverlayout, parent, false);
                }
            }
            Message m = messengers.get(position);

            TextView message = (TextView) v.findViewById(R.id.message_text);

            message.setText(m.mMessage);

            return v;
        }
    }


}
