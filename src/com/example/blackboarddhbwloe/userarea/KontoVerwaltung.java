package com.example.blackboarddhbwloe.userarea;

import java.sql.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;

public class KontoVerwaltung extends Activity {
	
	boolean sucheBieteSichtbar = false;

	@Override
	public void onBackPressed() {
		Intent intentAngebote = new Intent(
				"com.example.blackboarddhbwloe.BENUTZERBEREICH");
		startActivity(intentAngebote);
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kontoverwaltung);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.konto_verwaltung);

		Button button = (Button) findViewById(R.id.Button_kontoverwaltung_PasswortErneuern);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.CHANGEPASSWORD");
				startActivity(intentAngebote);
				finish();

			}
		});

		Button buttonGesuche = (Button) findViewById(R.id.button_benutzerprofil);
		buttonGesuche.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.MEINBENUTZERPROFIL");
				startActivity(intentAngebote);
				finish();
			}
		});

		Button buttonAnzeigeSchalten = (Button) findViewById(R.id.ButtonSetLastLogin);
		buttonAnzeigeSchalten.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DB.aktualisiereLetzterAngebotsAufrufInUserTable("2014-02-08 10:10:10");
				int year = 114;
				int month = 1;
				int day = 8;
				@SuppressWarnings("deprecation")
				Date d = new Date(year, month, day);

				MainActivity.LASTLOGIN = d;
			}
		});


		//Funktion iconBar: Farbschema anpassen
		
//		defaultcolor für das icon wenn es angeklickt wurde
		Button buttonStatusSelected = (Button) findViewById(R.id.ImageButton04);
		buttonStatusSelected.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_dhbw_icon_bar_selected));
		
		Button buttonDetailSucheBar = (Button) findViewById(R.id.ImageButton02);
		buttonDetailSucheBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.DETAILSUCHE");

				startActivity(intentAngebote);
				
			}
		});		
		
		Button buttonHomeBar = (Button) findViewById(R.id.ImageButton03);
		buttonHomeBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.main");

				startActivity(intentAngebote);
				
			}
		});	
		
		Button buttonBenutzerbereichBar = (Button) findViewById(R.id.ImageButton04);
		buttonBenutzerbereichBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Wenn Benutzer angemeldet
				if (MainActivity.isAnmeldestatus() == true) {
					// Zeige die Angebote
					Intent intentAngebote = new Intent(
							"com.example.blackboarddhbwloe.BENUTZERBEREICH");
					startActivity(intentAngebote);
					finish();
				} else {
					// Weiterleitung zum Login
					Intent intentLogin = new Intent(
							"com.example.blackboarddhbwloe.LOGINSCREEN");
					intentLogin.putExtra("activity", "fromBenutzerbereich");
					startActivity(intentLogin);
				}
			}
		});

		
		Button buttonSucheBar = (Button) findViewById(R.id.Button05);
		buttonSucheBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String sql = "Select * From Inserate Where biete = 1 order by datum desc;";
				Bundle container = new Bundle();
				container.putString("sql", sql);

				Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
				intentAngebote.putExtras(container);

				intentAngebote.putExtra("barTitle", "Angebote");
				startActivity(intentAngebote);
				finish();

			}
		});

		Button buttonBieteBar = (Button) findViewById(R.id.Button06);
		buttonBieteBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String sql = "Select * From Inserate Where biete = 0 order by datum desc;";
				Bundle container = new Bundle();
				container.putString("sql", sql);

				Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
				intentAngebote.putExtras(container);

				intentAngebote.putExtra("barTitle", "Gesuche");
				startActivity(intentAngebote);
				finish();
				
			}
		});

		
		Button buttonSucheBieteBar = (Button) findViewById(R.id.ImageButton01);
		buttonSucheBieteBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//open/close SucheBiete Bar
				Button buttonSucheSelected = (Button) findViewById(R.id.Button05);
				Button buttonBieteSelected = (Button) findViewById(R.id.Button06);
				if (sucheBieteSichtbar==false)
				{					
					buttonSucheSelected.setVisibility(View.VISIBLE);
					buttonBieteSelected.setVisibility(View.VISIBLE);
					sucheBieteSichtbar=true;
				}
				else
				{
					buttonSucheSelected.setVisibility(View.INVISIBLE);
					buttonBieteSelected.setVisibility(View.INVISIBLE);
					sucheBieteSichtbar=false;
				}

				}
			
		});
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
