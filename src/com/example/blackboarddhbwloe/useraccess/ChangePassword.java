package com.example.blackboarddhbwloe.useraccess;

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

public class ChangePassword extends Activity {
	
	@Override
	public void onBackPressed() {
		Intent intentAngebote = new Intent(
				"com.example.blackboarddhbwloe.KONTOVERWALTUNG");
		startActivity(intentAngebote);
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassword);

		getActionBar().setTitle(R.string.passwort_erneuern);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button button = (Button) findViewById(R.id.buttonChangePassword_ChangePass);
		button.setOnClickListener(new View.OnClickListener() {
			Intent intentMain = new Intent("com.example.blackboarddhbwloe.main");

			EditText et_newPSW = (EditText) findViewById(R.id.tv_changePasswort_PassNeu);
			EditText et_wdhPSW = (EditText) findViewById(R.id.tv_changePasswort_PassWdh);

			@Override
			public void onClick(View v) {

				String newPSW = et_newPSW.getText().toString();
				String wdhPSW = et_wdhPSW.getText().toString();

				if (newPSW.equals(wdhPSW)&&(newPSW.equals("")==false)) {
					DB.addValueToDB("UPDATE `BBDB`.`tblUser` SET `psw`='"
							+ newPSW + "', `initial`='1' WHERE `id`='"
							+ MainActivity.USERID + "';");

					Toast.makeText(getApplicationContext(),
							R.string.passwort_erfolgreich_geaendert, Toast.LENGTH_LONG)
							.show();
					startActivity(intentMain);

				} else {
					Toast.makeText(getApplicationContext(),
							R.string.passw_rter_stimmen_nicht_berein,
							Toast.LENGTH_LONG).show();
					et_newPSW.setText("");
					et_wdhPSW.setText("");
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
