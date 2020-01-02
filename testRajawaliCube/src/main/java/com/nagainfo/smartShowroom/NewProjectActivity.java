package com.nagainfomob.smartShowroom;

import com.nagainfomob.database.DatabaseHandler;

import rajawali.RajawaliActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NewProjectActivity extends Activity implements OnTouchListener {
	Button nextButton, backButton;

	// GlobalVariables gVariables = new GlobalVariables();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newproject);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		nextButton = (Button) findViewById(R.id.newProjectNextButton);
		backButton = (Button) findViewById(R.id.newProjectBackButton);

		nextButton.setOnClickListener(nextClicked);
		backButton.setOnClickListener(backClicked);
		LinearLayout base = (LinearLayout) findViewById(R.id.baseView);
		base.setOnTouchListener(this);
	}

	OnClickListener nextClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			EditText pro_name = (EditText) findViewById(R.id.pro_Name);
			if ((pro_name.getText().toString().trim()).length() > 0) {

				if ((pro_name.getText().toString().trim()).length() > 30) {

					Toast.makeText(getApplicationContext(),
							"Project name too large!", Toast.LENGTH_SHORT)
							.show();
					return;

				}
				DatabaseHandler db = new DatabaseHandler(
						getApplicationContext());
				if (!db.isProjectExist(pro_name.getText().toString())) {
					GlobalVariables.setProjectName(pro_name.getText()
							.toString());
					Intent i = new Intent(getApplicationContext(),
							SelectShapeActivity.class);
					startActivity(i);
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Project Already Exists!", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Type a project name!",
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	OnClickListener backClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			EditText pro_name = (EditText) findViewById(R.id.pro_Name);
			imm.hideSoftInputFromWindow(pro_name.getWindowToken(), 0);
			return true;
		}
		return false;
	}
}
