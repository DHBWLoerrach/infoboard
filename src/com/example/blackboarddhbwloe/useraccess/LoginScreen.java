package com.example.blackboarddhbwloe.useraccess;

import java.sql.ResultSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;

public class LoginScreen extends Activity {
	
	
	public static boolean verbindungHergestellt = true; 
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginscreen);
		
		// StrictMode ist dazu da damit die Policy connections nach aussen
		// zulaesst, MT
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(policy);

		getActionBar().setTitle(R.string.anmeldung);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button button = (Button) findViewById(R.id.button_LoginScreen_Anmeldung);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				TextView user = (TextView) findViewById(R.id.Anmeldung_LoginScreen_Username);
				TextView pass = (TextView) findViewById(R.id.Anmeldung_LoginScreen_Pass);
				try {
					ResultSet resultset = DB
							.getRSFromDB("Select * From tblUser Where username = '"
									+ user.getText()
									+ "' and psw = '"
									+ pass.getText() + "';");
					boolean erfolgreichAngemeldet = false;

					while (resultset.next()) {

						// Anmeldung erfolgreich: starte die Activity main und
						// Fuelle das textfield User ist angemeldet mit Text
						Intent intentChangePasswort = new Intent(
								"com.example.blackboarddhbwloe.CHANGEPASSWORD");
						Intent intentLastActivity = getIntent();
						String activityLast = intentLastActivity
								.getStringExtra("activity");

						// Variable Anmeldestatus wird auf true gesetzt
						MainActivity.setAnmeldestatus(true);
						MainActivity.USERNAME = resultset.getString("username");
						MainActivity.USERID = resultset.getString("id");
						MainActivity.LASTLOGIN = resultset.getDate("lastLogin");

						// layout laden
						erfolgreichAngemeldet = true;

						int ini = Integer.parseInt(resultset
								.getString("initial"));

						if (ini == 0) {
							startActivity(intentChangePasswort);
						} else {
							
							// Wenn Benutzer vorher vom loadingScreen kam
							// starte die entsprechende Activity
							if (activityLast.equals("splash")) {

								Intent intent = new Intent(
										"com.example.blackboarddhbwloe.main");
								startActivity(intent);

								// beende die activity loginScreen (kein zurück
								// möglich)
								finish();
							
							} else {
								if (activityLast.equals("fromKontakt") | activityLast.equals("fromProfilansicht")) {
									Intent intent = new Intent(
											"com.example.blackboarddhbwloe.DETAILVIEW");
									startActivity(intent);

									// beende die activity loginScreen (kein
									// zurück möglich)
									finish();
								} else {
									Intent intent = new Intent(
											"com.example.blackboarddhbwloe.main");
									startActivity(intent);

									// beende die activity loginScreen (kein
									// zurück möglich)
									finish();
								}
							}

						}

					}

					if (erfolgreichAngemeldet == false) {
						Toast.makeText(getApplicationContext(),
								R.string.benutzername_oder_passwort_falsch,
								Toast.LENGTH_LONG).show();
					}

				}

				catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							R.string.verbindung_fehlgeschlagen, Toast.LENGTH_LONG)
							.show();

					e.printStackTrace();

				}

			}
		});
		Button buttonReg = (Button) findViewById(R.id.Button_Loginscreen_Regist);
		buttonReg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentReg = new Intent(
						"com.example.blackboarddhbwloe.REGISTRATION");
				startActivity(intentReg);
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
