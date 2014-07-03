package com.example.blackboarddhbwloe.userarea;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;

public class AboListe extends Activity implements
		CheckBox.OnCheckedChangeListener {

	String[][] arrayAboListe;
	String[] arrayAboKategorie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aktivity_aboliste);

		getActionBar().setTitle(R.string.meine_abonnements);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// initialisiere das Abo array des benutzers
		initialsisiereAboListe();
		// Erzeuge die RadioButtons dynamisch aus dem xml-Array "kategorie"
		erzeugeCheckedTextView();

		Button buttonAbo = (Button) findViewById(R.id.button_AboListe_Speichern);
		buttonAbo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Schreibe das Abo in die DB
				schreibeAboDatenInDB();

				// Weiterleitung und Toast
				finish();
				Toast.makeText(getApplicationContext(), R.string.abo_gespeichert_,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void erzeugeCheckedTextView() {

		LinearLayout my_layout = (LinearLayout) findViewById(R.id.linearLayoutCheckedTextView);

		// Erzeuge so viele checkboxen wie es das Array lang ist und vergebe die
		// Namen aus dem ArrayAboKategorie
		for (int i = 0; i < arrayAboKategorie.length; i++) {
			TableRow row = new TableRow(this);
			row.setId(i);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			CheckBox checkBox = new CheckBox(this);
			checkBox.setOnCheckedChangeListener(this);
			checkBox.setId(i);
			checkBox.setText(arrayAboKategorie[i]);


			// Wenn arrayAboListe ungleich 0 dann setze die checkbox auf true
			if (arrayAboListe[i][1] != "0") {
				checkBox.setChecked(true);
			}

			row.addView(checkBox);
			my_layout.addView(row);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// Wenn der Benutzer auf eine checkbox klickt veraendere den status im
		// arrayAboListe
		String status = "0";
		if (isChecked == true) {
			status = "1";
		} else {
			status = "0";
		}

		String kategorieNr = Integer.toString(buttonView.getId());
		arrayAboListe[buttonView.getId()][0] = kategorieNr;
		arrayAboListe[buttonView.getId()][1] = status;
	}

	private void schreibeAboDatenInDB() {
		// erzeuge ein Thread in dem die Abo-Daten in die DB geschrieben werden
		new Thread(new Runnable() {
			@Override
			public void run() {
				entferneAlteAbos();

				String datenstring = "";
				int kommaMarker = 0;
				for (int x = 0; x < arrayAboListe.length; x++) {
					if (arrayAboListe[x][1].equals("1")) {
						// kein komma im SQL-Befehl beim ersten Durchgang
						if (kommaMarker == 1) {
							datenstring = datenstring + ",";
						}
						datenstring = datenstring + "('" + MainActivity.USERID
								+ "','" + arrayAboListe[x][0] + "')";
						kommaMarker = 1;
					}

				}
				String sqlStatement = "INSERT INTO abos(userID, kategorie) VALUES "
						+ datenstring;
				
				DB.addValueToDB(sqlStatement);
			}
		}).start();
	}

	private void entferneAlteAbos() {

		DB.deleteAllAbosFromUser(MainActivity.USERID);

	}

	private void initialsisiereAboListe() {
		// AboKategoryArray initialisieren
		arrayAboKategorie = getResources().getStringArray(R.array.kat);
		arrayAboListe = new String[arrayAboKategorie.length][2];
		// DB-Abfrage der UserAbos
		String sqlStatement = "Select * from abos where userID ="
				+ MainActivity.USERID;
		ResultSet resultSet = DB.getRSFromDB(sqlStatement);

		// arrayAboListe mit daten fuellen
		for (int x = 0; x < arrayAboKategorie.length; x++) {
			arrayAboListe[x][0] = Integer.toString(x);
			arrayAboListe[x][1] = "0";
		}

		// arrayAboListe mit unserAbonements fuellen
		try {
			while (resultSet.next()) {
				arrayAboListe[resultSet.getInt("kategorie")][1] = "1";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
