package com.nagainfo.smartShowroom;



import rajawali.RajawaliActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SampleActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		

	}
	public void notImplemented(View v)
	{
		Toast.makeText(getApplicationContext(), "Functionality not implemented", Toast.LENGTH_LONG).show();
	}
}
