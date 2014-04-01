package edu.uqac.multimedia.uqacmons;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class UqacmonPedia extends ListActivity {

	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    
    //Variables
	private Button Radar;
	List<String> tableauNotes;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_uqacmon_pedia);
		
		Radar = (Button) findViewById(R.id.b_radar);
		
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
		for(Integer i = 0; i < tableauNotes.size(); i++) {
			listItems.add(tableauNotes.get(i));
		}
		adapter.notifyDataSetChanged();
	}

}
