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
		
//		Button buttonBar = (Button) findViewById(R.id.ImageButton01);
//		buttonBar.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intentAngebote = new Intent(
//						"com.example.blackboarddhbwloe.INSERATLISTE");
//
//				startActivity(intentAngebote);
//				
//			}
//		});	
		
		Button buttonSucheBar = (Button) findViewById(R.id.ImageButton02);
		buttonSucheBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.DETAILSUCHE");

				startActivity(intentAngebote);
				
			}
		});		
		
		Button buttonSucheBar2 = (Button) findViewById(R.id.ImageButton03);
		buttonSucheBar2.setOnClickListener(new View.OnClickListener() {

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
