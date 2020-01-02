package com.nagainfomob.smartShowroom;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;

public class RegisterActivity extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		 //DatabaseHandler db=new DatabaseHandler(getApplicationContext());
		// db.clearTable();
		
		bindService(new Intent(
				"com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE);
		ArrayList<String> skuList = new ArrayList<String> ();
		skuList.add("premiumUpgrade");
		
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("FULL VERSION", skuList);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	ServiceConnection mServiceConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			

		}
	};

	public void activatePressed(View v) {
		Intent i = new Intent(getApplicationContext(), InAppActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
}
