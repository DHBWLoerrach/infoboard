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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.useraccess.LoginScreen;

public class MainActivity extends Activity {

	static final int uniqueId = 34235;
	String altesAbos = "";
	static boolean notificationErzeugt = false;
	

	private static boolean anmeldestatus = false;
	public static String USERNAME = "";
	public static String USERID = "";
	public static Date LASTLOGIN = null;
	boolean sucheBieteSichtbar = false;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_Support:
	    	Intent intentActionbar = new Intent("com.example.blackboarddhbwloe.KONTAKT");
			intentActionbar.putExtra("from", "support");
			startActivity(intentActionbar);
		
		  break;
	    case R.id.action_Feedback:
	    	Intent intentActionbar2 = new Intent("com.example.blackboarddhbwloe.KONTAKT");
			intentActionbar2.putExtra("from", "feedback");
			startActivity(intentActionbar2);
			
	      break;
	    case R.id.action_Datenschutz:
	      Toast.makeText(this, "öffne die Datenschutzerklärung", Toast.LENGTH_SHORT)
	          .show();
	      break;
	    case R.id.action_Beenden:
		      Toast.makeText(this, "Anwenung beenden", Toast.LENGTH_SHORT)
		          .show();
		  break;
	    default:
	      break;
	    }
	    return true;
	  } 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(LoginScreen.verbindungHergestellt==false)
		{
			datenverbindungFehlgeschlagen();
		}
		
		//System.out.println("Connection erfolgreich: "+verbindungHergestellt);


		// Starte den Notification Service

		//Prüfe ob der Service bereits gestartet wurde. -Verhindert, dass beim erneuten Aufruf der MainActivity der listener ein zweites Mal gestartet wird.
		if (notificationErzeugt==false)
		{
			starteNotificationListener();
		}
		

		TextView tvUsername = (TextView) findViewById(R.id.tv_anmedestatus);
		if (!USERNAME.equals("")) {
			tvUsername.setText(getString(R.string.sie_sind_angemeldet_als_) + USERNAME);

		}

		
	
		
		//Funktion iconBar: Farbschema anpassen
		
		
//		defaultcolor für das icon wenn es angeklickt wurde
		Button buttonStatusSelected = (Button) findViewById(R.id.ImageButton03);
		buttonStatusSelected.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_dhbw_icon_bar_selected));
		
		
		Button buttonDetailSucheBar = (Button) findViewById(R.id.ImageButton02);
		buttonDetailSucheBar.setOnClickListener(new View.OnClickListener() {

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
				
					// Zeige die Angebote
					Intent intentAngebote = new Intent(
							"com.example.blackboarddhbwloe.BENUTZERBEREICH");
					startActivity(intentAngebote);
					finish();
				}
			
		});
		
		
		Button buttonSucheBar = (Button) findViewById(R.id.Button05);
		buttonSucheBar.setOnClickListener(new View.OnClickListener() {
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

		Button buttonBieteBar = (Button) findViewById(R.id.Button06);
		buttonBieteBar.setOnClickListener(new View.OnClickListener() {
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

		
		Button buttonSucheBieteBar = (Button) findViewById(R.id.ImageButton01);
		buttonSucheBieteBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//open/close SucheBiete Bar
				Button buttonSucheSelected = (Button) findViewById(R.id.Button05);
				Button buttonBieteSelected = (Button) findViewById(R.id.Button06);
				if (sucheBieteSichtbar==false)
				{					
					buttonSucheSelected.setVisibility(View.VISIBLE);
					buttonBieteSelected.setVisibility(View.VISIBLE);
					sucheBieteSichtbar=true;
				}
				else
				{
					buttonSucheSelected.setVisibility(View.INVISIBLE);
					buttonBieteSelected.setVisibility(View.INVISIBLE);
					sucheBieteSichtbar=false;
				}

				}
			
		});
	}

	
	private void starteNotificationListener() {
		
				
		Thread t = new Thread(new Runnable() {
			
			@Override
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

						try {//der Listener prüft alle 60sec nach neuen Einträgen
							Thread.sleep(60000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						notificationErzeugt=true;
						System.out.println("NotificationService hat nach neuen Einträgen gesucht.");
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
						@Override
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
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							System.exit(0);
						}
					}).show();
	}
	
}
