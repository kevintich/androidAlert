package com.r4z0rw0lf.broadcastListener;

import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.lang.Runnable;
import org.json.*;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class service extends Service {
	private final static String TAG = "service";
	DatagramSocket DS;
	Runner r;
	Thread t;
	String ns;
	boolean ring = false;
	boolean vibrate = false;
    int port = 8081;
	SharedPreferences prefs;
	NotificationManager mNotificationManager;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		ring = prefs.getBoolean("ring", false);
		vibrate = prefs.getBoolean("vibrate", false);
		port = Integer.parseInt(prefs.getString("port", "8081"),10);
	}
	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		// close the socket here
		r.stop = true;
	}
	public void notifyData(String title, String message, int id){
		
		int icon = R.drawable.icon;
		
		long when = System.currentTimeMillis();
		CharSequence tickerText = title;
		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = title;
		CharSequence contentText = message;
		Intent notificationIntent = new Intent(this, broadcast.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		if(ring){
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if(vibrate){
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);


		mNotificationManager.notify(id, notification);
	}
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		
		ns = Context.NOTIFICATION_SERVICE;
	
		mNotificationManager = (NotificationManager) getSystemService(ns);
		r = new Runner();
		t = new Thread(r);
		t.start();
		//start the socket here
	}

	private class Runner implements Runnable {
			private volatile boolean stop = false;
			public void run(){
				try {
					DS  = new DatagramSocket(port);
					Log.i(TAG, "created DatagramSocket");
					byte[] receiveData = new byte[1024];
					while(!stop){
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						DS.receive(receivePacket);
						String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
						String  title = "no title";
						String text = "no text";
						int id = 1;
						try {
						JSONObject obj = (JSONObject) new JSONTokener(sentence).nextValue();
							if(obj.has("title"))
								title = obj.getString("title");
							if(obj.has("text"))
								text = obj.getString("text");
							if(obj.has("id"))
								id = obj.getInt("id");
							
						} catch (Exception Ee){
						}
						notifyData(title, text, id);
					}
					DS.close();
					Log.i(TAG, "Stopped listening");
				} catch (Exception e){
					Log.e(TAG, "Error", e);
				}
			}
	}
}
