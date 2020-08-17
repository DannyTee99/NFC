package com.developer.tapit;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MyHostApduService extends HostApduService {

	public int selMode=0;
	
	@Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {

		//ALL FIRST MESSAGE IF CONNECTION OK
		if (selectAidApdu(apdu)) {
			return DictInf.HAIDSelectionOkid;
		}
		
		//NFC signals sent and received
		//If Reader doesnt know, it sends Wait
		 String input="";
		try {
			input = new String(apdu, "US-ASCII");
			
			//Standby status
			if(Arrays.equals(DictInf.Rwaitid,apdu))
				return DictInf.HcommitWaitid;
			
			//1. Choose mode
			if(Arrays.equals(DictInf.RAskForIdentModeid,apdu))  //identification
				{selMode=1;
				return DictInf.HIdentModeSelectedid;}	
			
			//2. Send Username
			if(Arrays.equals(DictInf.RAskForU2id,apdu))
				return getData1();
			
			//3. Send Password
			if(Arrays.equals(DictInf.RAskForP2id,apdu))
				return getData2();
			
			//4. User successfully recognized
			if(Arrays.equals(DictInf.RokGoodid,apdu))
			{
				//statusRaw=input.substring(4,input.length());
				//1.Start activity of the app depending on the settings
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
				boolean pref = prefs.getBoolean("app_autostart",true);
				
				if(pref)
				{
					Intent menuIntent = new Intent();
					menuIntent.setClass(this,MainActivity.class);
					menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);				
					startActivity(menuIntent);
				}
				Context context = getApplicationContext();
				CharSequence text = "authentication completed !";
				int duration = 5;
				@SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
				return DictInf.HDoneWithIdid;
			}
			
			//5. User was not recognized
			if(Arrays.equals(DictInf.RokNotGoodid,apdu))
			{
				//1. Start activity with user data
				Intent menuIntent = new Intent();
				menuIntent.setClass(this, EnterAccountDetailsActivity.class);
				menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);				
				startActivity(menuIntent);	
				
				Context context = getApplicationContext();
				CharSequence text = "User not found. Please try again or contact your company.";
				int duration = 5;
				@SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
				return DictInf.HDoneWithIdid;
			}
			
			
			//6.Get status info (start intent and display it) -----------TODO
			if(input.substring(0,3).equals("S1:"))
			{		


			}
			

			
		}catch (UnsupportedEncodingException e) {}
		
		
		return DictInf.HcommitWaitid;
	}
	
	private byte[] getData1()
	{
		//return amount
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		String pu="U1: "+preferences.getString("u2219aAsgiLPOo","");

		return pu.getBytes();
	}
	
	private byte[] getData2()
	{
		//account user.key
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String pp="P1: "+preferences.getString("p09ki8dieik87n","");
		return pp.getBytes();
	}
	
	private boolean selectAidApdu(byte[] apdu) {
		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCE", "Deactivated: " + reason);
	}
}