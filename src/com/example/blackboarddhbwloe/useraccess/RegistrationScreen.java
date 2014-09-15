package com.example.blackboarddhbwloe.useraccess;

import java.sql.ResultSet;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.tools.Mail;

public class RegistrationScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		getActionBar().setTitle(R.string.registrierung);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Button button = (Button) findViewById(R.id.buttonRegistrationSenden);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				EditText user = (EditText)findViewById(R.id.tv_Registration_Anmeldung_User);
				try{
					//Prüfe ob eine Eingabe vorhanden ist
					if(user.getText().toString().equals(""))
					{
						Toast.makeText(getApplicationContext(), "Bitte geben Sie Ihren Benutzernamen an", Toast.LENGTH_LONG).show();
					}
					else
					{

					ResultSet resultset = DB.getRSFromDB("Select * From tblUser Where username = '"+ user.getText()+"';");
					boolean erfolgreichAngemeldet = false;
						System.out.println(user.getText().toString());
						while(resultset.next())
						{
						
							Toast.makeText(getApplicationContext(), R.string.benutzer_bereits_vorhanden, Toast.LENGTH_LONG).show();
						
							erfolgreichAngemeldet = true;
						}
						
						if(erfolgreichAngemeldet==false)
						{
							Intent intentLogin = new Intent("com.example.blackboarddhbwloe.LOGINSCREEN");
							String usr = user.getText().toString();
							String psw = createPassword();
							
							DB.addValueToDB("INSERT INTO `BBDB`.`tblUser` (`username`, `psw`, `initial`, lastLogin) VALUES ('"+usr+"', '"+psw+"', '0', '2013-01-01 00:00:00');");
							String message = "Ihre Anmeldedaten : \n Username: "+usr+"\n Initialpasswort: "+psw+getString(R.string._mit_freundlichen_gr_ssen_ihr_bbteam);
							Mail.sendMail(usr, message, "Registrierung BBDHBW");
							intentLogin.putExtra("activity", "fromRegistration");
							startActivity(intentLogin);
							
							Toast.makeText(getApplicationContext(), "Email wurde versendet an:\n "+usr+"@dhbw-loerrach.de", Toast.LENGTH_LONG).show();
							//beende die activity (kein zurück möglich)
							finish();
						}
									
						}
				}
					
						
				catch (Exception e){
					Toast.makeText(getApplicationContext(), R.string.verbindung_fehlgeschlagen, Toast.LENGTH_LONG).show();
					
					
					e.printStackTrace();
					
				}
				
			}
		});
		
	}
	
	public String createPassword(){
		String s = "";
		Random r = new Random();
		
		s = s+(r.nextInt());
		return s;
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
