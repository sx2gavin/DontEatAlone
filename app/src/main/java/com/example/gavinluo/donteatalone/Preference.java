package com.example.gavinluo.donteatalone;

import java.sql.Timestamp;

/**
 * Created by GavinLuo on 15-07-17.
 */
public class Preference {
	public int m_user_id;
	public int m_max_distance;
	public int m_min_age;
	public int m_max_age;
	public int m_min_price;
	public int m_max_price;
	public String m_comment;
	public String m_gender;
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
}
