package edu.uqac.multimedia.uqacmons;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class RadarActivity extends Activity {

	private ImageButton uqacpedia;
	private ImageView blueBip;
	private ImageView redBip;
	private ImageView redCircle;
	private Button flash;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radar);
		//GUI
		uqacpedia = (ImageButton)findViewById(R.id.b_uqacpedia);
		blueBip = (ImageView)findViewById(R.id.blueBip);
		redBip = (ImageView)findViewById(R.id.redBip);
		redCircle = (ImageView)findViewById(R.id.redCircle);
		flash = (Button)findViewById(R.id.b_flash);
		
		//Set variables
		redBip.setAlpha(0F);
		redCircle.setAlpha(0F);
		
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
		//Red Bip Alpha
		redBip.setAlpha(1F);
		AlphaAnimation alpha = new AlphaAnimation(1F, 0.0F); // Alpha variation
		alpha.setDuration(1000); // time in milliseconds
		alpha.setFillAfter(true); // Stays Like it is at the end of the animation
		redBip.startAnimation(alpha);// Launch animation
		//Red circe Animation
		redCircle.setAlpha(1F); // Alpha animation first
		ScaleAnimation scale = new ScaleAnimation(1F, 2F, // Start and end values for the X axis scaling
	            1F, 2F, // Start and end values for the Y axis scaling
	            Animation.RELATIVE_TO_SELF, 0.5F, // Pivot point of X scaling
	            Animation.RELATIVE_TO_SELF, 0.5F); // Pivot point of Y scaling
		scale.setDuration(2000);
		scale.setFillAfter(true);
		// On utilise un set d'animation pour les faire en meme temps
		AnimationSet animSet = new AnimationSet(true);
	    animSet.setFillAfter(true);
	    animSet.setDuration(1000);
	    animSet.setInterpolator(new LinearInterpolator());
	    animSet.addAnimation(alpha);
	    animSet.addAnimation(scale);
	    redCircle.startAnimation(animSet); //Launch Animations
	}

}
