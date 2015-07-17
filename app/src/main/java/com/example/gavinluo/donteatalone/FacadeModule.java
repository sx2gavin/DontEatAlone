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
		LOGIN, LOGOUT, SIGNUP, GET_PROFILE, UPDATE_PROFILE, CREATE_PREFERENCE, DELETE_PREFERENCE, GET_MATCHLIST
	}
    private static FacadeModule mInstance;
    private MySingleton mMySingleton;
    // private FacadeModule mContext;
    private Context mContext;
    private int mUserId;
	private int mMatchId;
	private int mFacebookId;
	private String mGCMToken;
    private JSONObject mSavedJSON;
    private Profile mUserProfile;
	private ArrayList<User> mMatchList;

    // constructor
    private FacadeModule(Context context)
    {
        mContext = context;
        mSavedJSON = null;
        mMySingleton = MySingleton.getMySingleton(context);
        mMatchList = new ArrayList<User> ();
		mUserProfile = new Profile();
		mMatchId = -1;
		mUserId = -1;
    }

    public void SendRequest(String url, int method, final RequestMode request)
    {
        Log.d(TAG, "SendRequest called");

        //try {
            //mSavedJSON = new JSONObject("{" +
                    //"\"matches\": [" +
                    //"{\"id\": \"413\"," +
                    //"	\"user_id\": \"413\"," +
                    //"	\"max_distance\": \"5153\"," +
                    //"	\"latitude\": \"-71.794353\"," +
                    //"	\"longitude\": \"-135.657826\"," +
                    //"	\"distance\": 2271.6234775433," +
                    //"	\"min_age\": \"13\"," +
                    //"	\"max_age\": \"32\"," +
                    //"	\"min_price\": \"1\"," +
                    //"	\"max_price\": \"34\"," +
                    //"	\"comment\": \"Quo voluptate et enim soluta omnis accusantium alias autem cupiditate cupiditate.\"," +
                    //"	\"gender\": \"M\"," +
                    //"	\"start_time\": \"2015-06-16 03:56:54\"," +
                    //"	\"end_time\": \"2015-06-16 05:16:28\"," +
                    //"	\"profile\": {" +
                    //"		\"id\": \"413\"," +
                    //"		\"user_id\": \"413\"," +
                    //"		\"name\": \"Dr. Don Collier\"," +
                    //"		\"image_url\": \"http://lorempixel.com/640/480/cats/?95650\"," +
                    //"		\"gender\": \"M\"," +
                    //"		\"likes\": \"100\"," +
                    //"		\"dislikes\": \"85\" " +
                    //"}" +
                    //"}," +
                    //"{" +
                    //"		\"id\": \"925\"," +
                    //"		\"user_id\": \"925\"," +
                    //"		\"max_distance\": \"8074\"," +
                    //"		\"latitude\": \"-60.298905\"," +
                    //"		\"longitude\": \"-43.911683\"," +
                    //"		\"distance\": 2953.9484421482," +
                    //"		\"min_age\": \"17\"," +
                    //"		\"max_age\": \"53\"," +
                    //"		\"min_price\": \"9\"," +
                    //"		\"max_price\": \"30\"," +
                    //"		\"comment\": \"Laudantium repudiandae dolore assumenda mollitia ullam quae cupiditate neque est.\"," +
                    //"		\"gender\": \"M\"," +
                    //"		\"start_time\": \"2015-06-16 03:56:54\"," +
                    //"		\"end_time\": \"2015-06-16 07:18:57\"," +
                    //"		\"profile\": {" +
                    //"			\"id\": \"925\"," +
                    //"			\"user_id\": \"925\"," +
                    //"			\"name\": \"Dillon Swift V\"," +
                    //"			\"image_url\": \"http://lorempixel.com/640/480/cats/?40699\"," +
                    //"			\"gender\": \"M\"," +
                    //"			\"likes\": \"31\"," +
                    //"			\"dislikes\": \"17\"" +
                    //"		}" +
                    //"	}" +
                    //"]," +
                    //"\"paginator\": {" +
                    //"	\"total_count\": 2" +
                    //"}" +
                    //"}");
            //ParseResponse();

        //} catch (JSONException e) {
            //e.printStackTrace();
            //return;
        //}
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
							} else {
								Log.d(TAG, response.toString());
								ParseResponse(request);
							}
						} catch (JSONException e) {
							Log.d(TAG, "There is an JSON exception.");
							e.printStackTrace();
							return;
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "ErrorResponse called");
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
		String url = "http://donteatalone.paigelim.com/api/v1/users/" + mUserId + "/matches";
		SendRequest(url, Request.Method.GET, RequestMode.GET_MATCHLIST);
	}

    public void CreatePreference(int user_id, int max_distance, int min_age, int max_age, int min_price, int max_price, char gender, Timestamp start_time, Timestamp end_time)
    {
        String start_time_string = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(start_time);
        String end_time_string = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(end_time);
        String url = "http://donteatalone.paigelim.com/api/v1/matches?" +
					 "user_id=" + Integer.toString(user_id) +
					 "&max_distance=" + Integer.toString(max_distance) +
					 "&min_age=" + Integer.toString(min_age) +
					 "&max_age=" + Integer.toString(max_age) +
					 "&min_price=" + Integer.toString(min_price) +
					 "&max_price=" + Integer.toString(max_price) +
					 "&gender=" + gender +
					 "&start_time=" + start_time_string +
					 "&end_time=" + end_time_string;
		SendRequest(url, Request.Method.POST, RequestMode.CREATE_PREFERENCE);
	}

	// return value 0: error: Preference has not been initialized.
	// return value 1: Delete the current preference.
	public int DeletePreference()
	{
		if (mMatchId == -1) {
			return 0;
		} else {
			String url = "http://donteatalone.paigelim.com/api/v1/matches/" + Integer.toString(mMatchId);	
			SendRequest(url, Request.Method.DELETE, RequestMode.DELETE_PREFERENCE);
			return 1;
		}
	}

	public void SendRequestLogIn(String email, String password)
	{
		String url = "http://donteatalone.paigelim.com/api/v1/login?" +
			"email=" + email +
			"&password=" + password;
		SendRequest(url, Request.Method.GET, RequestMode.LOGIN);
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

		String url = "http://donteatalone.paigelim.com/api/v1/profiles/" + Integer.toString(mUserId) +
			"?name=" + mUserProfile.GetName() +
			"&image_url=" + mUserProfile.GetImageUrl() +
			"&gender=" + mUserProfile.GetGender() +
			"&age=" + Integer.toString(mUserProfile.GetAge()) +
			"&description=" + mUserProfile.GetDescription();
		
		SendRequest(url, Request.Method.PUT, RequestMode.UPDATE_PROFILE);
    }

    public void LinkToFacebook(int facebookId)
    {
        // TODO: LINK TO FACEBOOK
		mFacebookId = facebookId;
    }

    public int GetUserId()
    {
        return mUserId;
    }

    public String GetResponse()
    {
        if (mSavedJSON == null) {
            return "";
        } else {
            return mSavedJSON.toString();
        }
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
						newUser.setDistance(userInfo.getDouble("distance"));
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
				} else if (request == RequestMode.CREATE_PREFERENCE) {
					mMatchId = mSavedJSON.getJSONObject("match").getInt("id");
				} else if (request == RequestMode.GET_PROFILE) {
					Log.d(TAG, "LOGIN parsing");
					JSONObject userProfile = mSavedJSON.getJSONObject("data").getJSONObject("user");
					mUserProfile.SetId(userProfile.getInt("id"));
					mUserProfile.SetName(userProfile.getString("name"));
					mUserProfile.SetEmail(userProfile.getString("email"));
					mUserProfile.SetImageUrl(userProfile.getString("image_url"));
					mUserProfile.SetGender(userProfile.getString("gender"));
					mUserProfile.SetAge(userProfile.getInt("age"));
					mUserProfile.SetDescription(userProfile.getString("description"));
					mUserProfile.LogProfile();
				} else if (request == RequestMode.LOGIN) {
					mGCMToken = mSavedJSON.getJSONObject("data").getJSONObject("user").getString("gcm_token");
					Log.d(FacadeModule.TAG, mGCMToken);
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
}
