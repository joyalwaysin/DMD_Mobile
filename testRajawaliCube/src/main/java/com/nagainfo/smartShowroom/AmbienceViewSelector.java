package com.nagainfo.smartShowroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AmbienceViewSelector extends Activity{
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ambience_select_view);
		
		final Button livingRoom01 = (Button)findViewById(R.id.living_room_01);
		final Button exterior = (Button)findViewById(R.id.exterior_01);
		final Button livingRoom02 = (Button)findViewById(R.id.living_room_02);
		final Button bathroom = (Button)findViewById(R.id.bathroom_01);
		final Button kitchen = (Button)findViewById(R.id.kitchen_01);
		final Button bedroom = (Button)findViewById(R.id.bedroom_01);
		
		exterior.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						AmbienceExterior.class);
				startActivity(intent);
			}
		});

		livingRoom01.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
				 AmbienceLivingRoom01.class);
//						AmbienceDrawingroom.class);
				startActivity(intent);
				
			}
		});
		livingRoom02.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getApplicationContext(),
						AmbienceLivingRoom02.class);
				startActivity(intent);
			}
		});
		
		bathroom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						AmbienceBathroom.class);
				startActivity(intent);
			}
		});
		
		kitchen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getApplicationContext(),
						AmbienceKitchen.class);
				startActivity(intent);
				
			}
		});
		
		bedroom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						AmbienceBedroom.class);
				startActivity(intent);
			}
		});
	}
	
	public void showToast(String string){
		
		Toast.makeText(getApplicationContext(), string, 1).show();
		
	}
	

}
