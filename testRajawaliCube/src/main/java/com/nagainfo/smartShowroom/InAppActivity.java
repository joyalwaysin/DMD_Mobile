package com.nagainfomob.smartShowroom;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nagainfomob.database.DatabaseHandler;

public class InAppActivity extends Activity {

	Button continueButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inapp);
		continueButton = (Button) findViewById(R.id.inAppContinueButton);
		continueButton.setOnClickListener(continueClicked);
		DatabaseHandler db = new DatabaseHandler(this);
		//db.clearTable();
	}

	OnClickListener continueClicked = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			Intent i = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(i);
			finish();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
}
