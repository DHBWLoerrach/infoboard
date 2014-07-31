package com.example.blackboarddhbwloe.userarea;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.tools.ImageFTPClient;

public class ProfilAnsicht extends Activity {

	static int ANGEBOTSID;
	String path;
	static ImageView iv;
	ImageFTPClient myFtpClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profilansicht);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.profilansicht);

		iv = (ImageView) findViewById(R.id.image_profilansicht_bild);
		TextView name = (TextView) findViewById(R.id.tv_profilansicht_name);

		Intent intent = getIntent();
		String userID = intent.getStringExtra("userID");
		final String inserattitel = intent.getStringExtra("titel");
		final String erstellerName = intent.getStringExtra("inseratErsteller");
		String sname = "";
		String iTelnr = "0";
		ResultSet rs = DB
				.getRSFromDB("select name, tel from tblUser where id = "
						+ userID);

		try {
			while (rs.next()) {
				sname = rs.getString("name");
				iTelnr = rs.getString("tel");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final String telnr = iTelnr;

		if (sname.equals("keine Angabe")) {
			name.setText("anonym");
		} else {

			name.setText(sname);
		}

		ImageButton button = (ImageButton) findViewById(R.id.imageButton_profilansicht_mail);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.KONTAKT");
				intentAngebote.putExtra("titel", inserattitel);
				intentAngebote.putExtra("inseratErsteller", erstellerName);
				startActivity(intentAngebote);
				
			}
		});

		//Zeige anruf und SMS nur an, wenn eine Telnr vorhanden ist
		if (!telnr.equals("0")) {
			ImageButton buttonTel = (ImageButton) findViewById(R.id.imageButton_profilansicht_tel);
			buttonTel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					starteTelefonanruf(telnr);
				}
			});

			ImageButton buttonSMS = (ImageButton) findViewById(R.id.imageButton_profilansicht_sms);
			buttonSMS.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// SMS
					Uri sms_uri = Uri.parse("smsto:" + String.valueOf(telnr));
					Intent sms_intent = new Intent(Intent.ACTION_SENDTO,
							sms_uri);
					sms_intent
							.putExtra("sms_body", "BulletinBoardDHBW-Anfrage");
					startActivity(sms_intent);
					
				}
			});
		}
		else
		{
			ImageButton buttonTel = (ImageButton) findViewById(R.id.imageButton_profilansicht_tel);
			buttonTel.setVisibility(View.GONE);
			ImageButton buttonSMS = (ImageButton) findViewById(R.id.imageButton_profilansicht_sms);
			buttonSMS.setVisibility(View.GONE);
		}
		
		myFtpClient = new ImageFTPClient();
		myFtpClient.ftpConnect();
		 
		path = "./" + userID + "/" + userID + ".jpg";
		new ImageFTPDownload().execute(path);

	}


	private void starteTelefonanruf(final String telnr) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ProfilAnsicht.this);
		builder.setTitle("Telefonanruf")
				.setMessage(
						R.string.m_chten_sie_den_besitzer_des_inserats_anrufen_)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String uri = "tel:" + telnr;
								Intent intent = new Intent(Intent.ACTION_CALL);
								intent.setData(Uri.parse(uri));
								startActivity(intent);
								finish();
								System.out.println("starte Anruf");
							}
						})
				.setNegativeButton(R.string.abbrechen,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).show();
	}

	public void goFullScreen(View v) {

		ImageFTPClient myFtpClient = new ImageFTPClient();
		myFtpClient.ftpConnect();

		Drawable image = iv.getDrawable();

		Dialog mSplashDialog = new Dialog(this);

		ImageView imageView = new ImageView(mSplashDialog.getContext());
		imageView.setImageDrawable(image);
		// imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));

		mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSplashDialog.setContentView(imageView);
		mSplashDialog.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
				LayoutParams.FLAG_FULLSCREEN);
		mSplashDialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mSplashDialog.setCancelable(true);

		mSplashDialog.show();
		myFtpClient.ftpDisconnect();
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

	public class ImageFTPDownload extends AsyncTask<String, Void, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {

			Drawable image = myFtpClient.ftpDownload(getBaseContext(),
					params[0], "tempFile");

			return image;

		}

		@Override
		protected void onPostExecute(Drawable result) {

			if (result != null) {
				iv.setImageDrawable(result);
			}
		}

	}

}
