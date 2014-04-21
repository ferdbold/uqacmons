package edu.uqac.multimedia.uqacmons;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UqacmonPedia extends ListActivity {
	
	

	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<View> listItems=new ArrayList<View>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<View> adapter;
    private LayoutInflater getPopupLayout;
    
    //Variables
	private ImageButton Radar;
	ArrayList<Boolean> uqacmonIsCaptured;
	ArrayList<Integer> uqacmonImg;
	ArrayList<String> uqacmonName;
	ArrayList<String> uqacmonType;
	private ImageView uqacmonUI_image;
	private TextView uqacmonUI_name;
	private TextView uqacmonUI_type;
	private NewProfsDbAdapter mDbHelper;
	
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_uqacmon_pedia);
		//Get UI
		Radar = (ImageButton) findViewById(R.id.b_radar);
		//Set variables 
		uqacmonIsCaptured = new ArrayList<Boolean>();
		uqacmonImg = new ArrayList<Integer>();
		uqacmonName = new ArrayList<String>();
		uqacmonType = new ArrayList<String>();
		
		Radar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vue) {
				openRadar();
			}
		});
		
		adapter = new ArrayAdapter<View>(this, android.R.layout.simple_list_item_1,  listItems);
	    setListAdapter(adapter);
	    addItems();
	        
	    CreateUqacmonList();
	
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
		for(Integer i = 0; i <  (uqacmonName.size()); i++) {
			 
			getPopupLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View newUqacmon = getPopupLayout.inflate(R.layout.get_uqacmon_ui, null, false);
			
			uqacmonUI_image = (ImageView)newUqacmon.findViewById(R.id.uqacmonUIPicture);
			uqacmonUI_name = (TextView)newUqacmon.findViewById(R.id.uqacmonUIName);
			uqacmonUI_type =  (TextView)newUqacmon.findViewById(R.id.uqacmonUIType);
			
			if(uqacmonIsCaptured.get(i)) {
				uqacmonUI_image.setImageResource(GetUqacmonPicture(uqacmonImg.get(i)));
				uqacmonUI_name.setText(uqacmonName.get(i));
				uqacmonUI_type.setText(uqacmonType.get(i));	
			} else {
				uqacmonUI_image.setImageResource(GetUqacmonPicture(uqacmonImg.get(-1)));
				uqacmonUI_name.setText("?????????");
				uqacmonUI_type.setText("??????");	
			}
					
			listItems.add(newUqacmon); 
		}
		// TESTT /*
		getPopupLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newUqacmon = getPopupLayout.inflate(R.layout.get_uqacmon_ui, null, false);
		uqacmonUI_image = (ImageView)newUqacmon.findViewById(R.id.uqacmonUIPicture);
		uqacmonUI_name = (TextView)newUqacmon.findViewById(R.id.uqacmonUIName);
		uqacmonUI_type =  (TextView)newUqacmon.findViewById(R.id.uqacmonUIType); 
		//uqacmonUI_image.setImageResource(GetUqacmonPicture(uqacmonImg.get(-1)));
		uqacmonUI_name.setText("?????????");
		uqacmonUI_type.setText("??????");	
		listItems.add(newUqacmon);
		//*/
		adapter.notifyDataSetChanged();
	}
	
	private void openRadar() {
		Intent i = new Intent(UqacmonPedia.this, RadarActivity.class);
        startActivity(i);
	}
	
	public int GetUqacmonPicture(int id) {
		switch (id)
		{    
			case -1:
				return(R.drawable.uqacmon_mistery); //on envoie -1 quand l'uqacmon n'est pas captur� encore
			case 0:
				return(R.drawable.uqacmon_djamal);
		    case 1:
		        return(R.drawable.uqacmon_verreault);
		    case 2:
		    	return(R.drawable.ic_launcher);
		    case 3:
		    	return(R.drawable.ic_launcher);
		    default:
		    	return(R.drawable.ic_launcher);
		}
	
	}

	
	public void CreateUqacmonList(){ // A FAIRE : Cr�e la liste a afficher dans l'uqacmonpedia
		mDbHelper = new NewProfsDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		Cursor profsdata = mDbHelper.getProfsData();
		profsdata.moveToFirst();
		while(!profsdata.isAfterLast()){
			uqacmonName.add(profsdata.getString(profsdata.getColumnIndexOrThrow("name")));
			uqacmonType.add(profsdata.getString(profsdata.getColumnIndexOrThrow("bio")));
			uqacmonImg.add(profsdata.getInt(profsdata.getColumnIndexOrThrow("image")));
			if(profsdata.getInt(profsdata.getColumnIndexOrThrow("captured"))==1){
				uqacmonIsCaptured.add(true);
			}
			else{
				uqacmonIsCaptured.add(false);
			}
			profsdata.moveToNext();
		}
		profsdata.close();
		mDbHelper.close();
		
		
		
	}
	
	
	
}
