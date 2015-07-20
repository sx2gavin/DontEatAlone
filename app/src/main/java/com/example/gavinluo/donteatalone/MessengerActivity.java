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
import android.widget.EditText;
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
    private EditText _sendMsgBox;
    private MessengerActivity _activity;
    private ListView _listview;
    private MessengerAdapter _adapter;

    private static final int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = this;
        _activity = this;
        setContentView(R.layout.activity_messenger);
        _sendMsgBox = (EditText)findViewById(R.id.edit_text);
        _listview = (ListView) findViewById(R.id.list);

        _messages = new ArrayList<Message>();
        _adapter = new MessengerAdapter(_context, _messages);
        _listview.setAdapter(_adapter);

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

    public void DisplayMessage(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onSendButtonClick(View view) {
        // TODO: Get current text in message box
        String message = _sendMsgBox.getText().toString();

        if(message.compareTo("")==0){
            DisplayMessage("Cannot send an empty message.");
        }

        FacadeModule.getFacadeModule(_context).SendMessageToUser(_meeting.mToUserId, message);

        Thread checker = new Thread() {
            public void run () {
                boolean running = true;
                while (running == true) {
                    try {
                        if (FacadeModule.getFacadeModule(_context).LastRequestResult()==1) {
                            _activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // get all the messages
                                    _activity.getAllMessages();

                                    // clear the textbox
                                    _sendMsgBox.getText().clear();
                                }
                            });
                            running = false;
                        } else if (FacadeModule.getFacadeModule(_context).LastRequestResult()==-1) {
                            String response = FacadeModule.getFacadeModule(_context).GetResponse();
                            DisplayMessage(response);
                            running = false;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        running = false;
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        checker.start();

//        this.getAllMessages();
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

    // Precondition: has to be run in UI thread
    private void getAllMessages() {
        Log.d(TAG, "getAllMessages called");

        // Do request
        FacadeModule.getFacadeModule(this).SendRequestGetAllMessages();

        Thread checker = new Thread() {
            public void run () {
                boolean running = true;
                while (running == true) {
                    try {
                        if (FacadeModule.getFacadeModule(_context).LastRequestResult()==1) {
                            _activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _meeting = FacadeModule.getFacadeModule(_activity).GetMeeting();
                                    _messages = _meeting.mMessages;

                                    _adapter.setMessageList(_messages);
                                    scrollMyListViewToBottom();
                                }
                            });
                            running = false;
                        } 
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        running = false;
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        checker.start();
    }

//    // Precondition: has to be run in UI thread
//    private void getAllMessages() {
//        Log.d(TAG, "getAllMessages called");
//
//        //this.checkInternetConnection();
//
//        // Do request
//        FacadeModule.getFacadeModule(this).SendRequestGetAllMessages();
//        _meeting = FacadeModule.getFacadeModule(this).GetMeeting();
//        _messages = _meeting.mMessages;
//
//        _adapter.setMessageList(_messages);
//        scrollMyListViewToBottom();
//    }

    // Reference : http://stackoverflow.com/questions/3606530/listview-scroll-to-the-end-of-the-list-after-updating-the-list
    private void scrollMyListViewToBottom() {
        _listview.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                _listview.setSelection(_adapter.getCount() - 1);
            }
        });
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

        public void setMessageList(ArrayList<Message> messages){
            this.messengers.clear();
            this.messengers.addAll(messages);
            this.notifyDataSetChanged();
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

            TextView name = (TextView) v.findViewById(R.id.name_text);
            TextView message = (TextView) v.findViewById(R.id.message_text);

            if (type == 0) name.setText(Integer.toString(m.mUserId));
            else name.setText(Integer.toString(m.mToUserId));
            message.setText(m.mMessage);

            return v;
        }
    }


}
