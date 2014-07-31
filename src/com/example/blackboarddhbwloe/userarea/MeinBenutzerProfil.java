package com.example.blackboarddhbwloe.userarea;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.tools.ImageFTPClient;
import com.example.blackboarddhbwloe.tools.ImageHelper;

public class MeinBenutzerProfil extends Activity {

	final Context context = this;
	ImageHelper iHelper;
	String path;
	static ImageView iv;
	ImageFTPClient myFtpClient;
	
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
		setContentView(R.layout.activity_meinbenutzerprofil);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.mein_profil);
		
		// Views
		EditText name = (EditText) findViewById(R.id.tv_profil_name);
		EditText kontaktdaten = (EditText) findViewById(R.id.tv_meinprofil_telnr);
		iv = (ImageView) findViewById(R.id.profil_imageView);
		
		String sqlStatement = "select name, tel from tblUser where id = "+MainActivity.USERID;
		ResultSet rs = DB.getRSFromDB(sqlStatement);
		String sname = "";
		String sTel = "";
		try {
			while (rs.next())
			{
				sname = rs.getString("name");
				sTel = rs.getString("tel");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Wenn in der DB keine Angaben stehen schreibe nichts in die Textfelder da sonst der Textvorschlag ueberschrieben wird
		if(!sname.equals("keine Angabe"))
		{
			name.setText(sname);
		}
		if(!sTel.equals("keine Angabe"))
		{
			kontaktdaten.setText(sTel);
		}


		iHelper = new ImageHelper(this);
		
		Button buttonAngebote = (Button) findViewById(R.id.button_benutzerprofil_senden);
		buttonAngebote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				anDBSenden(v);
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.KONTOVERWALTUNG");
				startActivity(intentAngebote);
				finish();
			}
		});
		
		Button buttonImage = (Button) findViewById(R.id.button_profil_avatar);
		buttonImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				runImageHelper(v);

			}
		});
		
		//Falls ein bereits ein BenutzerBild vorhanden ist, zeige es an.
		try {
			myFtpClient = new ImageFTPClient();
			myFtpClient.ftpConnect();
			 
			path = "./" + MainActivity.USERID + "/" + MainActivity.USERID + ".jpg";
			new ImageFTPDownload().execute(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	

	public void runImageHelper(View v) {
		
		iHelper.openImagePicker(v);
	}

	public void anDBSenden(View v) {

		// Variabeln Start -->

		// Views
		EditText name = (EditText) findViewById(R.id.tv_profil_name);
		EditText tv_tel = (EditText) findViewById(R.id.tv_meinprofil_telnr);
		String telnr = tv_tel.getText().toString();
		String name2 = name.getText().toString();
		
		if(telnr.equals(""))
		{
			telnr="keine Angabe";
		}
		if(name2.equals(""))
		{
			name2="keine Angabe";
		}

		//String sqlStatement = "insert into tblUser('name','kontaktdaten') values ('"+name.getText().toString()+"','"+kontaktdaten.getText().toString()+"')";
		String sqlStatement = "update tblUser set name = '"+name2+"',"+" tel = '"+telnr+"' where id="+MainActivity.USERID;
		System.out.println(sqlStatement);
		
		
			
		DB.addValueToDB(sqlStatement);
			
			if (iHelper.sourcePath != "") {
				new AsynchFTPUpload().execute(MainActivity.USERID);
			}
			
			
			Toast.makeText(getApplicationContext(),
					"Benutzerprofil gespeichert",
					Toast.LENGTH_LONG).show();

		}
	

	@Override
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
			ImageView previewImage = (ImageView) findViewById(R.id.profil_imageView);
			previewImage.setImageBitmap(iHelper.previewImage);
		}

	}

	private class AsynchFTPUpload extends AsyncTask<String, Void, Void> {

		@Override
	protected Void doInBackground(String... t) {
		
			try {
					ImageFTPClient mFtpClient = new ImageFTPClient();
					Boolean status = mFtpClient.ftpConnect();
					Log.d("FTP Upload", "ftp connect " + status);
					
					String desProfilimage = MainActivity.USERID+".jpg";
					
					String desFileDir = "./"+MainActivity.USERID;
					
					status = mFtpClient.ftpMakeDirectory(desFileDir);
					Log.d("FTP Upload", "ftp create Dir: " + status);
					
					status = mFtpClient.ftpUpload(ImageHelper.imageList, desProfilimage, desFileDir, context);
					Log.d("FTP Upload", "List Image: " + status);
					
					
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
