package com.example.blackboarddhbwloe;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.blackboarddhbwloe.tools.DB;

public class Splash extends Activity{
	public static ResultSet rsHome;
	public static String anzahlSuche;
	public static String anzahlBiete;
	
	ProgressBar spinner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Thread logoTimer = new Thread(){
		@Override
		public void run(){
			try {
				
				spinner = (ProgressBar)findViewById(R.id.progressBar2);
				spinner.setVisibility(View.VISIBLE);
				DB.setConnection();
				aktualisiereDatenHomescreen();
				Intent menuIntent = new Intent("com.example.blackboarddhbwloe.LOGINSCREEN");
				menuIntent.putExtra("activity", "splash");
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

		public void aktualisiereDatenHomescreen() {
			
			
			String sqlStatement = "SELECT titel,datum FROM BBDB.Inserate where status = 1 order by id desc limit 3";
			rsHome = DB.getRSFromDB(sqlStatement);
			
			
			String sqlStatement2 = "SELECT count(id) from Inserate where biete = 1 and status = 1";
			try {
				ResultSet rs1 = DB.getRSFromDB(sqlStatement2);
				rs1.next();
				anzahlSuche=rs1.getString(1);
				System.out.println("Anzahlllll: "+anzahlSuche);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			String sqlStatement3 = "SELECT count(id) from Inserate where biete = 0 and status = 1";
			try {
				ResultSet rs2 = DB.getRSFromDB(sqlStatement3);
				rs2.next();
				anzahlBiete=rs2.getString(1);
				System.out.println("AnzahlllllBiete: "+anzahlBiete);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		};
		logoTimer.start();
	}
	
}
