package com.example.gavinluo.donteatalone;

import android.util.Log;

/**
 * Created by GavinLuo on 15-07-17.
 */
public class Profile {
	private int m_id;
	private String m_email;
	private String m_name;
	private String m_image_url;
	private String m_gender;
	private int m_age;
	private String m_description;

	public int GetId()
	{
		return m_id;
	}
	public String GetEmail()
	{
		return m_email;
	}
	
	public String GetName()
	{
		return m_name;
	}
	
	public String GetImageUrl()
	{
		return m_image_url;
	}
	
	public String GetGender()
	{
		return m_gender;
	}
	
	public int GetAge()
	{
		return m_age;
	}
	
	public String GetDescription()
	{
		return m_description;
	}
	

	public void SetId(int id)
	{
		m_id = id;
	}
	
	public void SetEmail(String email)
	{
		m_email = email;	
	}
	
	public void SetName(String name)
	{
		m_name = name;	
	}
	
	public void SetImageUrl(String image_url)
	{
		m_image_url = image_url;	
	}
	
	public void SetGender(String gender)
	{
		m_gender = gender;	
	}
	
	public void SetAge(int age)
	{
		m_age = age;	
	}
	
	public void SetDescription(String description) {
		m_description = description;
	}

	public void LogProfile()
	{
		Log.d(FacadeModule.TAG, "ID:" + Integer.toString(m_id) + " " +
				"Name:" + m_name + " " +
				"Gender:" + m_gender + " " +
				"Email:" + m_email + " " +
				"Age:" + Integer.toString(m_age) + " " +
				"Description:" + m_description + " " +
				"ImageUrl:" + m_image_url);
	}
}
