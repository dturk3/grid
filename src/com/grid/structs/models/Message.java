package com.grid.structs.models;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Preconditions;
import com.grid.structs.geo.Point2D;
import com.ocpsoft.pretty.time.PrettyTime;

public class Message {
	private final String mPublisher;
	private final String mMessage;
	private final Point2D mLocation;
	private final DateTime mTimestamp;
	
	public Message(final String publisher, final String message, final Point2D location) {
		Preconditions.checkState(message.length() < 500);
		mPublisher = publisher;
		mMessage = message;
		mLocation = location;
		mTimestamp = DateTime.now();
	}
	
	public static Message create(final String publisher, final String message, final Point2D location) {
		return new Message(publisher, message, location);
	}

	public String getPublisher() {
		return mPublisher;
	}

	public String getMessage() {
		return mMessage;
	}

	public Point2D getLocation() {
		return mLocation;
	}

	public DateTime getTimestamp() {
		return mTimestamp;
	}
	
	@Override
	public String toString() {
		final JSONObject jsonRepr = new JSONObject();
		try {
			jsonRepr.put("publisher", mPublisher);
			jsonRepr.put("message", mMessage);
			jsonRepr.put("timestamp", mTimestamp.toDateTime().getMillis());
			jsonRepr.put("fuzzyTimestamp", new PrettyTime().format(mTimestamp.toLocalDateTime().toDate()));
		} catch (JSONException e) {
			return mPublisher + " [" + mTimestamp.toLocalDateTime() + "] -> " + mMessage;
		}
		return jsonRepr.toString();
	}
}
