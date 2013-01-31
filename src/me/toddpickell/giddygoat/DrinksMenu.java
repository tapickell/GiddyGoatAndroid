package me.toddpickell.giddygoat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DrinksMenu extends ListActivity {

	String drinks[];
	String descrips[];
	// load this from xml file


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		drinks = getResources().getStringArray(R.array.list);
		descrips = getResources().getStringArray(R.array.lValues);
		setListAdapter(new ArrayAdapter<String>(DrinksMenu.this,
				android.R.layout.simple_list_item_1, drinks));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Log.d("LIST VIEW", drinks[position] + " selected from menu");

		AlertDialog alert = new AlertDialog.Builder(DrinksMenu.this).create();
		alert.setTitle(drinks[position]);
		alert.setMessage(descrips[position]);

		alert.show();

	}

	
}
