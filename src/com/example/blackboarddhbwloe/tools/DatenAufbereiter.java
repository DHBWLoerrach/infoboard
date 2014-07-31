package com.example.blackboarddhbwloe.tools;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.main.InseratListe;

@SuppressLint("ResourceAsColor")
public class DatenAufbereiter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	public DatenAufbereiter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * Ueberladene methode (siehe unten): Methode wird benutzt wenn die
	 * Angebotsliste ohne Suchfilter aufgebaut werden soll Ueberladen, da die
	 * Hashmap mit dem Eintrag "neu" (fuer neue Angebote) ausgelesen werden
	 * muss. Beim Suchfilter ohne "neu"
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_row, null);
		}

		TextView title = (TextView) vi.findViewById(R.id.title);
		TextView preis = (TextView) vi.findViewById(R.id.preis);
		TextView beschreibung = (TextView) vi.findViewById(R.id.kuBeschreibung);

		HashMap<String, String> angebot = new HashMap<String, String>();
		angebot = data.get(position);
		
		CharSequence subString = "";
		// Setting all values in listview
		title.setText(angebot.get(InseratListe.KEY_TITEL));
		preis.setText(angebot.get(InseratListe.KEY_PREIS) + " \u20AC");
		if (angebot.get(InseratListe.KEY_BESCHR).length() >= 25) {
			subString = angebot.get(InseratListe.KEY_BESCHR).substring(0, 24)
					+ "...";
		} else {
			subString = angebot.get(InseratListe.KEY_BESCHR);
		}
		beschreibung.setText(subString);

		// Wenn der benutzer angemeldet ist und die abfrage nicht aus dem
		// searchfield kommt, markiere die neuesten Angebote

		ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);

		try {
			if (InseratListe.drawableList.get(position) != null) {
				thumb_image.setImageDrawable(InseratListe.drawableList
						.get(position));
			} 
		} catch (Exception e) {
		}

		try {
			if (!MainActivity.USERID.equals("")) {

				if (angebot.get(InseratListe.KEY_NEU).equals("1")) {
					vi.setBackgroundResource(R.drawable.list_selector_newitem);
				} else {
					vi.setBackgroundResource(R.drawable.list_selector);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return vi;
	}

}
