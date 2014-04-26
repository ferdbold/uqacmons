package edu.uqac.multimedia.uqacmons;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
//import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
//import android.widget.ImageView;
import android.widget.ListView;
//import android.widget.TextView;

public class UqacmonPedia extends Activity {
	
	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<View> listItems=new ArrayList<View>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<View> adapter;
    //private LayoutInflater getPopupLayout;
    
    //Variables
	private ImageButton Radar;
	ArrayList<Boolean> uqacmonIsCaptured;
	ArrayList<Integer> uqacmonImg;
	ArrayList<String> uqacmonName;
	ArrayList<String> uqacmonType;
	//private ImageView uqacmonUI_image;
	//private TextView uqacmonUI_name;
	//private TextView uqacmonUI_type;
	private NewProfsDbAdapter mDbHelper;
	
	//TEST C ADAPTER
	ListView testList;
	private UqacmonAdapter testAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		CreateUqacmonList();
		
		//Test C ADAPTEr
        testList=(ListView)findViewById(R.id.list);
        // Getting adapter by passing xml data ArrayList
        testAdapter = new UqacmonAdapter(this, uqacmonName, uqacmonType,uqacmonIsCaptured, uqacmonImg);
        testList.setAdapter(testAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uqacmon_pedia, menu);
		return true;
	}

	/*public void addItems(View v) {
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
		//
		adapter.notifyDataSetChanged();
	}*/
	
	private void openRadar() {
		Intent i = new Intent(UqacmonPedia.this, RadarActivity.class);
        startActivity(i);
	}
	
	public void CreateUqacmonList(){ // A FAIRE : Crée la liste a afficher dans l'uqacmonpedia
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
