package com.example.blackboarddhbwloe.widget;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.tools.DB;


/**
 * <h1> Beschreibung </h1>
 * <b>  Der BBWidgetRemoteViewsService dient zum Aufruf der RemoteViewsFactory.</b>
 */
public class BBWidgetRemoteViewsService extends RemoteViewsService {
	
	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die onGetViewFactory-Methode wird automatisch bei Start des Service aufgerufen und ruft die RemoteViewsFactory auf.</b>
	 */
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new BBWidgetRemoteViewsFactory(this.getApplicationContext(),
				intent);
	}
}
/**
 * <h1> Beschreibung </h1>
 * <b>  Die BBWidgetRemoteViewsFactory generiert anhand des für das Widget erstellten Sql-Strings die angezeigt Liste.
 * 		Sie beinhaltet eine statische ArrayList, welche mit HashMaps gefüllt wird. Eine HashMap enthält alle benötigten Daten eines Inserats.
 * 		Zusätzlich beinhaltet die Factory statische Strings für die Feldzuordnung der HashMaps.</b>
 */
class BBWidgetRemoteViewsFactory implements
		RemoteViewsService.RemoteViewsFactory {
	private Context mContext;
	private int widgetId;

	boolean conGeoeffnet;

	public static ArrayList<HashMap<String, String>> data;

	public static final String KEY_TITEL = "titel";
	public static final String KEY_PREIS = "preis";
	public static final String KEY_KAT = "kategorie";
	public static final String KEY_ID = "ID";
	public static final String KEY_NEU = "neu";
	public static final String KEY_USERID = "userID";
	public static final String KEY_BESCHR = "beschreibung";

	public BBWidgetRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;
		widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	@Override
	public void onCreate() {

	}
	
	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode getViewAt generiert das Listenelement anhand der Position in der Liste.</b>
	 * @return RemoteViews
	 */
	@Override
	public RemoteViews getViewAt(int position) {

		String subString;

		RemoteViews listLayout = new RemoteViews(mContext.getPackageName(),
				R.layout.widget_list_element);

		HashMap<String, String> inserat = new HashMap<String, String>();
		inserat = data.get(position);

		Bundle extras = new Bundle();
		extras.putInt(BBWidgetProvider.LIST_POSITION,
				Integer.parseInt(inserat.get(KEY_ID)));
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		listLayout.setOnClickFillInIntent(R.id.list_row_element, fillInIntent);

		listLayout
				.setTextViewText(R.id.tv_widget_title, inserat.get(KEY_TITEL));
		listLayout.setTextViewText(R.id.tv_widget_preis, inserat.get(KEY_PREIS)
				+ " \u20AC");
		if (inserat.get(KEY_BESCHR).length() >= 25) {
			subString = inserat.get(KEY_BESCHR).substring(0, 24) + "...";
		} else {
			subString = inserat.get(KEY_BESCHR);
		}
		listLayout.setTextViewText(R.id.tv_widget_kuBeschreibung, subString);

		return listLayout;
	}

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode getCount gibt den Wert für die Anzahl der Abläufe der getViewAt-Methode zurück. Der return Wert entsprichet der Anzahl an Datensätzen aus dem Sql-String.</b>
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode getViewTypeCount gibt an wieviele RemoteViews von der Methode getViewsAt zurückgegeben werden.</b>
	 */
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode onDataSetChanged ruft anhand des Sql-Strings des Widgets die Daten aus der Datenbank ab und fügt die erhaltenen Daten in eine HashMap ein und fügt die HashMaps in die ArrayList ein.
	 * 		Die Methode prüft bei Methodenstart, ob bereits eine Verbindung zur Datenbank besteht und benutzt diese, falls eine besteht. Falls keine besteht wird eine Konnektion geöffnet und dies im Boolean conGeoeffnet vermerkt. Die Verbindung wird am Ende der Methode wieder geschlossen.</b>
	 */

	@Override
	public void onDataSetChanged() {

		data = new ArrayList<HashMap<String, String>>();
		conGeoeffnet = false;
		try {
			if (DB.con.isClosed()) {
				conGeoeffnet = true;
				DB.setConnection();
			}
			ResultSet resultset = DB.getRSFromDB(BBWidgetProvider.sqlStatements
					.get(widgetId));
			try {
				while (resultset.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(KEY_TITEL, (resultset.getString(KEY_TITEL)));
					map.put(KEY_PREIS, (resultset.getString(KEY_PREIS)));
					map.put(KEY_ID, (resultset.getString(KEY_ID)));
					map.put(KEY_KAT, (resultset.getString(KEY_KAT)));
					map.put(KEY_USERID, (resultset.getString(KEY_USERID)));
					map.put(KEY_BESCHR, (resultset.getString(KEY_BESCHR)));

					data.add(map);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conGeoeffnet) {
				DB.closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
	}

}
