package com.example.blackboarddhbwloe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.blackboarddhbwloe.tools.DB;

public class splash extends Activity{
	ProgressBar spinner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Thread logoTimer = new Thread(){
		public void run(){
			try {
				
				spinner = (ProgressBar)findViewById(R.id.progressBar2);
				spinner.setVisibility(View.VISIBLE);
				DB.setConnection();
				Intent menuIntent = new Intent("com.example.blackboarddhbwloe.main");
				startActivity(menuIntent);
//				spinner.setProgress(100);
//				spinner.setVisibility(View.GONE);
			} catch (Exception e) {
				
				System.out.println("datenverbindung fehlgeschlagen");
				e.printStackTrace();
			}
			finally
			{
				finish();
			}
		}
		};
		logoTimer.start();
	}
	
}
