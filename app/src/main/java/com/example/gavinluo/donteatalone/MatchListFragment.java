package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * A nice example of a expandable list fragment
 * https://gist.github.com/bowmanb/4052030
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MatchListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MatchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchListFragment extends Fragment {
    private final String TAG = "MatchListFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static ExpandableListView matchListExpand;
    private static MatchListAdapter listAdapter;
    private Context context;

    private FacadeModule facade;
    private static boolean stopFetching;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchListFragment.
     */
    public static MatchListFragment newInstance(String param1, String param2) {
        MatchListFragment fragment = new MatchListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MatchListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getActivity();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_match_list, container, false);
        matchListExpand = (ExpandableListView) view.findViewById(R.id.match_list_expandable);
        listAdapter = new MatchListAdapter();
        matchListExpand.setAdapter(listAdapter);
//        facade = FacadeModule.getFacadeModule(this.context);

        // called in activity
        stopFetching = true;
//        startUpdate();

//        addTestData();

        return view;
    }

    public void stopUpdate(){
        stopFetching = true;
    }

    public void startUpdate(){
//        stopFetching = false;
        if(!stopFetching){
            // check if update already started
            return;
        }
        stopFetching = false;

        Thread looper = new Thread() {
            public void run() {
//                String response = null;
//                final int TIMEOUT = 3;
//                int counter = 0;

                // infinite loop to keep checking for new matches
                while(!stopFetching) {
                    // create a new thread if the response is empty
//                    if(response == null || response.compareTo("")!=0){
                        try {
                            FacadeModule.getFacadeModule(context).SendRequestForMatchList();
                            Thread checker = new Thread() {
                                public void run() {
                                    boolean running = true;
                                    while (!stopFetching && running == true) {
//                                        String response = FacadeModule.getFacadeModule(context).GetResponseMessage();
                                        try {
                                            // Get the match list
//                                            if (FacadeModule.getFacadeModule(context).GetResponse().compareTo("") != 0) {
                                              if(FacadeModule.getFacadeModule(context).LastRequestResult() != 0){
                                                  final ArrayList matches = FacadeModule.getFacadeModule(context).GetMatchList();

                                                  if(matches != null) {
                                                      Log.d("tag", "matches-size:" + matches.size());
                                                      Log.d("tag", "response: " + FacadeModule.getFacadeModule(context).GetResponse());
//                                                  listAdapter.setUserList(matches);
                                                      getActivity().runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              listAdapter.setUserList(matches);
//                                                              listAdapter.notifyDataSetChanged();
                                                              Log.d("tag", "actual list size: " + listAdapter.getUserList().size());
                                                          }
                                                      });
                                                  }

                                                  running = false;
                                              }

                                            // Sleep for 100 milliseconds
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            running = false;
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                            };
                            checker.start();

                            // sleep for 5 seconds
                            Thread.sleep(5001);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
//                    }

//                    response = FacadeModule.getFacadeModule(context).GetResponseMessage();
//                    counter += 1;
                }
            }
        };
        looper.start();
    }
//    public void startUpdate(){
//        stopFetching = false;
//        if(facade == null){
//            facade = FacadeModule.getFacadeModule(context);
//        }
//
//        Thread looper = new Thread() {
//            public void run() {
//                String response = "";
//
//                // infinite loop to keep checking for new matches
//                while(!stopFetching) {
//                    // create a new thread if the response is empty
//                    if(response.compareTo("")==0){
//                        try {
//                            facade.SendRequestForMatchList();
//                            Thread checker = new Thread() {
//                                public void run() {
//                                    boolean running = true;
//                                    while (running == true) {
//                                        String response = facade.GetResponseMessage();
//                                        try {
//                                            // Get the match list
//                                            if (facade.GetResponse().compareTo("") != 0) {
//                                                ArrayList matches = facade.GetMatchList();
//
//                                                Log.d("tag", "matches-size:" +  matches.size());
//                                                Log.d("tag", "response: " + facade.GetResponse());
//                                                listAdapter.setUserList(matches);
//                                                Log.d("tag", "actual list size: " + listAdapter.getUserList().size());
//                                                running = false;
//                                            }
//
//                                            Thread.sleep(1000);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                            running = false;
//                                            Thread.currentThread().interrupt();
//                                        }
//                                    }
//                                }
//                            };
//                            checker.start();
//
//                            // sleep for 10 seconds
//                            Thread.sleep(10001);
//                        } catch (InterruptedException e){
//                            e.printStackTrace();
//                            Thread.currentThread().interrupt();
//                        }
//                    }
//                }
//            }
//        };
//        looper.start();
//    }

    // add 2 test users
    public void addTestData(){
        User user1 = new User();
        user1.setId(1);
        user1.setName("test user 1");
        user1.setGender("male");
        user1.setMaxDistance(3);
        user1.setLatitude(23);
        user1.setLongitude(32);
        user1.setDistance(3);
        user1.setMinAge(10);
        user1.setMaxAge(100);
        user1.setMinPrice(1);
        user1.setMaxPrice(1000);
        user1.setStartTime("10:30am");
        user1.setEndTime("11:59pm");
        user1.setLikes(110);
        user1.setDislikes(2);

        User user2 = new User();
        user2.setId(2);
        user2.setName("test user 2");
        user2.setGender("male");
        user2.setMaxDistance(5);
        user2.setLatitude(54);
        user2.setLongitude(32);
        user2.setDistance(4);
        user2.setMinAge(20);
        user2.setMaxAge(200);
        user2.setMinPrice(2);
        user2.setMaxPrice(2000);
        user2.setStartTime("12:30am");
        user2.setEndTime("10:59pm");
        user2.setLikes(12);
        user2.setDislikes(234);

        this.listAdapter.addUser(user1);
        this.listAdapter.addUser(user2);
    }

    public void DisplayMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateData(JSONObject response){
        // TODO: Parse the data and add that to the list adapter
//
//        final String MATCHES = "matches";
//        final String ID = "id";
//
//        // remove this after testing
//        try {
//            listAdapter.clearMatches(); // clear things in adapter TODO: remove and add element as necessary
//
//            // Getting all the matches
//            JSONArray matches = response.getJSONArray(MATCHES);
//
//            // Add the IDs to the adapter
//            for(int i=0; i<matches.length(); i++){
//                JSONObject match = matches.getJSONObject(i);
//
//                // TODO: Remove this after testing
//                listAdapter.addMatches("id: " + match.getString(ID));
//                Log.d(TAG, "id: " + match.getString(ID));
//            }
//            Log.d(TAG, "no matches?") ;
//        } catch (Exception e){
//            Log.d(TAG, e.toString());
//        }
    }

    public void inviteUser(Integer otherId, final Button inviteButton){
        // pause the update
        stopUpdate();

        FacadeModule.getFacadeModule(context).SendRequestInviteUser(otherId);

        Thread checker = new Thread() {
            public void run () {
                boolean running = true;
                while (running == true) {
                    String response = FacadeModule.getFacadeModule(context).GetResponseMessage();
                    try {
                        if (FacadeModule.getFacadeModule(context).LastRequestResult()==1) {
                            DisplayMessage("User Invited Successfully");
                            running = false;
                        } else if (FacadeModule.getFacadeModule(context).LastRequestResult()==-1) {
                            DisplayMessage(response);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // enabled the ui button to resend the invitation
                                    inviteButton.setEnabled(true);
                                }
                            });
                            running = false;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        running = false;
                        Thread.currentThread().interrupt();
                    } finally{
                        // resume the updating of the match list
                        startUpdate();
                    }
                }
            }
        };
        checker.start();
    }

    public MatchListAdapter getListAdapter(){
        return listAdapter;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        stopFetching = true;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        stopFetching = true;
    }

    /**
     * The list adapter for expandable list
     */
    public class MatchListAdapter extends UserListAdapter {

        public MatchListAdapter(){
            super();
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.matchesgrouprow, null);
            }

            // get the user object
            final User user = this.getGroup(i);

            TextView basicInfoView = (TextView) view.findViewById(R.id.matches_group_basic_info);
            TextView thumbsUpView = (TextView) view.findViewById(R.id.matches_group_thumb_up);
            TextView thumbsDownView = (TextView) view.findViewById(R.id.matches_group_thumb_down);
            final ImageView image = (ImageView) view.findViewById(R.id.group_profile_image);
            String url = user.getImageUrl();
            new DownloadImageTask((ImageView)image).execute(url);

            final Button inviteButton = (Button) view.findViewById(R.id.matches_group_invite);

            // format the strings
            Resources res = context.getResources();
            String basicInfoText = String.format(res.getString(R.string.matches_user_basic_info),
                    user.getName());

            final View wholeView = view;

            // set the text
            basicInfoView.setText(basicInfoText);
            thumbsUpView.setText(user.getLikes() + "");
            thumbsDownView.setText(user.getDislikes() + "");

            // add event listener to the invite button
            final Button listenerBtn = inviteButton;
            inviteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.matches_group_invite:
//                            ImageView image = (ImageView) wholeView.findViewById(R.id.group_profile_image);
//                            loadImage(image, "https://s-media-cache-ak0.pinimg.com/736x/a1/e3/6b/a1e36bcb8ce179bd8cc8db28ff4ef6fb.jpg");
//                            String url = "https://s-media-cache-ak0.pinimg.com/736x/a1/e3/6b/a1e36bcb8ce179bd8cc8db28ff4ef6fb.jpg";
//                            new DownloadImageTask((ImageView)image).execute(url);

                            // disable button when clicked
                            listenerBtn.setEnabled(false);

                            // invite the user
                            inviteUser(user.getId(), listenerBtn);

                            Log.d(TAG, "invite button event fired");
                            break;
                    }
                }
            });

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.matcheschildrow, null);
            }

            // get the user object
            User user = this.getGroup(i);

            TextView distView = (TextView) view.findViewById(R.id.matches_distance);
            TextView timeView = (TextView) view.findViewById(R.id.matches_time);
            TextView priceView = (TextView) view.findViewById(R.id.matches_price);
            TextView commentView = (TextView) view.findViewById(R.id.matches_comment);

            // format the strings
            Resources res = context.getResources();
            String distText = String.format(res.getString(R.string.matches_distance), String.format("%.1f", user.getDistance()));
            String timeText = String.format(res.getString(R.string.matches_time), user.getStartTime(), user.getEndTime());
            String priceText = String.format(res.getString(R.string.matches_price), String.format("%.2f", user.getMinPrice()), String.format("%.2f", user.getMaxPrice()));
            String commentText = String.format(res.getString(R.string.matches_comment), "testing testing");

            // set the text
            distView.setText(distText);
            timeView.setText(timeText);
            priceView.setText(priceText);
            commentView.setText(commentText);

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
