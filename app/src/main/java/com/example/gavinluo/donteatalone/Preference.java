package com.example.gavinluo.donteatalone;

import java.sql.Timestamp;

/**
 * Created by GavinLuo on 15-07-17.
 */
public class Preference {
	public int m_user_id = 0;
	public int m_max_distance = 0;
	public int m_min_age = 0;
	public int m_max_age = 0;
	public int m_min_price = 0;
	public int m_max_price = 0;
	public String m_comment = "";
	public String m_gender = "";
	public Timestamp m_start_time;
	public Timestamp m_end_time;

	public Preference(int user_id, int max_distance, int min_age, int max_age, int min_price, int max_price, String comment, String gender, Timestamp start_time, Timestamp end_time)
	{
		m_user_id = user_id;
		m_max_distance = max_distance;
		m_min_age = min_age;
		m_max_age = max_age;
		m_min_price = min_price;
		m_max_price = max_price;
		m_comment = comment;
		m_gender = gender;
		m_start_time = start_time;
		m_end_time = end_time;
	}

	public Preference()
	{
		m_user_id = -1;
		m_max_distance = 0;
		m_min_age = 0;
		m_max_age = 0;
		m_min_price = 0;
		m_max_price = 0;
		m_comment = "";
		m_gender = "";
	}
}
