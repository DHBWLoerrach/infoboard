package com.example.blackboarddhbwloe.userarea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.Mail;

public class KontaktFormular extends Activity {

	// //Definiere welche activity angezeigt wird wenn der zur�ckbutton
	// gedr�cht wird
	// @Override
	// public void onBackPressed() {
	// Intent intentAngebote = new
	// Intent("com.example.blackboarddhbwloe.DETAILVIEW");
	// startActivity(intentAngebote);
	// super.onBackPressed();
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kontaktformular);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.kontakt_formular);

		final Intent intentFromDetailView = getIntent();
		TextView titel = (TextView) findViewById(R.id.tv_kontakt_inserattitel);
		final EditText nachricht = (EditText) findViewById(R.id.tv_kontakt_nachricht);
		final EditText betreff = (EditText) findViewById(R.id.tv_kontakt_betreff);

		titel.setText(intentFromDetailView.getStringExtra("titel"));
		final String username = intentFromDetailView
				.getStringExtra("inseratErsteller");

		Button button = (Button) findViewById(R.id.button_kontaktformular_senden);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Mail.sendMail(
						username,
						MainActivity.USERNAME
								+ " hat eine Nachricht gesendet. Klicken Sie hier um zu antworten\n mailto:"
								+ MainActivity.USERNAME
								+ "@dhbw-loerrach.de?subject=Antwort%20auf%20Artikel:%20"
								+ intentFromDetailView.getStringExtra("titel")
								+ " \n\n" + betreff.getText().toString() + "\n"
								+ nachricht.getText().toString(),
						"Anfrage zum Artikel: "
								+ intentFromDetailView.getStringExtra("titel"));

				Toast.makeText(getApplicationContext(),
						R.string.nachricht_wurde_versendet, Toast.LENGTH_LONG)
						.show();
				finish();

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
