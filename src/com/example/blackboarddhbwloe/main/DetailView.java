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
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.blackboarddhbwloe.tools.Mail;
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
	static String inseratTitel;
	static String inseratInhaberID;
	static int inseratID;
	
	//variable verhindert das mehrmalige Drücken den Butto Inserat melden.
	boolean inseratWurdeGemeldet=false; 

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_actionbar_detailview, menu);
	    System.out.println("TEEEEEEST: " +inseratInhaberID);
	    System.out.println("TEST: "+menu.size());
	    System.out.println("TEST2: "+menu.getItem(2).getItemId());
	    
	    
	    //Wenn das Inserat nicht dem angemeldeten User gehört dann verberge die menu-items: Löscheun und Verkauft, in der Actionbar. 
	    if (inseratInhaberID.equals(MainActivity.USERID)==false) {
	    	
	    	MenuItem menuMelden = menu.findItem(R.id.action_Verkauft);
	    	MenuItem menuLoeschen = menu.findItem(R.id.action_loeschen);

	    	menu.removeItem(menuMelden.getItemId());
	    	menu.removeItem(menuLoeschen.getItemId());

	    }
		return super.onCreateOptionsMenu(menu);
	}
	

	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    
	    case android.R.id.home:
			Intent intent = new Intent("com.example.blackboarddhbwloe.main");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
	    case R.id.action_Kontakt:
	    	
	    	Intent intentAngebote = new Intent(
					"com.example.blackboarddhbwloe.PROFILANSICHT");
			intentAngebote.putExtra("userID",
					Integer.toString(userID));
			intentAngebote.putExtra("titel", inseratTitel);
			intentAngebote.putExtra("inseratErsteller",
					inseratErsteller);
			startActivity(intentAngebote);
		
		  break;
	    case R.id.action_Melden:
	    	eintragMelden();
	      break;
	    case R.id.action_Verkauft:
	      inseratAlsVerkauftMarkieren();
	      break;
	    case R.id.action_loeschen:
	    	eintragLoeschen();
		  break;
	    default:
	      break;
	    }
	    return true;
	  } 
	
	
	private void inseratAlsVerkauftMarkieren() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(DetailView.this);
		builder.setTitle("Inserat abschließen?")
				.setMessage("Das Inserat wird danach nicht mehr angezeigt.")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// erzeuge ein Thread in dem die Abo-Daten in die DB geschrieben werden
								new Thread(new Runnable() {
									@Override
									public void run() {
										
										String sqlStatement = "Update Inserate set status = '2' where id = "+inseratID;
										
										DB.addValueToDB(sqlStatement);
									}
								}).start();
								Toast.makeText(getApplicationContext(),
										"Dein Inserat wurde als verkauft markiert",
										Toast.LENGTH_LONG).show();
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

	/**
	 * reconnectFTP prÃ¼ft ob es fÃ¼r die Klasse DetailView bereits ein objekt des ImageFTPClient gibt. 
	 * wenn nicht wird ein neues objekt erstellt und der statischen Variablen zugewiesen 
	 * 
	 * danach wird geprÃ¼ft ob schon eine connection zu, FTP Server besteht, 
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
		rs = DB.getRSFromDB("select Inserate.id,titel,kategorie,preis,userID,biete,datum,beschreibung,username, anzahlMeldungen, gemeldetVon From Inserate, tblUser "
				+ "where Inserate.ID ="
				+ ANGEBOTSID
				+ " and Inserate.userID = tblUser.id" + " order by datum desc");

	}
	/**
	 * setDetailViewImage lÃ¤d anhandt der UserID und der 
	 * Inseratnummer das dazugehÃ¶rige Bild vom FTP server.
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
	 * Ein Button wird implementiert welcher nur sichtbar ist wenn der User der das Inserat bertachte der eigentÃ¼mer des inserates ist.
	 *  
	 */
	private void setDetailViewData() {
		final TextView titel = (TextView) findViewById(R.id.tv_detailview_titel);

		TextView preis = (TextView) findViewById(R.id.tv_detailview_preis);
		TextView kategorie = (TextView) findViewById(R.id.tv_detailview_kategorie);
		TextView beschreibung = (TextView) findViewById(R.id.tv_detailview_beschreibung);

		try {
			rs.next();

			
			inseratTitel = rs.getString("titel");
			inseratInhaberID = rs.getString("userID");
			inseratID =  rs.getInt("id");
			
			titel.setText(inseratTitel);
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

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
/**
 * setGestureListener implementiert die Funtionen die beim klick auf das bild und beim wischen Ã¼ber den bildschirm geschehen sollen.
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
				System.out.println(" bild Ã¶ffnen");

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
							- e2.getAxisValue(MotionEvent.AXIS_Y)) <= 110) {
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
 * eintragLoeschen lÃ¶scht das aktuelle Inserat.
 */
	public void eintragLoeschen() {

		AlertDialog.Builder builder = new AlertDialog.Builder(DetailView.this);
		builder.setTitle(R.string.eintrag_loeschen)
				.setMessage(R.string.m_chten_sie_ihr_inserat_wirklich_loeschen_)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
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
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).show();
	}
	
	public void eintragMelden() {

		AlertDialog.Builder builder = new AlertDialog.Builder(DetailView.this);
		builder.setTitle("Beitrag Melden")
				.setMessage("Soll der Beitrag gemeldet werden?")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
							try {
								int anzahlMeldungen = rs.getInt("anzahlMeldungen");
								String gemeldetVon = rs.getString("gemeldetVon");
								
								//prüfe ob das Inserat bereits zuvor ODER gerade eben schon vom User gemeldet wurde. (zweite Bedinung verhindert Mail-SPAM) Wenn nein schreibe den Usernamen in das Feld gemeldetVon hinzu und erhöhe den counter anzahlMeldungen um 1
								if(gemeldetVon.contains(MainActivity.USERNAME)==false || inseratWurdeGemeldet==false)
								{
									inseratWurdeGemeldet=true;
									System.out.println("Meldung: username ist in gemeldetVon:"+gemeldetVon+","+MainActivity.USERNAME);
									//Schreibe den usernamen in das Feld gemeldetVon hinzu
									gemeldetVon = gemeldetVon+","+MainActivity.USERNAME;
									//erhöhe den Counter anzahlMeldungen um 1
									anzahlMeldungen=anzahlMeldungen+1;
									
									//Wenn Anzahl Meldungen jetzt bei 3 ist...
									if(anzahlMeldungen==3)
									{
										//Hole die Mail des Admin
										ResultSet rs2;
										rs2 = DB.getRSFromDB("SELECT benutzername FROM tblAdmin");
										while(rs2.next())
										{
										String adminMail = rs2.getString("benutzername");
										
										//Erzeuge eine Email-Benachrichtigung an den Admin
										Mail.sendMail(
												adminMail,
												gemeldetVon
														+ " haben das Inserat mit der ID="+ANGEBOTSID+" als Verstoß gemeldet.",
												"DHBW-Blackboard Verstoß. Inserat wurde gemeldet. Bitte um weitere Prüfung.");
										System.out.println("MELDUNG: Email an Admin gesendet: " + adminMail);
									}}
									
									//schreibe den neuen CounterWert und gemeldetVon in die DB
									DB.addValueToDB("UPDATE Inserate SET anzahlMeldungen="+anzahlMeldungen+",gemeldetVon='"+gemeldetVon+"' WHERE id="+ANGEBOTSID);
									System.out.println("MELDEUNG: Meldungsfelder aktualisiert");
								}
								else
								{
									System.out.println("MELDUNG: User hat bereits gemeldet");
								}
								

								
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
								Toast.makeText(getApplicationContext(),
										"Beitrag wurde gemeldet",
										Toast.LENGTH_LONG).show();
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

		@Override
		protected void onPostExecute(Drawable result) {

			if (result != null) {
				iv.setImageDrawable(result);

			}
		}

	}

}
