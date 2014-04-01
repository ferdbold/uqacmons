package edu.uqac.multimedia.uqacmons;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RadarActivity extends Activity {

	private Button uqacpedia;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radar);
		
		uqacpedia = (Button)findViewById(R.id.b_uqacpedia);
		
		uqacpedia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vue) {
				Intent i = new Intent(RadarActivity.this,UqacmonPedia.class);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.radar, menu);
		return true;
	}

}
