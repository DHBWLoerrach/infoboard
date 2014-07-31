package com.example.blackboarddhbwloe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.main.DetailView;

/**
 * <h1> Beschreibung </h1>
 * <b>  Die BBWidgetProvider fungiert als Receiver für alle Widgets der Applikation.
 * 		Er beinhaltet die Actionbenennung für die Broadcastintents des Widgets, z.B. für die Öffnung der DetailView eines Inserats als Strings und die in der Konfiguration generierten Sql-Strings in einer SparseArray, welche der WidgetId zugeordnet werden.</b>
 */

public class BBWidgetProvider extends AppWidgetProvider {
	public static String UPDATE_LIST = "com.example.blackboarddhbwloe.widget.BBWidgetProvider.UPDATE_LIST";
	public static String GO_DETAILVIEW = "com.example.blackboarddhbwloe.widget.BBWidgetProvider.GO_DETAILVIEW";
	public static String LIST_POSITION = "LIST_POSITION";

	static SparseArray<String> sqlStatements;

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  In der onUpdate Methode werden die Intents für die RemoteViewsFactory für die Listenerstellung und die Broadcast für die Buttons des Widgets hinzugefügt
	 * 		Die Methode wird automatisch bei Anlegen jedes Widgets ausgeführt.</b>
	 */
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIDs) {

		super.onUpdate(context, appWidgetManager, appWidgetIDs);

		sqlStatements = new SparseArray<String>();

		ComponentName thisWidget = new ComponentName(context,
				BBWidgetProvider.class);

		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		for (int widgetId : allWidgetIds) {

			RemoteViews widgetLayout = new RemoteViews(
					context.getPackageName(), R.layout.layout_widget);
			
			//Intenterstellung für die RemoteViewsFactory 
			Intent adapterIntent = new Intent(context,
					BBWidgetRemoteViewsService.class);
			adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					widgetId);
			adapterIntent.setData(Uri.parse(adapterIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			widgetLayout.setRemoteAdapter(R.id.widgetList, adapterIntent);

			Intent refreshIntent = new Intent(context, BBWidgetProvider.class);
			refreshIntent.setAction(BBWidgetProvider.UPDATE_LIST);
			refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					widgetId);
			refreshIntent.setData(Uri.parse(refreshIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
					context.getApplicationContext(), 0, refreshIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			widgetLayout.setOnClickPendingIntent(R.id.widgetSyncButton,
					refreshPendingIntent);

			//BroadcastIntent zur Öffnung der Konfugiration über das Widget
			Intent konfigIntent = new Intent(context, WidgetKonfiguration.class);
			konfigIntent.setAction("com.example.blackboarddhbwloe.widget.WidgetKonfiguration");
			konfigIntent
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			konfigIntent.putExtra("reConfig", true);
			konfigIntent.setData(Uri.parse(konfigIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent konfigPendingIntent = PendingIntent.getActivity(
					context.getApplicationContext(), 0, konfigIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			widgetLayout.setOnClickPendingIntent(R.id.widgetKonfigButton,
					konfigPendingIntent);
			
			//IntentTemplate für die Listenelemente des Widget zur Öffnung der DetailView des Inserats
			Intent detailIntent = new Intent(context, BBWidgetProvider.class);
			detailIntent.setAction(BBWidgetProvider.GO_DETAILVIEW);
			detailIntent
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			detailIntent.setData(Uri.parse(detailIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent detailPendingIntent = PendingIntent
					.getBroadcast(context, 0, detailIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			widgetLayout.setPendingIntentTemplate(R.id.widgetList,
					detailPendingIntent);
			
			//Refresh des Widget, damit Intentzuordnungen angewendet werden
			appWidgetManager.updateAppWidget(widgetId, widgetLayout);

		}
	}
	
	/**
	 * <h1> Beschreibung </h1>
	 * <b>  In der onReveive-Methode werden zu den vom Receiver empfangenen Intents anhand der Intent-Action bestimmten.
	 * 		Durch super.onReceive werden generische Aufrufe, z.B. der onUpdate-Methode implementiert.</b>
	 */

	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		if (intent.getAction() == BBWidgetProvider.UPDATE_LIST) {
			Log.d("Widget", "inside If Update_list");
			appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,
					R.id.widgetList);
		}
		if (intent.getAction() == BBWidgetProvider.GO_DETAILVIEW) {
			Log.d("Widget", "Inside Go Detail");
			DetailView.ANGEBOTSID = intent.getIntExtra(LIST_POSITION, 0);
			
			Intent intentDetailView = new Intent(
					"com.example.blackboarddhbwloe.DETAILVIEW");
			intentDetailView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intentDetailView);
		}
		
	}
}
