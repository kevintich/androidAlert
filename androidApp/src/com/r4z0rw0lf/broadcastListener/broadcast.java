package com.r4z0rw0lf.broadcastListener;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

public class broadcast extends PreferenceActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
		addPreferencesFromResource(R.xml.preferences);
		Log.w("broadcast", "AppStarted");
		startService(new Intent(this, service.class));
        Preference restartPref = (Preference) findPreference("restartservice");
		restartPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
										getBaseContext().stopService(new Intent(broadcast.this, service.class));
										getBaseContext().startService(new Intent(broadcast.this, service.class));
                                        return true;
                                    }
                                });
        Preference stopPref = (Preference) findPreference("stopservice");
		stopPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
										getBaseContext().stopService(new Intent(broadcast.this, service.class));
                                        return true;
                                    }
                                });
    }
	@Override
	public void onDestroy(){
		super.onDestroy();
		//DS.close();
		stopService(new Intent(this, service.class));
	}
}
