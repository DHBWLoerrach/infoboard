package com.example.blackboarddhbwloe.widget;

import com.example.blackboarddhbwloe.R;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class WidgetKonfiguration extends Activity {

	static String sqlStatement;
	int widgetId;
	Intent resultValue = new Intent();
	boolean reConfig;

	/**
	 * <h1>Beschreibung</h1> <b> In der Mehtode onCreate wird der Spinner mit
	 * den Werten aus dem katSuche Arrays befüllt, die WidgetId aus dem
	 * aufrufendem Intent gelesen und der Result-Wert der Activity auf den Wert
	 * "RESULT_CANCELED" gesetzt. Durch den Result-Wert wird das Widget, falls
	 * der User die Konfiguration nicht abschliest, nicht angelegt.</b>
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_konfiguration);

		Spinner spinner = (Spinner) findViewById(R.id.Kategorie);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.katSuche, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			reConfig = extras.getBoolean("reConfig");
		}

		setResult(RESULT_CANCELED, resultValue);
	}

	/**
	 * <h1>Beschreibung</h1> <b> In der Methode widgetAnlegen wird der
	 * Sql-String für das Widget mit den eingegebenen Werten erstellt und der
	 * Result-Wert auf "RESULT_OK" gesetzt, damit das Widget angelegt wird. Für
	 * die Erstellung des Sql-Strings wird jedes EditText, der Spinner und die
	 * RadioButtons geprüft und falls eine Eingabe oder Auswahl vorliegt, wird
	 * diese in den Sql-String aufgenommen. Bei jeder Feldprüfung wird anhand
	 * des Boolean-Wertes firstValue überprüft, ob es sich um die erste Eingabe
	 * oder auswahl handelt und eine Bedingung mit "WHERE" in den SQL-Strings
	 * einfügt werden muss. Falls eine Bedingung eingefügt wurde, wird der
	 * Boolean-Wert insertAnd auf true gesetzt, damit bei einer weiteren
	 * Bedingung ein "AND" zwischen die Bedingungen des Sql-String gesetzt wird.
	 * Nach der Generierung des Sql-Strings wird dieser mit der WidgetId gemapt
	 * in die SparseArray gespeichert und ein Intent erstellt, um die
	 * onUpdate-Methode des BBWidgetProvider zu starten.</b>
	 */
	public void widgetAnlegen(View v) {

		sqlStatement = "SELECT * From Inserate";

		Boolean insertAND = false;
		Boolean firstValue = true;

		EditText textSuchFeld = (EditText) findViewById(R.id.SuchfeldText);
		CheckBox cBoxSuche = (CheckBox) findViewById(R.id.CBoxSearch);
		Spinner katSuche = (Spinner) findViewById(R.id.Kategorie);
		EditText textPreisVon = (EditText) findViewById(R.id.Preis_von);
		EditText textPreisBis = (EditText) findViewById(R.id.Preis_bis);
		RadioGroup radioSuche = (RadioGroup) findViewById(R.id.radioGroupDetailSuche);

		if (!textSuchFeld.getText().toString().equals("")) {
			firstValue = checkIfFirstValue(firstValue);
			sqlStatement += "titel like '%" + textSuchFeld.getText().toString()
					+ "%'";
			insertAND = true;
		}

		if (cBoxSuche.isChecked()
				&& !textSuchFeld.getText().toString().equals("")) {
			sqlStatement += " OR beschreibung like '%"
					+ textSuchFeld.getText().toString() + "%'";
			insertAND = true;
		}

		if (katSuche.getSelectedItemId() != 0) {
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "kategorie=" + (katSuche.getSelectedItemId() - 1);
			insertAND = true;
		}

		if (!textPreisVon.getText().toString().equals("")
				&& !textPreisBis.getText().toString().equals("")) {
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "preis BETWEEN "
					+ textPreisVon.getText().toString() + " AND "
					+ textPreisBis.getText().toString();
		} else {
			if (!textPreisVon.getText().toString().equals("")) {
				firstValue = checkIfFirstValue(firstValue);
				insertSqlAnd(insertAND);
				sqlStatement += "preis > " + textPreisVon.getText().toString();
			}
			if (!textPreisBis.getText().toString().equals("")) {
				firstValue = checkIfFirstValue(firstValue);
				insertSqlAnd(insertAND);
				sqlStatement += "preis < " + textPreisBis.getText().toString();
			}
		}

		switch (radioSuche.getCheckedRadioButtonId()) {
		case R.id.radioBiete:
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "biete = 1";
			break;
		case R.id.radioSuche:
			firstValue = checkIfFirstValue(firstValue);
			insertSqlAnd(insertAND);
			sqlStatement += "biete = 0";
			break;
		default:
			break;
		}

		sqlStatement += " ORDER BY datum desc;";

		BBWidgetProvider.sqlStatements.append(widgetId, sqlStatement);

		if (reConfig) {
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(getBaseContext());
			appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,
					R.id.widgetList);

		} else {
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			resultValue.setAction("android.appwidget.action.APPWIDGET_UPDATE");
			resultValue.setData(Uri.parse(resultValue
					.toUri(Intent.URI_INTENT_SCHEME)));
			setResult(RESULT_OK, resultValue);
			getApplicationContext().sendBroadcast(resultValue);
		}
		finish();

	}

	/**
	 * <h1>Beschreibung</h1> <b> Die Methode checkIfFirstValue dient zur einfach
	 * Überprüfung, ob es sich um die erste gesetzte Bedingung handelt und dem
	 * entsprechend dem Einfügen des "WHERE" Strings in den Sql-String.</b>
	 */
	private Boolean checkIfFirstValue(Boolean firstValue) {
		if (firstValue) {
			sqlStatement += " WHERE ";
			return false;
		}
		return false;
	}

	/**
	 * <h1>Beschreibung</h1> <b> Die Methode insertSqlAnd dient zur einfach
	 * Überprüfung, ob bereits eine Bedingung gesetzt wurde und dem entsprechend
	 * dem Einfügen des "AND" Strings in den Sql-String.</b>
	 */
	private void insertSqlAnd(Boolean insertAND) {
		if (insertAND) {
			sqlStatement += " AND ";
		}
	}

}
