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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GavinLuo on 15-06-26.
 *
 */


public class FacadeModule {

    public final static String TAG = "TAG";
    private static FacadeModule mInstance;
    private MySingleton mMySingleton;
    // private FacadeModule mContext;
    private JSONObject mSavedJSON;
    private Context mContext;
    private int mUserId;

    // constructor
    private FacadeModule(Context context)
    {
        mContext = context;
        mSavedJSON = null;
        mMySingleton = MySingleton.getMySingleton(context);
    }

    public void SendRequest(String url, int method)
    {
        Log.d(TAG, "SendRequest called");
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
                                Log.d(TAG, message);
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

    public String GetResponse()
    {
        if (mSavedJSON == null) {
            return "";
        } else {
            return mSavedJSON.toString();
        }
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
