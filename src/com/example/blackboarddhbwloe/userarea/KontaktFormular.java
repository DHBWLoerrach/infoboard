package com.example.blackboarddhbwloe.userarea;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.tools.Mail;

public class KontaktFormular extends Activity {

	private String adminMailAdress = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kontaktformular);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		

		final Intent intentFromDetailView = getIntent();
		String fromActivity = intentFromDetailView.getStringExtra("from");
		TextView titel = (TextView) findViewById(R.id.tv_kontakt_inserattitel);
		final EditText nachricht = (EditText) findViewById(R.id.tv_kontakt_nachricht);
		final EditText betreff = (EditText) findViewById(R.id.tv_kontakt_betreff);

		if(fromActivity.equals("kontakt"))
		{
			System.out.println("Ich Kontaktiere");
			getActionBar().setTitle(R.string.kontakt_formular);
			titel.setText(titel.getText()+intentFromDetailView.getStringExtra("titel"));
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
		else
		{
			//Hole Emailadresse von verantwortlichem Support
			String sqlStatement2 = "SELECT benutzername from tblAdmin";
			try {
				ResultSet rs1 = DB.getRSFromDB(sqlStatement2);
				rs1.next();
				adminMailAdress=rs1.getString(1);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(fromActivity.equals("support"))
			{
				System.out.println("Ich Supporte");
				getActionBar().setTitle("Supportanfrage");
				titel.setText("Ich habe eine Frage zu:");
				final String usernameKontakt = adminMailAdress;

				
				betreff.setText("Supportanfrage");
				betreff.setTextColor(Color.BLACK);
				betreff.setEnabled(false);
				
				Button button2 = (Button) findViewById(R.id.button_kontaktformular_senden);
				button2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						Mail.sendMail(
								usernameKontakt,
								MainActivity.USERNAME
										+ " hat eine Supportanfrage gestellt. Klicken Sie hier um zu antworten\n mailto:"
										+ MainActivity.USERNAME
										+ "@dhbw-loerrach.de?subject=Supportanfrage DHBW-InfoBoard"
										+ "\n"
										+ nachricht.getText().toString(),
								"Supportanfrage DHBW-InfoBoard");

						Toast.makeText(getApplicationContext(),
								R.string.nachricht_wurde_versendet, Toast.LENGTH_LONG)
								.show();
						finish();

					}
				});

		}}
		
		if(fromActivity.equals("feedback"))
		{
			getActionBar().setTitle("Feedback");
			titel.setText("Ich m�chte helfen, die App zu verbessern.");
			final String usernameKontakt = adminMailAdress;
			
			betreff.setText("Feedback");
			betreff.setTextColor(getResources().getColor(R.color.black));
			betreff.setEnabled(false);
			
			Button button2 = (Button) findViewById(R.id.button_kontaktformular_senden);
			button2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Mail.sendMail(
							usernameKontakt,
							MainActivity.USERNAME
									+ " hat Feedback gesendet. Klicken Sie hier um zu antworten\n mailto:"
									+ MainActivity.USERNAME
									+ "@dhbw-loerrach.de?subject=Feedback DHBW-InfoBoard"
									+ "\n"
									+ nachricht.getText().toString(),
							"Feedback f�r DHBW-InfoBoard");

					Toast.makeText(getApplicationContext(),
							R.string.nachricht_wurde_versendet, Toast.LENGTH_LONG)
							.show();
					finish();

				}
			});
		}
		

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
