package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import java.util.Date;

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

    // UI Components
    EditText _distanceEdit;
    EditText _minAgeEdit;
    EditText _maxAgeEdit;
    EditText _minPriceEdit;
    EditText _maxPriceEdit;
    Button _startTimeEdit;
    Button _endTimeEdit;
    RadioGroup _genderRadios;
    RadioButton _genderNoPrefRadio;
    Button _prefButton;

    Context _context;

    // Time components
    private int _startHour = -1;
    private int _startMin = -1;
    private int _endHour = -1;
    private int _endMin = -1;

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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preference, container, false);

        // set the references
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


        // attach the button click listeners
        _startTimeEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.pref_start_time:
                        Log.d(TAG, "strt time button fired");
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
                        Log.d(TAG, "search button clicked");
                        if(checkInput()) {
                            // TODO: call facade to submit request
                        }
                        break;
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
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
        mTimePicker = new TimePickerDialog(_context, new TimePickerDialog.OnTimeSetListener() {
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

        hour = hour % 12 ;
        if(hour == 0){
            hour = 12;
        }

        if(min/10 == 0){
            return hour + ":0" + min + ending;
        }

        return hour + ":" + min + ending;
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

        if(_startHour == -1 ){
            Toast.makeText(_context, "Please choose the start time.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(_endHour == -1 ){
            Toast.makeText(_context, "Please choose the end time.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(_startHour>_endHour || (_startHour==_endHour&&_startMin>=_endHour)) {
            Toast.makeText(_context, "Your start time is later than your end time.", Toast.LENGTH_LONG).show();
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