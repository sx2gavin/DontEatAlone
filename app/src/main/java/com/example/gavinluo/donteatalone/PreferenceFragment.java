package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.Calendar;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreferenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreferenceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "PreferenceFragment";

    // keep track of the state of the preference page
    private static final int READ_MODE= 1;      // A preference already exists for the user
    private static final int EDIT_MODE = 2;     // No preference exists, the user is creating one
    private int state;

    // UI Components
    EditText _distanceEdit;
    EditText _minAgeEdit;
    EditText _maxAgeEdit;
    EditText _minPriceEdit;
    EditText _maxPriceEdit;
    EditText _commentEdit;
    Button _startTimeEdit;
    Button _endTimeEdit;
    RadioGroup _genderRadios;
    RadioButton _genderNoPrefRadio;
    Button _prefButton;
//    static EditText _distanceEdit;
//    static EditText _minAgeEdit;
//    static EditText _maxAgeEdit;
//    static EditText _minPriceEdit;
//    static EditText _maxPriceEdit;
//    static EditText _commentEdit;
//    static Button _startTimeEdit;
//    static Button _endTimeEdit;
//    static RadioGroup _genderRadios;
//    static RadioButton _genderNoPrefRadio;
//    static Button _prefButton;

    Context _context;
    Activity activity;

    // Time components
    private Timestamp startTime;
    private Timestamp endTime;

    private FacadeModule facade;
    private Preference preference;  // store the current preference set by the user

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PreferenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PreferenceFragment newInstance(String param1, String param2) {
        PreferenceFragment fragment = new PreferenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PreferenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        _context = this.getActivity();
        activity = this.getActivity();

        state = EDIT_MODE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preference, container, false);

        facade = FacadeModule.getFacadeModule(_context);

        // set the references
        _commentEdit = (EditText) view.findViewById(R.id.pref_comment);
        _distanceEdit = (EditText)view.findViewById(R.id.pref_distance);
        _startTimeEdit = (Button)view.findViewById(R.id.pref_start_time);
        _endTimeEdit = (Button)view.findViewById(R.id.pref_end_time);
        _genderRadios = (RadioGroup)view.findViewById(R.id.pref_gender);
        _genderNoPrefRadio = (RadioButton)view.findViewById(R.id.pref_sex_none);
        _minAgeEdit = (EditText)view.findViewById(R.id.pref_min_age);
        _maxAgeEdit = (EditText)view.findViewById(R.id.pref_max_age);
        _minPriceEdit = (EditText)view.findViewById(R.id.pref_min_price);
        _maxPriceEdit = (EditText)view.findViewById(R.id.pref_max_price);
        _prefButton = (Button)view.findViewById(R.id.pref_search_button);

        startTime = null;
        endTime = null;




        // attach the button click listeners
        _startTimeEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.pref_start_time:
                        Log.d(TAG, "start time button fired");
                        getStartTime(_startTimeEdit);
                        break;
                }
            }
        });

        _endTimeEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.pref_end_time:
                        Log.d(TAG, "end time button fired");
                        getEndTime(_endTimeEdit);
                        break;
                }
            }
        });

        _prefButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.pref_search_button:
                        if(state == EDIT_MODE) {
                            Log.d(TAG, "search button clicked");
                            if (checkInput()) {
                                // TODO: call facade to submit request
                                setPreference();
                                submitPreference();
//                                setState(READ_MODE);
                            }
                        } else { // it is in the read only mode
                            // TODO: delete preference on server
                            deletePreference();

                            // TODO: enable all components on the page

                            // change the state to edit
//                            setState(EDIT_MODE);
                        }
                        Log.d("tag", "state: "+state);
                        break;
                }
            }
        });

        retrievePreference();
        return view;
    }


    @Override
    public void onResume(){
        super.onResume();

//        try {
//            // retrieve and set the preference if there's one
//            retrievePreference();
//            Thread.sleep
//        } catch(){
//
//        }

        // Thread
        // TODO: wait a while before retrieving preference

//        Log.d("tag", "sent request retrieve preference");
//        Thread checker = new Thread() {
//            public void run () {
//                FacadeModule.getFacadeModule(_context).SendRequestGetPreference();
//                boolean running = true;
//                while (running == true) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        running = false;
//                        Thread.currentThread().interrupt();
//                    }
//                }
//            }
//        };
//        checker.start();

    }

    // Precondition: The preference needs to be set
    public void submitPreference(){
        facade.CreatePreference(facade.GetUserId(), preference.m_max_distance,
            preference.m_min_age, preference.m_max_age, preference.m_min_price,
            preference.m_max_price, preference.m_gender.substring(0,1),
            preference.m_comment, startTime, endTime);

        Thread checker = new Thread() {
            public void run () {
                boolean running = true;
                while (running == true) {
                    String response = FacadeModule.getFacadeModule(_context).GetResponseMessage();
                    try {
                        if (response.compareTo("Match successfully created.")==0) {

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setState(READ_MODE);
                                    DisplayMessage("Match successfully created.");
                                }
                            });
                            running = false;
                        } else if (response != "") {
                            DisplayMessage(response);
                            Log.d("tag", response);
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

    public void deletePreference(){
        int check = facade.DeletePreference();
        if(check==0){ // do not create a thread if no preference is found
            return;
        }

        Thread checker = new Thread() {
            public void run () {
                boolean running = true;
                while (running == true) {
                    String response = FacadeModule.getFacadeModule(_context).GetResponseMessage();
                    try {
                        if (response.compareTo("Match has been successfully deleted.")==0) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setState(EDIT_MODE);
                                    DisplayMessage("Match has been successfully deleted.");
                                }
                            });
                            running = false;
                        } else if (response.compareTo("") !=0 ) {
//                            setState(EDIT_MODE);
                            // create a toast to show the error message
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
    }

    // Precondition: should be ran on ui thread
    public void loadPreference(final Preference p) {
        String startTimeString = new SimpleDateFormat("hh:mm aa").format(p.m_start_time);
        String endTimeString = new SimpleDateFormat("hh:mm aa").format(p.m_end_time);
        _minAgeEdit.setText(p.m_min_age+"");
        _maxAgeEdit.setText(p.m_max_age+"");
        _startTimeEdit.setText(startTimeString);
        _endTimeEdit.setText(endTimeString);
        _minPriceEdit.setText(p.m_min_price+"");
        _maxPriceEdit.setText(p.m_max_price+"");
        _distanceEdit.setText(p.m_max_distance+"");
        _commentEdit.setText(p.m_comment);

        startTime = p.m_start_time;
        endTime = p.m_end_time;

        setState(READ_MODE);
    }

    public void retrievePreference(){
        // Thread

        Log.d("tag", "sent request retrieve preference");
        Thread checker = new Thread() {
            public void run () {
                FacadeModule.getFacadeModule(_context).SendRequestGetPreference();
                boolean running = true;
                final int TIMEOUT = 100;
                int  counter = 0;
                while (running == true) {
                    try {
                        if (FacadeModule.getFacadeModule(_context).LastRequestResult() != 0) {
                            counter += 1;
                            final Preference pref = FacadeModule.getFacadeModule(_context).GetPreference();
                            Log.d("tag", "retrieve-response: "+FacadeModule.getFacadeModule(_context).GetResponse());
                            if(pref.m_start_time==null && counter<TIMEOUT){
                                continue;
                            } else if (pref.m_end_time==null){
                                running = false;
                                continue;
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("tag", "load the preference");
                                    loadPreference(pref);
                                }
                            });
                            running = false;
                        }
//                        else if (FacadeModule.getFacadeModule(_context).LastRequestResult() !=0 ){
//                            // there's no preference set
//                            running = false;
//                        }
                        Thread.sleep(500);
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

    public void DisplayMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * disable all components
     *
     * Precondition: should be run on ui thread
     */
    public void setEnabledAllComponents(boolean enable){
        // disable all components
        _minAgeEdit.setEnabled(enable);
        _maxAgeEdit.setEnabled(enable);
        _startTimeEdit.setEnabled(enable);
        _endTimeEdit.setEnabled(enable);
        _minPriceEdit.setEnabled(enable);
        _maxPriceEdit.setEnabled(enable);
        _distanceEdit.setEnabled(enable);
        _commentEdit.setEnabled(enable);

        _genderRadios.findViewById(R.id.pref_male).setEnabled(enable);
        _genderRadios.findViewById(R.id.pref_female).setEnabled(enable);
        _genderRadios.findViewById(R.id.pref_sex_none).setEnabled(enable);
    }

    /**
     * set all components to default values
     *
     * Precondition: should be run on ui thread
     */
    public void setDefault(){
        _minAgeEdit.getText().clear();
        _maxAgeEdit.getText().clear();
        _minPriceEdit.getText().clear();
        _maxPriceEdit.getText().clear();
        _distanceEdit.getText().clear();
        _commentEdit.getText().clear();
        _startTimeEdit.setText("START");
        _endTimeEdit.setText("END");

        RadioButton radioButton = (RadioButton) _genderRadios.findViewById(R.id.pref_sex_none);
        radioButton.setChecked(true);
    }

    /**
     * set the state of the preference page
     *
     * Precondition: should be run on ui thread
     * @param newState
     */
    public void setState(int newState){
        Resources res = _context.getResources();

        if(newState == READ_MODE){
            setEnabledAllComponents(false);
            // change the button label
            _prefButton.setText(res.getString(R.string.pref_delete_pref));
        } else if (newState == EDIT_MODE){
            setEnabledAllComponents(true);
            setDefault();
            // change the button label
            _prefButton.setText(res.getString(R.string.pref_start_search));
        }
        state = newState;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    // Convert today's date with the input hour and minute to long
    public long toMilliSecond(int hour, int min){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date d1 = c.getTime(); //the midnight, that's the first second of the day.

        long milliSec = d1.getTime();
        milliSec += hour * 3600000;
        milliSec += min * 60000;

        return milliSec;
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
    }

    /*
     * OnClick Listeners
     */
    public void getStartTime(View view){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(_context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                long milliSec = toMilliSecond(selectedHour, selectedMinute);
                startTime = new Timestamp(milliSec);
                String startTimeString = new SimpleDateFormat("hh:mm aa").format(startTime);
                _startTimeEdit.setText(startTimeString);
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
        mTimePicker = new TimePickerDialog(_context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute){
                long milliSec = toMilliSecond(selectedHour, selectedMinute);
                endTime = new Timestamp(milliSec);
                String startTimeString = new SimpleDateFormat("hh:mm aa").format(endTime);
                _endTimeEdit.setText(startTimeString);
            }
        }, hour, minute, false); // use the 12 hour clock
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    // Precondition: the input has to be valid
    public void setPreference(){
        int id= _genderRadios.getCheckedRadioButtonId();
        View radioButton = _genderRadios.findViewById(id);
        RadioButton btn = (RadioButton) _genderRadios.getChildAt(_genderRadios.indexOfChild(radioButton));
        String pref_gender_selection = (String) btn.getText();
//        pref_gender_selection = pref_gender_selection.substring(0,1); // retrieve the very first letter of gender
//        char gender = pref_gender_selection.charAt(0);

        Integer pref_min_age = Integer.parseInt(_minAgeEdit.getText().toString());
        Integer pref_max_age = Integer.parseInt(_maxAgeEdit.getText().toString());
        Integer pref_min_price = Integer.parseInt(_minPriceEdit.getText().toString());
        Integer pref_max_price = Integer.parseInt(_maxPriceEdit.getText().toString());
        Integer pref_distance_int = Integer.parseInt(_distanceEdit.getText().toString());

        preference = new Preference(facade.GetUserId(), pref_distance_int,
                pref_min_age, pref_max_age, pref_min_price, pref_max_price,
                _commentEdit.getText().toString(), pref_gender_selection, startTime, endTime);
    }

    public boolean checkInput(){
        // angela.
        int id= _genderRadios.getCheckedRadioButtonId();
        View radioButton = _genderRadios.findViewById(id);
        RadioButton btn = (RadioButton) _genderRadios.getChildAt(_genderRadios.indexOfChild(radioButton));
        String pref_gender_selection = (String) btn.getText();
        pref_gender_selection = pref_gender_selection.substring(0,1); // retrieve the very first letter of gender

        //error checking
        if(_minAgeEdit.getText().toString().matches("")){
            Toast.makeText(_context, "Please enter the minimum age.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(_maxAgeEdit.getText().toString().matches("")){
            Toast.makeText(_context, "Please enter the maximum age.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(startTime == null){
            Toast.makeText(_context, "Please choose the start time.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(endTime == null){
            Toast.makeText(_context, "Please choose the end time.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(startTime.compareTo(endTime) >= 0){
            Toast.makeText(_context, "The start time is later or equal to the end time.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(_minPriceEdit.getText().toString().matches("")){
            Toast.makeText(_context, "Please enter the minimum price.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(_maxPriceEdit.getText().toString().matches("")){
            Toast.makeText(_context, "Please enter the maximum price.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (_distanceEdit.getText().toString().matches("")) {
            Toast.makeText(_context, "You did not enter a distance", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
    }

}
