package com.example.blackboarddhbwloe.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;

public class BenutzerBereich extends Activity {
	
	boolean sucheBieteSichtbar = false;

	// Definiere welche activity angezeigt wird wenn der zurueckbutton
	// gedrueckt wird
	@Override
	public void onBackPressed() {
		Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.main");
		
		startActivity(intentAngebote);
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_benutzerbereich);
		getActionBar().setTitle(R.string.benutzer_bereich);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button buttonAnzeige = (Button) findViewById(R.id.ButtonAnzeigeSchalten);
		buttonAnzeige.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.ANZEIGESCHALTEN");
				startActivity(intentAngebote);
				
			}
		});

		Button buttonGesuche = (Button) findViewById(R.id.ButtonMeineAnzeigen);
		buttonGesuche.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String sql = "Select * From Inserate Where userID = "
						+ MainActivity.USERID + ";";
				Bundle container = new Bundle();
				container.putString("sql", sql);

				Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
				intentAngebote.putExtras(container);
				intentAngebote.putExtra("from", "benutzerbereich");
				intentAngebote.putExtra("barTitle", "Meine Anzeigen");
				startActivity(intentAngebote);
				finish();

			}
		});

		Button buttonAnzeigeSchalten = (Button) findViewById(R.id.ButtonKontoVerwaltung);
		buttonAnzeigeSchalten.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.KONTOVERWALTUNG");
				startActivity(intentAngebote);
				finish();

			}
		});

		Button buttonSuche = (Button) findViewById(R.id.ButtonBenutzerbereich_MeineAbos);
		buttonSuche.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.MEINEABOS");
				startActivity(intentAngebote);

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
		
		
//Funktion iconBar: Farbschema anpassen
		
		
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
		
		Button buttonBenutzerbereichBar = (Button) findViewById(R.id.ImageButton03);
		buttonBenutzerbereichBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Wenn Benutzer angemeldet
				if (MainActivity.isAnmeldestatus() == true) {
					// Zeige die Angebote
					Intent intentAngebote = new Intent(
							"com.example.blackboarddhbwloe.main");
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
