package com.example.blackboarddhbwloe.main;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.tools.ImageFTPClient;
/**
 * 
 * DetailView ist die Klasse in welcher die Funktionen und Darstellung,
 *  der Detail-View eines Inserates implementiert sind.
 *
 */
public class DetailView extends Activity implements OnTouchListener {

	public static int ANGEBOTSID;
	public static int POSITION;
	private int userID;

	static ImageView iv;
	static ImageFTPClient myFtpClient;

	private GestureDetector detector;
	private Vibrator vibrate;
	private ScrollView sview;
	private ResultSet rs;

	private String path;
	static String inseratErsteller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		reconnectFTP();

		setContentView(R.layout.activity_detail_view);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setGestureListener();

		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		sview = (ScrollView) findViewById(R.id.LinearLayout1);
		sview.setOnTouchListener(this);

		iv = (ImageView) findViewById(R.id.detailView_imageView);
		iv.setOnTouchListener(this);

		getDetailViewData();
		setDetailViewData();
		setDetailViewImage();

	}
	/**
	 * reconnectFTP prüft ob es für die Klasse DetailView bereits ein objekt des ImageFTPClient gibt. 
	 * wenn nicht wird ein neues objekt erstellt und der statischen Variablen zugewiesen 
	 * 
	 * danach wird geprüft ob schon eine connection zu, FTP Server besteht, 
	 * wenn nicht   wird eine Verbindung aufgebaut. 
	 * 
	 */
	private void reconnectFTP() {
		if (myFtpClient == null) {
			myFtpClient = new ImageFTPClient();
		}
		if (!myFtpClient.getFtpConnectState()) {
			myFtpClient.ftpConnect();
		}
	}
	/**
	 * getDetailViewData ruft die Inserat-speziefischen Daten von der Datenbank ab.
	 */
	private void getDetailViewData() {
		rs = DB.getRSFromDB("select Inserate.id,titel,kategorie,preis,userID,biete,datum,beschreibung,username From Inserate, tblUser "
				+ "where Inserate.ID ="
				+ ANGEBOTSID
				+ " and Inserate.userID = tblUser.id" + " order by datum desc");

	}
	/**
	 * setDetailViewImage läd anhandt der UserID und der 
	 * Inseratnummer das dazugehörige Bild vom FTP server.
	 */
	private void setDetailViewImage() {
		Log.d("FTP DV_Image_Download", "startMethode");
		try {
			userID = Integer.parseInt(rs.getString("userID"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		path = "./" + userID + "/" + ANGEBOTSID + "_detail.jpg";
		Log.d("FTP DV_Image_Download", path);
		
		new ImageFTPDownload().execute(path);
	}
	
	
	/**
	 * setDetailViewData setzt alle Werte in die TextViews der Anzeige.
	 * Ein Button wird implementiert welcher nur sichtbar ist wenn der User der das Inserat bertachte der eigentümer des inserates ist.
	 *  
	 */
	private void setDetailViewData() {
		final TextView titel = (TextView) findViewById(R.id.tv_detailview_titel);

		TextView preis = (TextView) findViewById(R.id.tv_detailview_preis);
		TextView kategorie = (TextView) findViewById(R.id.tv_detailview_kategorie);
		TextView beschreibung = (TextView) findViewById(R.id.tv_detailview_beschreibung);

		try {
			rs.next();

			// Wenn das Inserat dem angemeldeten User gehört dann erzeuge den
			// loeschenButton
			if (rs.getString("userID").equals(MainActivity.USERID)) {
				Button buttonEntfernen = (Button) findViewById(R.id.button_detailView_angebotEntfernen);
				buttonEntfernen.setVisibility(View.VISIBLE);
				buttonEntfernen.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						eintragLoeschen();
					}
				});

			}

			titel.setText(rs.getString("titel"));
			preis.setText(rs.getString("preis") + getString(R.string.euro));
			beschreibung.setText(rs.getString("beschreibung"));
			inseratErsteller = rs.getString("username");

			kategorie.setText(getResources().getStringArray(R.array.kat)[rs
					.getInt("kategorie")]);
			System.out.println(Integer.parseInt(rs.getString("kategorie")));
			// String s = getString(Integer.parseInt(rs.getString("kategorie")),
			// R.array.kat) ;

			String sucheBiete = "Biete";
			if (rs.getInt("biete") != 0) {
				sucheBiete = "Suche";
			}
			getActionBar().setTitle(sucheBiete + " : " + rs.getString("titel"));

			setDetailViewImage();

			ImageButton contactform = (ImageButton) findViewById(R.id.button_detailview_profilansicht);
			contactform.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// Wenn Benutzer angemeldet
					if (MainActivity.isAnmeldestatus() == true) {

						Intent intentAngebote = new Intent(
								"com.example.blackboarddhbwloe.PROFILANSICHT");
						intentAngebote.putExtra("userID",
								Integer.toString(userID));
						intentAngebote.putExtra("titel", titel.getText()
								.toString());
						intentAngebote.putExtra("inseratErsteller",
								inseratErsteller);
						startActivity(intentAngebote);

					} else {
						// Weiterleitung zum Login
						Intent intentLogin = new Intent(
								"com.example.blackboarddhbwloe.LOGINSCREEN");
						intentLogin.putExtra("activity", "fromProfilansicht");
						startActivity(intentLogin);

					}
				}
			});

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
/**
 * setGestureListener implementiert die Funtionen die beim klick auf das bild und beim wischen über den bildschirm geschehen sollen.
 */
	private void setGestureListener() {
		detector = new GestureDetector(this, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				double ivX = iv.getX();
				double ivY = iv.getY();
				double ivWidth = iv.getWidth();
				double ivHeight = iv.getHeight();
				int e1X = (int) e.getX();
				int e1Y = (int) e.getY();
				if (e1X > ivX && e1X < (ivX + ivWidth) && e1Y > ivY
						&& e1Y < ivY + ivHeight) {
					goFullScreen(iv);
				}
				System.out.println(" bild öffnen");

				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				double ivX = iv.getX();
				double ivY = iv.getY();
				double ivWidth = iv.getWidth();
				double ivHeight = iv.getHeight();

				int e1X = (int) e1.getX();
				int e2X = (int) e2.getX();
				System.out.println(e1.getY() + "e1Y " + e1X + "e1x " + ivX
						+ "X " + ivY + "Y " + ivHeight + "height " + ivWidth
						+ "width ");
				int diffX = Math.abs(e1X - e2X);
				if (diffX >= 20) {
					System.out.println("JETZT Hallo Hier ich "
							+ Math.abs(e1.getAxisValue(MotionEvent.AXIS_Y)
									- e2.getAxisValue(MotionEvent.AXIS_Y)));

					if (Math.abs(e1.getAxisValue(MotionEvent.AXIS_Y)
							- e2.getAxisValue(MotionEvent.AXIS_Y)) <= 250) {
						if (e1.getAxisValue(MotionEvent.AXIS_X) <= e2
								.getAxisValue(MotionEvent.AXIS_X)) {
							if (DetailView.POSITION > 0) {
								DetailView.POSITION = DetailView.POSITION - 1;
							} else {
								DetailView.POSITION = InseratListe.AngeboteLookup
										.size() - 1;
							}
						} else {
							if (DetailView.POSITION < InseratListe.AngeboteLookup
									.size() - 1) {
								DetailView.POSITION = DetailView.POSITION + 1;
							} else {
								DetailView.POSITION = 0;
							}
						}
						DetailView.ANGEBOTSID = Integer
								.parseInt(InseratListe.AngeboteLookup.get(
										DetailView.POSITION).get(
										InseratListe.KEY_ID));
						Intent intentAngebote = new Intent(
								"com.example.blackboarddhbwloe.DETAILVIEW");
						vibrate.vibrate(25);
						startActivity(intentAngebote);
						finish();
					}
				}

				System.out.println(DetailView.POSITION
						+ "hallo Hier ich jetzt "
						+ e1.getAxisValue(MotionEvent.AXIS_X) + "second"
						+ e2.getAxisValue(MotionEvent.AXIS_X));
				return true;

			}

			@Override
			public boolean onDown(MotionEvent e) {

				return false;
			}
		});
	}
/**
 * eintragLoeschen löscht das aktuelle Inserat.
 */
	public void eintragLoeschen() {

		AlertDialog.Builder builder = new AlertDialog.Builder(DetailView.this);
		builder.setTitle(R.string.eintrag_loeschen)
				.setMessage(R.string.m_chten_sie_ihr_inserat_wirklich_loeschen_)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								DB.entferneUserEintrag("delete from Inserate where id = "
										+ ANGEBOTSID
										+ " and userID= "
										+ MainActivity.USERID);

								ImageFTPClient ftpClient = new ImageFTPClient();
								ftpClient.ftpConnect();
								ftpClient.ftpRemoveFile("./"
										+ MainActivity.USERID + "/"
										+ ANGEBOTSID + "_detail.jpg");
								ftpClient.ftpRemoveFile("./"
										+ MainActivity.USERID + "/"
										+ ANGEBOTSID + "_list.jpg");
								ftpClient.ftpDisconnect();

								Intent intentAngebote = new Intent(
										"com.example.blackboarddhbwloe.main");
								startActivity(intentAngebote);
								finish();
								Toast.makeText(getApplicationContext(),
										R.string.inserat_wurde_gel_scht,
										Toast.LENGTH_LONG).show();
							}
						})
				.setNegativeButton(R.string.abbrechen,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).show();
	}
/**
 * goFullScreen zeigt das bild im FullScreen mode an.
 * @param v
 */
	public void goFullScreen(View v) {

		ImageFTPClient myFtpClient = new ImageFTPClient();
		myFtpClient.ftpConnect();

		Drawable image = iv.getDrawable();

		Dialog mSplashDialog = new Dialog(this);

		ImageView imageView = new ImageView(mSplashDialog.getContext());
		imageView.setImageDrawable(image);
		// imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));

		mSplashDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mSplashDialog.setContentView(imageView);
		mSplashDialog.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
				LayoutParams.FLAG_FULLSCREEN);
		mSplashDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mSplashDialog.setCancelable(true);

		mSplashDialog.show();
		myFtpClient.ftpDisconnect();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent("com.example.blackboarddhbwloe.main");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		detector.onTouchEvent(event);
		return false;
	}

	public class ImageFTPDownload extends AsyncTask<String, Void, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {

			Drawable image = myFtpClient.ftpDownload(getBaseContext(),
					params[0], "tempFile");

			return image;

		}

		protected void onPostExecute(Drawable result) {

			if (result != null) {
				iv.setImageDrawable(result);

			}
		}

	}

}