package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by GavinLuo on 15-06-26.
 *
 */


public class FacadeModule {

    public final static String TAG = "TAG";
	
	private enum RequestMode {
		LOGIN, LOGOUT, SIGNUP, GET_PROFILE, UPDATE_PROFILE, UPDATE_USER, CREATE_PREFERENCE, DELETE_PREFERENCE, GET_MATCHLIST, GET_REQUESTLIST, INVITE_USER, GET_MEETING, OTHER
	}

    private static FacadeModule mInstance;
    private MySingleton mMySingleton;
    // private FacadeModule mContext;
    private Context mContext;
	private int mMatchId;
	private String mFacebookId;
	private String mGCMToken;
    private JSONObject mSavedJSON;
    private Profile mUserProfile;
	private ArrayList<User> mMatchList;
	private ArrayList<User> mRequestList;
	private Preference mPreference;
	private GPSTracker mGPS;
	private int mRequestResult;

    // constructor
    private FacadeModule(Context context)
    {
		mRequestResult = 0;
        mContext = context;
        mSavedJSON = null;
        mMySingleton = MySingleton.getMySingleton(context);
		mUserProfile = new Profile();
		mMatchId = -1;
		mGPS = new GPSTracker(context);
    }

    public void SendRequest(String url, int method, final RequestMode request)
    {
        Log.d(TAG, "SendRequest called");
		mSavedJSON = null;
		mRequestResult = 0;
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						String message = "";
						try {
							Log.d(TAG, "Getting the message.");
							// message = (String) response.get("message");
							mSavedJSON = new JSONObject(response.toString());
							if (mSavedJSON == null) {
								Log.d(TAG, "mSavedJSON is null");
								mRequestResult = -1;
							} else {
								Log.d(TAG, response.toString());
								mRequestResult = 1;
								ParseResponse(request);
							}
						} catch (JSONException e) {
							Log.d(TAG, "There is an JSON exception.");
							e.printStackTrace();
							mRequestResult = -1;
							return;
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "ErrorResponse called");
						mRequestResult = -1;
						NetworkResponse response = error.networkResponse;
						if(response != null && response.data != null) {
							String message = new String(response.data);
							String errorMessage;
							try {
								mSavedJSON = new JSONObject(message);
								// errorMessage = mSavedJSON.getString("message");
								Log.d(TAG, message);
							} catch(JSONException e) {
								e.printStackTrace();
								return;
							}
						}
					}
				});

		mMySingleton.addToRequestQueue(jsonObjectRequest);
    }

	public void SendRequestForMatchList() 
	{
		mMatchList = new ArrayList<User> ();
		String url = "http://donteatalone.paigelim.com/api/v1/users/" + mUserProfile.GetId() + "/matches";
		SendRequest(url, Request.Method.GET, RequestMode.GET_MATCHLIST);
	}

	public void SendRequestForRequestList()
	{
		mRequestList = new ArrayList<User> ();
		String url = "http://donteatalone.paigelim.com/api/v1/requests/" + mUserProfile.GetId() + "/retrieved";
		SendRequest(url, Request.Method.GET, RequestMode.GET_REQUESTLIST);
	}

	public void SendRequestInviteUser(int otherUserId)
	{
		String url = "http://donteatalone.paigelim.com/api/v1/requests?" + 
			"user_id=" + mUserProfile.GetId() +
			"&to_user_id=" + Integer.toString(otherUserId) +
			"&match_id=" + Integer.toString(mMatchId);

		SendRequest(url, Request.Method.POST, RequestMode.INVITE_USER);
	}

	public void AcceptUserRequest(int requestId)
	{
		String url = "http://donteatalone.paigelim.com/api/v1/requests/" + Integer.toString(requestId) + "/accept";
		SendRequest(url, Request.Method.POST, RequestMode.OTHER);	
	}

	public void DeleteUserRequest(int requestId)
	{
		String url = "http://donteatalone.paigelim.com/api/v1/requests/" + Integer.toString(requestId);
		SendRequest(url, Request.Method.DELETE, RequestMode.OTHER);
	}

    public int CreatePreference(int user_id, int max_distance, int min_age, int max_age, int min_price, int max_price, String gender, String comment, Timestamp start_time, Timestamp end_time)
    {
        String start_time_string = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start_time);
        String end_time_string = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end_time);

		double latitude;
		double longitude;

		if (mGPS.canGetLocation()) {
			latitude = mGPS.getLatitude();
			longitude = mGPS.getLongitude();
		} else {
			return 0;
		}

		mPreference = new Preference(user_id, max_distance, min_age, max_age, min_price, max_price, gender, comment, start_time, end_time);
        String url = "http://donteatalone.paigelim.com/api/v1/matches?" +
					 "user_id=" + Integer.toString(user_id) +
					 "&latitude=" + Double.toString(latitude) +
					 "&longtitude=" + Double.toString(longitude) +
					 "&max_distance=" + Integer.toString(max_distance) +
					 "&min_age=" + Integer.toString(min_age) +
					 "&max_age=" + Integer.toString(max_age) +
					 "&min_price=" + Integer.toString(min_price) +
					 "&max_price=" + Integer.toString(max_price) +
					 "&gender=" + gender +
					 "&comment=" + comment +
					 "&start_time=" + start_time_string +
					 "&end_time=" + end_time_string;
		url = url.replace(" ", "%20");
		SendRequest(url, Request.Method.POST, RequestMode.CREATE_PREFERENCE);
		return 1;
	}

	// return value 0: error: Preference has not been initialized.
	// return value 1: Delete the current preference.
	public int DeletePreference()
	{
		mPreference = null;
		if (mMatchId == -1) {
			return 0;
		} else {
			String url = "http://donteatalone.paigelim.com/api/v1/matches/" + Integer.toString(mMatchId);	
			SendRequest(url, Request.Method.DELETE, RequestMode.DELETE_PREFERENCE);
			return 1;
		}
	}

	public Preference GetPreference()
	{
		return mPreference;
	}

	public void SendRequestLogIn(String email, String password)
	{
		String url = "http://donteatalone.paigelim.com/api/v1/login?" +
			"email=" + email +
			"&password=" + password;
		SendRequest(url, Request.Method.GET, RequestMode.LOGIN);
	}

	public void SendRequestGetProfile(int user_id)
	{
		String url = "http://donteatalone.paigelim.com/api/v1/users/" + Integer.toString(user_id);
		SendRequest(url, Request.Method.GET, RequestMode.GET_PROFILE);
	}
	
	public void SendRequestSignUp(String email, String password, String password_confirm, String name, String age)
	{
        String url ="http://donteatalone.paigelim.com/api/v1/users?" +
                "email=" + email +
                "&password=" + password +
                "&password_confirmation=" + password_confirm +
                "&name=" + name +
                "&age=" + age;
		SendRequest(url, Request.Method.POST, RequestMode.SIGNUP);
	}	

    public void SendRequestLogOut()
    {
        
		String url = "http://donteatalone.paigelim.com/api/v1/logout";
		SendRequest(url, Request.Method.GET, RequestMode.LOGOUT);
    }

    public Profile GetUserProfile()
    {
        return mUserProfile;
    }

    public void UpdateUserProfile(Profile newProfile)
    {
        mUserProfile = newProfile;

		String url = "http://donteatalone.paigelim.com/api/v1/profiles/" + Integer.toString(mUserProfile.GetId()) +
			"?name=" + mUserProfile.GetName() +
			"&image_url=" + mUserProfile.GetImageUrl() +
			"&gender=" + mUserProfile.GetGender() +
			"&age=" + Integer.toString(mUserProfile.GetAge()) +
			"&description=" + mUserProfile.GetDescription();
		
		SendRequest(url, Request.Method.PUT, RequestMode.UPDATE_PROFILE);
    }

    public int GetUserId()
    {
        return mUserProfile.GetId();
    }

    public String GetResponse()
    {
        if (mSavedJSON == null) {
            return "";
        } else if(mSavedJSON.has("error")) {
			return "error";
		} else {
            return mSavedJSON.toString();
        }
    }

	public int LastRequestResult()
	{
		return mRequestResult;	
	}	

	public String GetResponseMessage()
	{
		if (mSavedJSON == null) {
			return "";
		} else if (mSavedJSON.has("message")) {

			try {
				String message = mSavedJSON.getString("message");
				return message;
			} catch (JSONException e) {
				e.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}

	public int ParseResponse(RequestMode request)
	{
		if (mSavedJSON == null) {
			return 0;
		} else {
            try {
				if (request == RequestMode.GET_MATCHLIST) {
					JSONArray matches = mSavedJSON.getJSONArray("matches");
					for (int i = 0; i < matches.length(); i++) {
						User newUser = new User();
						JSONObject userInfo = matches.getJSONObject(i);
						newUser.setId(Integer.parseInt(userInfo.getString("id")));
						newUser.setName(userInfo.getJSONObject("profile").getString("name"));
						newUser.setGender(userInfo.getString("gender"));
						newUser.setMaxDistance(Float.parseFloat(userInfo.getString("max_distance")));
						newUser.setLatitude(Float.parseFloat(userInfo.getString("latitude")));
						newUser.setLongitude(Float.parseFloat(userInfo.getString("longitude")));
						// newUser.setDistance(userInfo.getDouble("distance"));
						newUser.setMinAge(Integer.parseInt(userInfo.getString("min_age")));
						newUser.setMaxAge(Integer.parseInt(userInfo.getString("max_age")));
						newUser.setMinPrice(Float.parseFloat(userInfo.getString("min_price")));
						newUser.setMaxPrice(Float.parseFloat(userInfo.getString("max_price")));
						newUser.setStartTime(userInfo.getString("start_time"));
						newUser.setEndTime(userInfo.getString("end_time"));
						newUser.setLikes(Integer.parseInt(userInfo.getJSONObject("profile").getString("likes")));
						newUser.setDislikes(Integer.parseInt(userInfo.getJSONObject("profile").getString("dislikes")));
						mMatchList.add(newUser);
					}
				} else if (request == RequestMode.GET_REQUESTLIST) {
					JSONArray requestList = mSavedJSON.getJSONArray("requests");
					for (int i = 0; i < requestList.length(); i++) {
						User newUser = new User();
						JSONObject userInfo = requestList.getJSONObject(i);
						newUser.setId(Integer.parseInt(userInfo.getString("id")));
						newUser.setName(userInfo.getJSONObject("profile").getString("name"));
						newUser.setGender(userInfo.getJSONObject("profile").getString("gender"));
						newUser.setMaxDistance(Float.parseFloat(userInfo.getJSONObject("match").getString("max_distance")));
						newUser.setLatitude(Float.parseFloat(userInfo.getJSONObject("match").getString("latitude")));
						newUser.setLongitude(Float.parseFloat(userInfo.getJSONObject("match").getString("longitude")));
						newUser.setDistance(userInfo.getJSONObject("match").getDouble("distance"));
						newUser.setMinAge(Integer.parseInt(userInfo.getJSONObject("match").getString("min_age")));
						newUser.setMaxAge(Integer.parseInt(userInfo.getJSONObject("match").getString("max_age")));
						newUser.setMinPrice(Float.parseFloat(userInfo.getJSONObject("match").getString("min_price")));
						newUser.setMaxPrice(Float.parseFloat(userInfo.getJSONObject("match").getString("max_price")));
						newUser.setStartTime(userInfo.getJSONObject("match").getString("start_time"));
						newUser.setEndTime(userInfo.getJSONObject("match").getString("end_time"));
						newUser.setLikes(Integer.parseInt(userInfo.getJSONObject("profile").getString("likes")));
						newUser.setDislikes(Integer.parseInt(userInfo.getJSONObject("profile").getString("dislikes")));
						mRequestList.add(newUser);	
					}	
				} else if (request == RequestMode.CREATE_PREFERENCE) {
					mMatchId = mSavedJSON.getJSONObject("match").getInt("id");
				} else if (request == RequestMode.GET_PROFILE) {
					Log.d(TAG, "LOGIN parsing");
					JSONObject userProfile = mSavedJSON.getJSONObject("user").getJSONObject("profile");
					mUserProfile.SetId(userProfile.getInt("id"));
					mUserProfile.SetName(userProfile.getString("name"));
					mUserProfile.SetImageUrl(userProfile.getString("image_url"));
					mUserProfile.SetGender(userProfile.getString("gender"));
					mUserProfile.SetAge(userProfile.getInt("age"));
					mUserProfile.SetDescription(userProfile.getString("description"));
					mUserProfile.LogProfile();
				} else if (request == RequestMode.LOGIN) {
					mUserProfile.SetId(mSavedJSON.getJSONObject("data").getJSONObject("user").getInt("id"));
					mUserProfile.SetEmail(mSavedJSON.getJSONObject("data").getJSONObject("user").getString("email"));
					mGCMToken = mSavedJSON.getJSONObject("data").getJSONObject("user").getString("gcm_token");
					Log.d(FacadeModule.TAG, mGCMToken);
				} else if (request == RequestMode.GET_MEETING) {
					JSONArray meetingList = mSavedJSON.getJSONArray("meetings");
					if (meetingList.length() == 0) {
						
					}	
				}
            } catch (JSONException e) {
                e.printStackTrace();
				Log.d(FacadeModule.TAG, "There are JSONExceptions.");
                return 0;
            }

            return 1;
		}
	}

	public ArrayList<User> GetMatchList()
	{
		return mMatchList;
	}

	public ArrayList<User> GetRequestList()
	{
		return mRequestList;
	}

    public boolean LoggedIn()
    {
        if (mSavedJSON == null) {
            return false;
        } else {
            try {
                String LoginMessage = mSavedJSON.getString("message");
                if (LoginMessage.equals("Login successful!")) {
                    return true;
                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // public boolean

    public static synchronized FacadeModule getFacadeModule(Context context)
    {
        if (mInstance == null) {
            mInstance = new FacadeModule(context);
        }
        return mInstance;
    }

	public void UpdateGCMToken(String token)
	{
		Log.d(FacadeModule.TAG, "UpdateGCMToken called.");
		Log.d(FacadeModule.TAG, Integer.toString(mUserProfile.GetId()));

		mGCMToken = token;
		String url = "http://donteatalone.paigelim.com/api/v1/users/" + Integer.toString(mUserProfile.GetId()) +
				"?gcm_token=" + token;

		SendRequest(url, Request.Method.PUT, RequestMode.UPDATE_USER);
	}

	public void UpdateFacebookId(String facebookId)
	{
		mFacebookId = facebookId;
		String url = "http://donteatalone.paigelim.com/api/v1/users/" + Integer.toString(mUserProfile.GetId()) +
				"?facebook_id=" + facebookId;

		SendRequest(url, Request.Method.PUT, RequestMode.UPDATE_USER);
	}


	public void SendRequestGetMeeting()
	{
		String url = "http://donteatalone.paigelim.com/api/v1/users/" + Integer.toString(mUserProfile.GetId()) + "/meetings";
		SendRequest(url, Request.Method.GET, RequestMode.GET_MEETING);
	}
}
