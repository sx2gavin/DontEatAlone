package com.example.gavinluo.donteatalone;

/**
 * Created by GavinLuo on 15-07-17.
 */
public class Restaurant {
	public String m_name;
	public String m_image_url;
	public double m_rating;
	public String m_formatted_address;
	public String m_formatted_phone_number;
	public double m_latitude;
	public double m_longtitude;
	public String m_url;
	public String m_website;

	public Restaurant(String name, String image_url, double rating, String formatted_address, String formatted_phone_number, double latitude, double longtitude, String url, String website) 
	{
		m_name = name;
		m_image_url = image_url;
		m_rating = rating;
		m_formatted_address = formatted_address;
		m_formatted_phone_number = formatted_phone_number;
		m_latitude = latitude;
		m_longtitude = longtitude;
		m_url = url;
		m_website = website;
	}
}
