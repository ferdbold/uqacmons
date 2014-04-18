package edu.uqac.multimedia.uqacmons;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class UqacmonPedia extends ListActivity {
	
	private ProfsDbAdapter mDbHelper;

	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    
    //Variables
	private ImageButton Radar;
	List<String> tableauUqacmons;
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_uqacmon_pedia);
		//Get UI
		Radar = (ImageButton) findViewById(R.id.b_radar);
		//Set variables 
		tableauUqacmons = new ArrayList<String>();
		
		Radar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vue) {
				Intent i = new Intent(UqacmonPedia.this,RadarActivity.class);
				startActivity(i);
			}
		});
		
		adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  listItems);
	        setListAdapter(adapter);
	        addItems();
	        
	        
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uqacmon_pedia, menu);
		return true;
	}
	
	 @Override
     protected void onListItemClick(ListView l, View v, int position, long id) { 
           super.onListItemClick(l, v, position, id);
              int itemPosition = position;
              // faire qqc une fois bouton cliquer.
              /*Intent i = new Intent(NoteActivity.this, MainActivity.class);
              i.putExtra("ID", position);
              startActivity(i);*/
     }
	public void addItems(/*View v*/) {
		for(Integer i = 0; i <  (tableauUqacmons.size()); i++) {
			listItems.add(tableauUqacmons.get(i));
		}
		adapter.notifyDataSetChanged();
	}
	
	private void openRadar() {
		Intent i = new Intent(this, RadarActivity.class);
        startActivity(i);
	}
}
