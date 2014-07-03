package com.example.blackboarddhbwloe.userarea;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.tools.ImageFTPClient;
import com.example.blackboarddhbwloe.tools.ImageHelper;

public class AnzeigeSchalten extends Activity {
	
	final Context context = this;
	ImageHelper iHelper;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anzeigeschalten);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Spinner spinner = (Spinner) findViewById(R.id.Kategorie);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.kat, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		iHelper = new ImageHelper(this);

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


	public void runImageHelper(View v) {
		
		iHelper.openImagePicker(v);
	}

	public void anDBSenden(View v) {

		// Variabeln Start -->

		// Views
		EditText titel = (EditText) findViewById(R.id.InseratTitel);
		Spinner kategorie = (Spinner) findViewById(R.id.Kategorie);
		EditText preis = (EditText) findViewById(R.id.Preis);
		EditText beschreibung = (EditText) findViewById(R.id.Beschreibung);
		RadioGroup bieteSuche = (RadioGroup) findViewById(R.id.radioGroupBieteSuche);

		// hilfsvariablen
		int biete = 1; // int weil der wert direkt an die DB weitergeleitet wird
		boolean userTitelError = false; // user hat titel schon verwendet
		boolean titelError = false; // titel feld ist leer
		boolean preisError = false; // preis feld ist leer

		// <--- Ende Variablen

		// Bedingungen Start -->

		// Biete oder Suche biete = 1 => angebot 0 => suche
		if (bieteSuche.getCheckedRadioButtonId() == R.id.radioSuche) {
			biete = 0;
		}

		System.out.println(bieteSuche.getCheckedRadioButtonId());
		System.out.println(R.id.radioSuche);

		/*
		 * Eingabe wird auf redundanz des Users geprueft alle Titel des Users
		 * werden ausgelesen und mit dem Eingegebenen Titel verglichen im falle
		 * einer Uebereinstimmung userTitelError-> true
		 */
		ResultSet rs = DB
				.getRSFromDB("select titel From Inserate where userID ='"
						+ MainActivity.USERID + "';");
		try {
			while (rs.next()) {
				if (rs.getString("titel").equals(titel.getText().toString())) {
					userTitelError = true;
					titel.setError(getString(R.string.anzeige_mit_selbem_titel_schon_verwendent));
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// User hat den titel schon verwendet ?
		if (userTitelError) {

		}

		// im falle einer
		if (biete == 1) {
			if (preis.getText().toString().equals("")) {
				preis.setError(getString(R.string.bitte_preis_angeben));
				preisError = true;
			}
		}

		// Titelfeld wird ueberprueft ob eine Eingabe erfolgt ist
		if (titel.getText().toString().equals("")) {
			titel.setError(getString(R.string.bitte_titel_eingeben));
			titelError = true;
		}

		// <--- Ende Bedingungen

		// wird in DB geschrieben
		if (!userTitelError && !preisError && !titelError) {
			String t = titel.getText().toString();
			String k = "" + kategorie.getSelectedItemId();
			String p = preis.getText().toString();
			
			if (biete == 0) {
				p = "" + 0;
			}
			String b = beschreibung.getText().toString();

			String sqlStatement = "INSERT INTO `BBDB`.`Inserate` (`titel`, `kategorie`,`preis`,`beschreibung`, `userID`,`datum`,`biete`) VALUES ('"
					+ t
					+ "', '"
					+ k
					+ "', '"
					+ p
					+ "', '"
					+ b
					+ "', '"
					+ MainActivity.USERID 
					+ "', current_timestamp()"
					+ ", '"
					+biete
					+ "');";
			System.out.println(sqlStatement);
			DB.addValueToDB(sqlStatement);
			
			if (iHelper.sourcePath != "") {
				new AsynchFTPUpload().execute(t);
			}

			String sql = "Select * From Inserate order by datum desc;";
			Bundle container = new Bundle();
			container.putString("sql", sql);
			
			Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
			intentAngebote.putExtra("barTitle", "Inserate");
			intentAngebote.putExtras(container);
			startActivity(intentAngebote);
//			this.finish();

		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(iHelper.tag, "onActivityResult started");
		if (resultCode != Activity.RESULT_OK) {
			System.out.println(iHelper.tag + "  No Image taken");
			return;
		}

		if (requestCode == ImageHelper.PICK_FROM_FILE) {
			iHelper.mImageCaptureUri = data.getData();
			iHelper.sourcePath = iHelper
					.getRealPathFromURI(iHelper.mImageCaptureUri); // from
																	// Gallery

			if (iHelper.sourcePath == null)
				iHelper.sourcePath = iHelper.mImageCaptureUri.getPath(); // from
																			// File
																			// Manager

		} else {
			iHelper.sourcePath = iHelper.mImageCaptureUri.getPath();
		}

		System.out.println(iHelper.tag + "  onACtivityResult finished");

		new ASynchImageHelper().execute();

	}

	private class ASynchImageHelper extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			iHelper.imageScaling();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("AsynchTask", "PostExecute started");
			ImageView previewImage = (ImageView) findViewById(R.id.previewImage);
			findViewById(R.id.TextView_bild).setVisibility(View.VISIBLE);
			previewImage.setVisibility(View.VISIBLE);
			previewImage.setImageBitmap(iHelper.previewImage);
		}

	}

	private class AsynchFTPUpload extends AsyncTask<String, Void, Void> {

		@Override
	protected Void doInBackground(String... t) {
		
			try {
					
					String sqlStatement = "Select id FROM BBDB.Inserate WHERE titel= '"+t[0]+"' and userID= '"+MainActivity.USERID+"';";
					Log.d("FTP Upload", "SQL String: "+sqlStatement);
					
					ResultSet resultID = DB.getRSFromDB(sqlStatement);
					resultID.next();
								
					ImageFTPClient mFtpClient = new ImageFTPClient();
					Boolean status = mFtpClient.ftpConnect();
					Log.d("FTP Upload", "ftp connect " + status);
					
					String desFileNameList = resultID.getString("ID")+"_list.jpg";
					String desFileNameDetail = resultID.getString("ID")+"_detail.jpg";
					String desFileDir = "./"+MainActivity.USERID;
					
					status = mFtpClient.ftpMakeDirectory(desFileDir);
					Log.d("FTP Upload", "ftp create Dir: " + status);
					
					status = mFtpClient.ftpUpload(ImageHelper.imageList, desFileNameList, desFileDir, context);
					Log.d("FTP Upload", "List Image: " + status);
					
					status = mFtpClient.ftpUpload(ImageHelper.imageDetail, desFileNameDetail, desFileDir, context);
					Log.d("FTP Upload", "Detail Image: " + status);
					
					status = mFtpClient.ftpDisconnect();
					Log.d("FTP Upload", "ftp disconnect " + status);
			} catch (Exception e){
				Log.d("FTP Upload", "Upload Failed: "+e);
			}
		
		return null;
	}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(getApplicationContext(), R.string.upload_abgeschlossen_,
					Toast.LENGTH_LONG).show();
		}

	}

}
