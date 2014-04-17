package edu.uqac.multimedia.uqacmons;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RadarActivity extends Activity {

	private ImageButton uqacpedia;
	private ImageView blueBip;
	private ImageView redBip;
	private ImageView redCircle;
	private Button flash;
	private Button getUqacmon;
	private EditText addInput;
	private Context ctx;
	
	private LayoutInflater getPopupLayout;
	private ImageView uqacmonUI_image;
	private TextView uqacmonUI_name;
	private TextView uqacmonUI_type;
	private ViewGroup Test;
	
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
		getUqacmon = (Button)findViewById(R.id.b_get);
		
		ctx = this.getApplicationContext();
		//Set variables
		redBip.setAlpha(0F);
		redCircle.setAlpha(0F);
		
		uqacpedia.setOnClickListener(new View.OnClickListener() { //bouton pour changer d'écran
			@Override
			public void onClick(View vue) {
				Intent i = new Intent(RadarActivity.this,UqacmonPedia.class);
				startActivity(i);
			}
		});
		flash.setOnClickListener(new View.OnClickListener() { // bouton faire flasher le bouton (TEST)
			@Override
			public void onClick(View vue) {
				Flash();
			}
		});
		getUqacmon.setOnClickListener(new View.OnClickListener() { // bouton pour obtenir un uqacmon (TEST)
			@Override
			public void onClick(View vue) {
				//TODO : GET ID of pokemon gotten from DB
				int p_id = 0;
				String p_name = "DjamalMon";
				String p_type = "Binaire";
				GetUqacmon(p_id,p_name,p_type);
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
		alpha.setDuration(2000); // time in milliseconds
		alpha.setFillAfter(true); // Stays Like it is at the end of the animation
		redBip.startAnimation(alpha);// Launch animation
		//Red circe Animation
		redCircle.setAlpha(1F); // Alpha animation first
		ScaleAnimation scale = new ScaleAnimation(1F, 4F, // Start and end values for the X axis scaling
	            1F, 4F, // Start and end values for the Y axis scaling
	            Animation.RELATIVE_TO_SELF, 0.5F, // Pivot point of X scaling
	            Animation.RELATIVE_TO_SELF, 0.5F); // Pivot point of Y scaling
		scale.setDuration(2000);
		scale.setFillAfter(true);
		// On utilise un set d'animation pour les faire en meme temps
		AnimationSet animSet = new AnimationSet(true);
	    animSet.setFillAfter(true);
	    animSet.setDuration(2000);
	    animSet.setInterpolator(new LinearInterpolator());
	    animSet.addAnimation(alpha);
	    animSet.addAnimation(scale);
	    redCircle.startAnimation(animSet); //Launch Animations
	}
	
	public void GetUqacmon(int id, String name, String type)  {
		
		getPopupLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		uqacmonUI_image = (ImageView)getPopupLayout.inflate(R.layout.get_uqacmon_ui, null, false).findViewById(R.id.uqacmonUIPicture);
		uqacmonUI_name = (TextView)getPopupLayout.inflate(R.layout.get_uqacmon_ui, null, false).findViewById(R.id.uqacmonUIName);
		uqacmonUI_type =  (TextView)getPopupLayout.inflate(R.layout.get_uqacmon_ui, null, false).findViewById(R.id.uqacmonUIType);
		
		uqacmonUI_image.setImageResource(GetUqacmonPicture(id));
		uqacmonUI_name.setText(name);
		uqacmonUI_type.setText(type);
	    

		
	    builder.setView(getPopupLayout.inflate(R.layout.get_uqacmon_ui, Test, true));
	    builder.setTitle("YOU CAPTURED A WILD UQACMON !");
	    builder.setPositiveButton("VIEW", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            
	        }
	    });
	    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        }
	    });
	    	
	    builder.create();
        builder.show();    
	}
	
	public int GetUqacmonPicture(int id) {
		switch (id)
		{    
			case 0:
				return(R.drawable.pokedex_bouton2);
		    case 1:
		        return(R.drawable.ic_launcher);
		    case 2:
		    	return(R.drawable.ic_launcher);
		    case 3:
		    	return(R.drawable.ic_launcher);
		    default:
		    	return(R.drawable.ic_launcher);
		}
	
	}

}
