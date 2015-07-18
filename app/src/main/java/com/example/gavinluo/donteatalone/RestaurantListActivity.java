package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RestaurantListActivity extends ActionBarActivity {
    private static final String TAG = "RestaurantListActivity";
    private static final String CARD_DETAIL = "card_detail";

    private Button customLocButton;
    private Context context;

    private RestaurantListAdapter listAdapter ;

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

    private int PLACE_PICKER_REQUEST;

    private ExpandableListView restaurantListExpand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        context = this;

        customLocButton = (Button)findViewById(R.id.custom_loc_button);

        customLocButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.custom_loc_button:
                        pickPlace();
                        Log.d(TAG, "Set location clicked!");
                        break;
                }
            }
        });


        // Inflate the layout for this fragment
        restaurantListExpand = (ExpandableListView) findViewById(R.id.match_list_expandable);
        listAdapter = new RestaurantListAdapter();
        restaurantListExpand.setAdapter(listAdapter);

        // TODO: remove after testing
        addTestData();
    }

    public void addTestData(){
        listAdapter.addRestaurant("Restaurant 1");
        listAdapter.addRestaurant("Restaurant 2");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_list, menu);
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

    public void pickPlace() {
        // BEGIN_INCLUDE(intent)
        /* Use the PlacePicker Builder to construct an Intent.
        Note: This sample demonstrates a basic use case.
        The PlacePicker Builder supports additional properties such as search bounds.
         */
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(context);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

            // Hide the pick option in the UI to prevent users from starting the picker
            // multiple times.
//            showPickAction(false);
            Log.d(TAG, "picked place");

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
            Log.d(TAG, "error: " + e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(context, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "error: " + e.toString());
        }

        // END_INCLUDE(intent)
    }

    /**
     * Extracts data from PlacePicker result.
     * This method is called when an Intent has been started by calling
     * {@link #startActivityForResult(android.content.Intent, int)}. The Intent for the
     * {@link com.google.android.gms.location.places.ui.PlacePicker} is started with
     * {@link #REQUEST_PLACE_PICKER} request code. When a result with this request code is received
     * in this method, its data is extracted by converting the Intent data to a {@link Place}
     * through the
     * {@link com.google.android.gms.location.places.ui.PlacePicker#getPlace(android.content.Intent,
     * android.content.Context)} call.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Enter onActivityResult");
        // BEGIN_INCLUDE(activity_result)
        if (requestCode == REQUEST_PLACE_PICKER) {
            // This result is from the PlacePicker dialog.
            if (resultCode == Activity.RESULT_OK) {
                /* User has picked a place, extract data.
                   Data is extracted from the returned intent by retrieving a Place object from
                   the PlacePicker.
                 */
                final Place place = PlacePicker.getPlace(data, this);

                /* A Place object contains details about that place, such as its name, address
                and phone number. Extract the name, address, phone number, place ID and place types.
                 */
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();

                String attribution = PlacePicker.getAttributions(data);
                if(attribution == null){
                    attribution = "";
                }

                // add more details
                final int priceLevel = place.getPriceLevel();
                final float rating = place.getRating();
                final Uri website = place.getWebsiteUri();
                final LatLngBounds viewport = place.getViewport();
                final LatLng latLng = place.getLatLng();
                final Locale locale = place.getLocale();
                final List<Integer> placeTypes = place.getPlaceTypes();

                // show then in the log cat
                Log.d(CARD_DETAIL, "place id: " + placeId);
                Log.d(CARD_DETAIL, "price level: " + priceLevel);
                Log.d(CARD_DETAIL, "rating: " + rating);
                Log.d(CARD_DETAIL, "website: " + website);
                Log.d(CARD_DETAIL, "viewport: " + viewport);
                Log.d(CARD_DETAIL, "latLng: " + latLng);
                Log.d(CARD_DETAIL, "locale: " + locale);
                Log.d(CARD_DETAIL, "placeTypes: " + placeTypes);
                Log.d(CARD_DETAIL, "attribution: " + attribution);
                Log.d(CARD_DETAIL, "attribution html: " + Html.fromHtml(attribution));
            }
            else {
                // User has not selected a place
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // END_INCLUDE(activity_result)
    }

    /**
     * The list adapter for expandable list
     */
    public class RestaurantListAdapter extends BaseExpandableListAdapter {

        private ArrayList<String> restaurantList;

        public RestaurantListAdapter(){
            super();
            this.restaurantList = new ArrayList<String>();
        }

        public void addRestaurant(String name){
            restaurantList.add(name);
        }

        @Override
        public int getGroupCount() {
            return this.restaurantList.size();
        }

        @Override
        public int getChildrenCount(int index){
            if(index >=0 && index < this.restaurantList.size()){
                return 1;
            }
            return 0;
        }

        @Override
        public Object getGroup(int i){
            return this.restaurantList.get(i);
        }

        @Override
        public Object getChild(int i, int j){
            return this.restaurantList.get(i);
        }

        @Override
        public long getGroupId(int i){
            return i;
        }

        @Override
        public long getChildId(int i, int j){
            return j;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.restaurantgrouprow, null);
            }

            // TODO: get the restaurant object

            TextView nameView = (TextView) view.findViewById(R.id.restaurant_name);
            RatingBar ratingView = (RatingBar) view.findViewById(R.id.restaurant_rating);

            nameView.setText("Restaurant name1");
            ratingView.setRating(3.1f);

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.restaurantchildrow, null);
            }

            // TODO: get the restaurant object

            TextView addressView = (TextView) view.findViewById(R.id.restaurant_address);
            TextView phoneView = (TextView) view.findViewById(R.id.restaurant_phone);
            TextView webView = (TextView) view.findViewById(R.id.restaurant_website);
            Button directionBtn = (Button) view.findViewById(R.id.restaurant_direction_button);

            // format the strings
            Resources res = context.getResources();
            String addressText = String.format(res.getString(R.string.rest_address), "test avenue");
            String phoneText = String.format(res.getString(R.string.rest_phone), "(432)-321-3293");

            // set the text
            addressView.setText(addressText+"test");
            phoneView.setText(phoneText+"test");

            final View wholeView = view;

            // add event listener to the invite button
            directionBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.restaurant_direction_button:
                            // Open google map in internet
                            String url = "http://maps.google.com/maps/place?cid=10281119596374313554";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);

                            Log.d(TAG, "direction button event fired");
                            break;
                    }
                }
            });

            return view;
        }
    }
}
