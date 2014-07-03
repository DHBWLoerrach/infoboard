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

	}

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
