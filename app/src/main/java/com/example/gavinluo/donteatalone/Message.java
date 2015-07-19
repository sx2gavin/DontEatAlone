package com.example.gavinluo.donteatalone;

import java.sql.Timestamp;

/**
 * Created by GavinLuo on 15-07-18.
 */
public class Message {
	public int mUserId;
	public int mToUserId;
	public String mMessage;
	public String mTimestamp;
	public int mTimezoneType;
	public String mTimeZone;

	public Message(int userId, int toUserId, String message, String timeStamp)
	{
		mUserId = userId;
		mToUserId = toUserId;
		mMessage = message;
	}
}
