package com.example.blackboarddhbwloe.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;
import com.example.blackboarddhbwloe.tools.DatenAufbereiter;
import com.example.blackboarddhbwloe.tools.ImageFTPClient;

public class InseratListe extends Activity {
	public static final String KEY_TITEL = "titel";
	public static final String KEY_PREIS = "preis";
	public static final String KEY_KAT = "kategorie";
	public static final String KEY_ID = "ID";
	public static final String KEY_NEU = "neu";
	public static final String KEY_USERID = "userID";
	public static final String KEY_BESCHR = "beschreibung";
	
	public static ArrayList<HashMap<String, String>> AngeboteLookup ;

	public static ArrayList<String> imageURLQueue;
	public static ArrayList<Drawable> drawableList;
	
	public static Integer counter;

	public static ImageFTPClient ftpClient;
	public static ListView list;
	
	private DatenAufbereiter adapter;

	private Bundle container;

	// Definiere welche activity angezeigt wird wenn der zurueckbutton gedrueckt
	// wird
	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		
		String fromActivity = "";
		
		//try falls keine extras ausgelesen werden koennen
		try {
			fromActivity = intent.getStringExtra("from");
			if(fromActivity==null)
			{
				fromActivity="";
			}
		} catch (Exception e) {
			fromActivity="";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(fromActivity.equals("benutzerbereich"))
		{
			Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.BENUTZERBEREICH");
			startActivity(intentAngebote);
		}
		else
		{
			Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.main");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentAngebote);
			
		}
		
		super.onBackPressed();
	}
	/**
	 * In der onCreate Methode wird der BarTitel auf die aktuelle View angepasst,
	 * den Ftp connect aufgebaut und die  Daten der Liste übergeben.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_angebote);

		container = getIntent().getExtras();
		//Try wegen dem zurueck button bei dem der container nicht neu geladen wird
		try {
			getActionBar().setTitle(container.getString("barTitle"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		imageURLQueue = new ArrayList<String>();
		drawableList = new ArrayList<Drawable>();
		
		ftpClient = new ImageFTPClient();
		ftpClient.ftpConnect();

		String sql = container.getString("sql");
		setListData(sql);
		
		Log.d("Async Download", "Size URL Queue: " + imageURLQueue.size());
		
		for (counter = 0; counter < imageURLQueue.size(); counter++) {
			new ImageFTPDownload().execute(imageURLQueue.get(counter));
		}

		// DB.aktualisiereLetzterAngebotsAufrufInUserTable();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_inseratliste, menu);
		
		SearchManager searchManager =
		           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		    SearchView searchView =
		            (SearchView) menu.findItem(R.id.search).getActionView();
		    searchView.setSearchableInfo(
		            searchManager.getSearchableInfo(getComponentName()));
		    
		return true;
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
	
	@Override
	protected void onNewIntent(Intent intent){
		Log.d("newIntent", "started");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		      String query = intent.getStringExtra(SearchManager.QUERY);
		      setListData("select *, 0 as neu From Inserate where titel like '%"
						+ query + "%';");
		    }
	}
	/**
	 * <h1> Beschreibung </h1>
	 * <b>setListData(String sql) hol sich mithilve des SQL Strings einen Resultset aus der Datenbank 
	 * Die Arrylist wird dabei mit den Zeilen welche in Hasmaps geladen werden befüllt um die Datenstruktur beizubehalten.
	 * in der Hashmap fungieren die Spaltennamen der Tabellen der Datenbank als eindeutige IDs der geladenen Werte.
	 * @param sql    SQL String welche Daten dargesetellt werden sollen. </b>
	 * @return void 
	 * 
	 */

	public void setListData(String sql) {
		
		
		final ArrayList<HashMap<String, String>>  angebote = new ArrayList<HashMap<String, String>>();
		try {
			ResultSet resultset = DB.getRSFromDB(sql);

			try {
				while (resultset.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(KEY_TITEL, (resultset.getString(KEY_TITEL)));
					map.put(KEY_PREIS, (resultset.getString(KEY_PREIS)));
					map.put(KEY_ID, (resultset.getString(KEY_ID)));
					map.put(KEY_KAT, (resultset.getString(KEY_KAT)));
					map.put(KEY_USERID, (resultset.getString(KEY_USERID)));
					map.put(KEY_BESCHR, (resultset.getString(KEY_BESCHR)));

					String srcFilePath = "./" + resultset.getString(KEY_USERID)
							+ "/" + resultset.getString(KEY_ID) + "_list.jpg";

					imageURLQueue.add(srcFilePath);

					// Wenn Benutzer angemeldet lese das Flag (fuer neue
					// Angebote) auch aus

					if (!MainActivity.USERID.equals("")) {
						System.out.println(resultset.getDate("datum"));
						System.out.println(MainActivity.LASTLOGIN);
						if (MainActivity.LASTLOGIN.before(resultset
								.getDate("datum"))) {
							map.put(KEY_NEU, "1");
						} else {
							map.put(KEY_NEU, "0");
						}
					}
					DB.aktualisiereLetzterAngebotsAufrufInUserTable();

					angebote.add(map);

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		list = (ListView) findViewById(R.id.list);
		
		AngeboteLookup = angebote;
		
		adapter = new DatenAufbereiter(this, angebote);
		list.setAdapter(adapter);
		list.setFocusable(true);
		list.requestFocus();
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println(position);
				DetailView.ANGEBOTSID = Integer.parseInt(angebote.get(position)
						.get(KEY_ID));
				DetailView.POSITION = position;
				ftpClient.ftpDisconnect();
				gotoDetail();

			}
		});
		

	}
	/**
	 * <h1> Beschreibung </h1>
	 * geheZurDetailView() startet die Activity DetailView anhand der statischen 
	 * AngebotsID welche durch den in setzeListeDaten() definierten onItemClickListener gesetzt wird.
	 * 
	 */
	public void gotoDetail() {
		Intent intentAngebote = new Intent("com.example.blackboarddhbwloe.DETAILVIEW");
		
		startActivity(intentAngebote);
	}

	public class ImageFTPDownload extends AsyncTask<String, Void, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {

			Drawable image = ftpClient.ftpDownload(getBaseContext(), params[0],
					"tempFile"+counter);

			return image;

		}
		
		@Override
		protected void onPostExecute(Drawable result){
			Log.d("DrawableList Size: ", ""+drawableList.size());
			if (result != null){
				drawableList.add(result);
			} else{
				drawableList.add(getResources().getDrawable(R.drawable.bb_noimage));
			}
				
			list.invalidateViews();
		}

	}

}
