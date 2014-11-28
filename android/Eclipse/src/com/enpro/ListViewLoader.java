package com.enpro;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewLoader extends ListActivity {
	private TextView text;
	private List<String> listValues;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("list");
		super.onCreate(savedInstanceState);
		//text = (TextView) findViewById(R.id.mainText);

		listValues = new ArrayList<String>();
		listValues.add("Android");
		listValues.add("iOS");
		listValues.add("Symbian");
		listValues.add("Blackberry");
		listValues.add("Windows Phone");
		
		// initiate the listadapter
		ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this, 
				R.layout.list_layout , R.id.listText, listValues);
 
         // assign the list adapter
         setListAdapter(myAdapter);
	}

}
