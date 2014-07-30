package com.example.blackboarddhbwloe;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.blackboarddhbwloe.tools.DB;

public class MainActivity extends Activity {

	static final int uniqueId = 34235;
	String altesAbos = "";
	public static boolean verbindungHergestellt = true; 

	private static boolean anmeldestatus = false;
	public static String USERNAME = "";
	public static String USERID = "";
	public static Date LASTLOGIN = null;

	public MainActivity holeThis() {
		return this;
	}

	public static boolean isAnmeldestatus() {
		return anmeldestatus;
	}

	public static void setAnmeldestatus(boolean anmeldestatus) {
		MainActivity.anmeldestatus = anmeldestatus;
	}

	@Override
	public void onBackPressed() {
		Intent homeIntent= new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(verbindungHergestellt==false)
		{
			datenverbindungFehlgeschlagen();
		}
		
		//System.out.println("Connection erfolgreich: "+verbindungHergestellt);
		// StrictMode ist dazu da damit die Policy connections nach aussen
		// zulaesst, MT
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(policy);

		// Starte den Notification Service

		starteNotificationListener();

		TextView tvUsername = (TextView) findViewById(R.id.tv_anmedestatus);
		if (!USERNAME.equals("")) {
			tvUsername.setText(getString(R.string.sie_sind_angemeldet_als_) + USERNAME);

		}

		Button buttonAngebote = (Button) findViewById(R.id.ButtonAngebote);
		buttonAngebote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String sql = "Select * From Inserate Where biete = 1 order by datum desc;";
				Bundle container = new Bundle();
				container.putString("sql", sql);

				Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
				intentAngebote.putExtras(container);

				intentAngebote.putExtra("barTitle", "Angebote");
				startActivity(intentAngebote);
				finish();

			}
		});

		Button buttonGesuche = (Button) findViewById(R.id.ButtonGesuche);
		buttonGesuche.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String sql = "Select * From Inserate Where biete = 0 order by datum desc;";
				Bundle container = new Bundle();
				container.putString("sql", sql);

				Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
				intentAngebote.putExtras(container);

				intentAngebote.putExtra("barTitle", "Gesuche");
				startActivity(intentAngebote);
				finish();
				
			}
		});

		Button buttonBenutzerbereich = (Button) findViewById(R.id.ButtonBenutzerbereich);
		buttonBenutzerbereich.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Wenn Benutzer angemeldet
				if (isAnmeldestatus() == true) {
					// Zeige die Angebote
					Intent intentAngebote = new Intent(
							"com.example.blackboarddhbwloe.BENUTZERBEREICH");
					startActivity(intentAngebote);
					finish();
					
				} else {
					// Weiterleitung zum Login
					Intent intentLogin = new Intent(
							"com.example.blackboarddhbwloe.LOGINSCREEN");
					intentLogin.putExtra("activity", "fromBenutzerbereich");
					startActivity(intentLogin);
				}

			}
		});

		Button buttonSuche = (Button) findViewById(R.id.ButtonDetailSuche);
		buttonSuche.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.DETAILSUCHE");

				startActivity(intentAngebote);
				
			}
		});

		Button buttonAnmeldung = (Button) findViewById(R.id.button_LoginScreen_Anmeldung);
		buttonAnmeldung.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.LOGINSCREEN");
				intentAngebote.putExtra("activity", "fromMain");
				startActivity(intentAngebote);

			}
		});
		
		//Funktion iconBar: Farbschema anpassen
		
		
//		defaultcolor für das icon wenn es angeklickt wurde
		Button buttonStatusSelected = (Button) findViewById(R.id.ImageButton03);
		buttonStatusSelected.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_dhbw_icon_bar_selected));
		
		
		
//		Button buttonBar = (Button) findViewById(R.id.ImageButton01);
//		buttonBar.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intentAngebote = new Intent(
//						"com.example.blackboarddhbwloe.INSERATLISTE");
//
//				startActivity(intentAngebote);
//				
//			}
//		});	
		
		Button buttonSucheBar = (Button) findViewById(R.id.ImageButton02);
		buttonSucheBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAngebote = new Intent(
						"com.example.blackboarddhbwloe.DETAILSUCHE");

				startActivity(intentAngebote);
				
			}
		});		
		
		Button buttonBenutzerbereichBar = (Button) findViewById(R.id.ImageButton04);
		buttonBenutzerbereichBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Wenn Benutzer angemeldet
				if (isAnmeldestatus() == true) {
					// Zeige die Angebote
					Intent intentAngebote = new Intent(
							"com.example.blackboarddhbwloe.BENUTZERBEREICH");
					startActivity(intentAngebote);
					finish();
				} else {
					// Weiterleitung zum Login
					Intent intentLogin = new Intent(
							"com.example.blackboarddhbwloe.LOGINSCREEN");
					intentLogin.putExtra("activity", "fromBenutzerbereich");
					startActivity(intentLogin);
				}
			}
		});

		
		
	}

	private void starteNotificationListener() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				int s = 1;
				boolean notificationErzeugen = true;
				// Starte einen durchgaenigen Zyklus
				while (s == 1) {

					// Wenn user nicht angemeldet ist mache nichts
					while (MainActivity.USERID == "") {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// Hat sich der User angemeldet beginne mit dem
					// notificationListener
					while (MainActivity.USERID != "") {

						// Durchsuche die Angebote nach abonierten Kategorien
						// und hole davon die Angebote mit
						// erstalldatum > letzteAktualisierungsabfrage des User
						// die nicht von ihm selbst erstellt wurden
						String sqlStatement = "SELECT Inserate.kategorie, Inserate.titel "
								+ "FROM Inserate, tblUser "
								+ "where tblUser.id = "
								+ MainActivity.USERID
								+ " and Inserate.userId != "
								+ MainActivity.USERID
								+ " and Inserate.datum > tblUser.lastLogin "
								+ " and kategorie in (select abos.kategorie from abos where userID = "
								+ MainActivity.USERID + ")";
						ResultSet rsNeueAngebote = DB.getRSFromDB(sqlStatement);

						int anzahlNeueAngebote = 0;
						// String angebotTitle = "";
						String sa = "";
						try {
							notificationErzeugen = true;
							while (rsNeueAngebote.next()) {
								anzahlNeueAngebote = anzahlNeueAngebote + 1;
			
								sa = rsNeueAngebote.getString("titel");
								if (sa.equals(altesAbos) == true) {
									notificationErzeugen = false;
								}
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// Neue Notification nur dann erzeugen wenn es
						// Ã„nderungen gibt und mehr als 0 neue Angebote
						if (notificationErzeugen == true) {
							altesAbos = sa;

							// Wenn eine Neuheit gefunden wurde erzeuge eine
							// Notification
							if (anzahlNeueAngebote > 0) {
								if (anzahlNeueAngebote == 1) {
									erzeugeNotification("BBAboService",
											getString(R.string.es_gibt_ein_neues_angebot));
								} else {
									erzeugeNotification("BBAboService",
											"Es gibt " + anzahlNeueAngebote
													+ " neue Angebote");
								}

							}
						}
						// Das aktuelisierungsdatum wird beim Aufruf der
						// activity angebote neu gesetzt

						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		});

		t.start();

	}

	private void erzeugeNotification(String notTitle, String notText) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.bb_notificationicon)
				.setContentTitle(notTitle).setContentText(notText)
				.setTicker(getString(R.string.dhbw_nachricht))
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setAutoCancel(true);
		// definiere die activity die aufgerufen wird wenn man die notification
		// anklickt
		Intent resultIntent = new Intent("com.example.blackboarddhbwloe.INSERATLISTE");
		String sqlStatement = "SELECT Inserate.* "
				+ "FROM Inserate, tblUser "
				+ "where tblUser.id = "
				+ MainActivity.USERID
				+ " and Inserate.userId != "
				+ MainActivity.USERID
				+ " and Inserate.datum > tblUser.lastLogin "
				+ " and kategorie in (select abos.kategorie from abos where userID = "
				+ MainActivity.USERID  +") " + "order by datum desc";
		
		Bundle container = new Bundle();
		container.putString("sql", sqlStatement);
		resultIntent.putExtras(container);
		resultIntent.putExtra("barTitle", "Neue Angebote");

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mit der uniqueId kann die notif spaeter aufgerufen werden
		mNotificationManager.notify(uniqueId, mBuilder.build());

	}
	
	private void datenverbindungFehlgeschlagen()
	{
	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	builder.setTitle(R.string.keine_verbindung_zum_internet)
			.setMessage(R.string.was_m_chten_sie_tun_)
			.setPositiveButton("Erneut versuchen",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							Intent i = getBaseContext().getPackageManager()
						             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);

						}
					})
			.setNegativeButton(R.string.beenden,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							System.exit(0);
						}
					}).show();
	}

}
