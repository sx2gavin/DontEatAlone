package com.example.gavinluo.donteatalone;

import android.util.Log;

/**
 * Created by GavinLuo on 15-07-12.
 */
public class User {
    private int m_id;
	// used only for requests.
	private int m_request_id;
	private int m_invitation_sent;
    private String m_name;
	private String m_image_url;
	private String m_gender;
    private float m_max_distance;
    private float m_latitude;
    private float m_longitude;
    private double m_distance;
	private int m_min_age;
	private int m_max_age;
	private float m_min_price;
	private float m_max_price;
	private String m_start_time;
	private String m_end_time;
	private int m_likes;
	private int m_dislikes;

    public User()
    {

    }

    public User(int id, String name)
    {

    }
	

    public int getId() 
	{
		return m_id;		
	}

	public int getRequestId()
	{
		return m_request_id;
	}

	public int getInvitationSent()
	{
		return m_invitation_sent;
	}

    public String getName()
	{
		return m_name;
	}

	public String getImageUrl() { return m_image_url; }
	
	public String getGender()    
	{
		return m_gender;
	}	
		
	public float getMaxDistance()
	{
		return m_max_distance;	
	}

    public float getLatitude()
	{
		return m_latitude;
	}

    public float getLongitude()
	{
		return m_longitude;
	}

    public double getDistance()
	{
		return m_distance;
	}

	public int getMinAge()
	{
		return m_min_age;
	}

	public int getMaxAge()
	{
		return m_max_age;
	}

	public float getMinPrice()
	{
		return m_min_price;
	}

	public float getMaxPrice()
	{
		return m_max_price;
	}

	public String getStartTime()
	{
		return m_start_time;
	}

	public String getEndTime()
	{
		return m_end_time;
	}

	public int getLikes()
	{
		return m_likes;
	}

	public int getDislikes()
	{
		return m_dislikes;
	}


	public void setId(int id)
	{
		m_id = id;
	}

	public void setRequestId(int request_id)
	{
		m_request_id = request_id;
	}

	public void setInvitationSent(int invitationSent)
	{
		m_invitation_sent = invitationSent;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public void setImageUrl(String image_url) {m_image_url = image_url; }

	public void setGender(String gender)
	{
		m_gender = gender;
	}

	public void setMaxDistance(float max_distance)
	{
		m_max_distance = max_distance;
	}

	public void setLatitude(float latitude)
	{
		m_latitude = latitude;
	}

	public void setLongitude(float longitude)
	{
		m_longitude = longitude;
	}

	public void setDistance(double distance)
	{
		m_distance = distance;
	}

	public void setMinAge(int min_age)
	{
		m_min_age = min_age;
	}

	public void setMaxAge(int max_age)
	{
		m_max_age = max_age;
	}

	public void setMinPrice(float min_price)
	{
		m_min_price = min_price;
	}

	public void setMaxPrice(float max_price)
	{
		m_max_price = max_price;
	}

	public void setStartTime(String start_time)
	{
		m_start_time = start_time;
	}

	public void setEndTime(String end_time)
	{
		m_end_time = end_time;
	}

	public void setLikes(int likes)
	{
		m_likes = likes;
	}

	public void setDislikes(int dislikes)
	{
		m_dislikes = dislikes;
	}

	public void LogUserInfo()
	{
		Log.d(FacadeModule.TAG, "ID:" + Integer.toString(m_id) + " " +
			"Name:" + m_name + " " +
			"Gender:" + m_gender + " " +
			"Max distance:" + Float.toString(m_max_distance) + " " +
			"Latitude:" + Float.toString(m_latitude) + " " +
			"Longtitude:" + Float.toString(m_longitude) + " " +
			"Distance:" + Double.toString(m_distance) + " " +
			"Min_age:" + Integer.toString(m_min_age) + " " +
			"Max_age:" + Integer.toString(m_max_age) + " " +
			"Min_price:" + Float.toString(m_min_price) + " " +
			"Max_price:" + Float.toString(m_max_price) + " " +
			"Start time:" + m_start_time + " " +
			"End time:" + m_end_time + " " +
			"Likes:" + Integer.toString(m_likes) + " " +
			"Dislikes:" + Integer.toString(m_dislikes));
	}
}
