package edu.uqac.multimedia.uqacmons;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class RadarActivity extends Activity {

	private ImageButton uqacpedia;
	private ImageView blueBip;
	private ImageView redBip;
	private Button flash;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radar);
		//GUI
		uqacpedia = (ImageButton)findViewById(R.id.b_uqacpedia);
		blueBip = (ImageView)findViewById(R.id.blueBip);
		redBip = (ImageView)findViewById(R.id.redBip);
		flash = (Button)findViewById(R.id.b_flash);
		
		//Set variables
		redBip.setAlpha(0F);
		
		uqacpedia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vue) {
				Intent i = new Intent(RadarActivity.this,UqacmonPedia.class);
				startActivity(i);
			}
		});
		flash.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vue) {
				Flash();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.radar, menu);
		return true;
	}
	
	public void Flash() {
		redBip.setAlpha(1F);
		AlphaAnimation alpha = new AlphaAnimation(1F, 0.0F); // change values as you want
		alpha.setDuration(1000); // Make animation instant
		alpha.setFillAfter(true); // Tell it to persist after the animation ends
		redBip.startAnimation(alpha);// And then on your imageview
	}

}
