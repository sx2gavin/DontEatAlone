package com.example.gavinluo.donteatalone;

import java.util.ArrayList;

/**
 * Created by GavinLuo on 15-07-18.
 */
public class Meeting {
	public int mUserId;
	public int mToUserId;
	public ArrayList<Message> mMessages;

	public Meeting(int userId, int toUserId)
	{
		mUserId = userId;
		mToUserId = toUserId;
		mMessages = new ArrayList<Message>();	
	}
}

