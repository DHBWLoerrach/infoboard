package com.example.blackboarddhbwloe.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;

public class DetailSuche extends Activity {
	
	static String sqlStatement;
	boolean sucheBieteSichtbar = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suche);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Spinner spinner = (Spinner) findViewById(R.id.Kategorie);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.katSuche, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		
		//Funktion iconBar: Farbschema anpassen
		
		
//		defaultcolor f�r das icon wenn es angeklickt wurde
		Button buttonStatusSelected = (Button) findViewById(R.id.ImageButton02);
		buttonStatusSelected.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_dhbw_icon_bar_selected));
		
		
		
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
		
		Button buttonMain = (Button) findViewById(R.id.ImageButton03);
		buttonMain.setOnClickListener(new View.OnClickListener() {

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

	public void startSearch(View v) {
		
		sqlStatement = "SELECT * From Inserate";

		Boolean insertAND = false;
		Boolean firstValue = true;


		EditText textSuchFeld = (EditText) findViewById(R.id.SuchfeldText);
		CheckBox cBoxSuche = (CheckBox) findViewById(R.id.CBoxSearch);
		Spinner katSuche = (Spinner) findViewById(R.id.Kategorie);
		EditText textPreisVon = (EditText) findViewById(R.id.Preis_von);
		EditText textPreisBis = (EditText) findViewById(R.id.Preis_bis);
		RadioGroup radioSuche = (RadioGroup) findViewById(R.id.radioGroupDetailSuche);

		if (!textSuchFeld.getText().toString().equals("")) {
			firstValue = checkIfFirstValue(firstValue);
			sqlStatement += "titel like '%" + textSuchFeld.getText().toString()
					+ "%'";
			insertAND = true;
		}

		if (cBoxSuche.isChecked()
				&& !textSuchFeld.getText().toString().equals("")) {
			sqlStatement += " OR beschreibung like '%"
					+ textSuchFeld.getText().toString() + "%'";
			insertAND = true;
		}

		if (katSuche.getSelectedItemId() != 0) {
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "kategorie=" + (katSuche.getSelectedItemId()-1);
			insertAND = true;
		}

		if (!textPreisVon.getText().toString().equals("")
				&& !textPreisBis.getText().toString().equals("")) {
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "preis BETWEEN "
					+ textPreisVon.getText().toString() + " AND "
					+ textPreisBis.getText().toString();
		} else {
			if (!textPreisVon.getText().toString().equals("")) {
				firstValue = checkIfFirstValue(firstValue);
				insertSqlAnd(insertAND);
				sqlStatement += "preis > " + textPreisVon.getText().toString();
			}
			if (!textPreisBis.getText().toString().equals("")) {
				firstValue = checkIfFirstValue(firstValue);
				insertSqlAnd(insertAND);
				sqlStatement += "preis < " + textPreisBis.getText().toString();
			}
		}

		switch (radioSuche.getCheckedRadioButtonId()) {
		case R.id.radioBiete:
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "biete = 1";
			break;
		case R.id.radioSuche:
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "biete = 0";
			break;
		default:
			break;
		}

		sqlStatement += " order by datum desc;";

		Log.d("DetailSearch", "SqlString: " + sqlStatement);
		
		Bundle container = new Bundle();
		container.putString("sql", sqlStatement);
		
		Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
		intentAngebote.putExtras(container);
		intentAngebote.putExtra("barTitle", "Detailsuche");
		startActivity(intentAngebote);

	}

	private Boolean checkIfFirstValue(Boolean firstValue) {
		if (firstValue) {
			sqlStatement += " WHERE ";
			return false;
		}
		return false;
	}

	private void insertSqlAnd(Boolean insertAND) {
		if (insertAND) {
			sqlStatement += " AND ";
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
