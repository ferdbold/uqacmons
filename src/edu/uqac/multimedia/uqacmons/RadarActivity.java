package edu.uqac.multimedia.uqacmons;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RadarActivity extends Activity {

	private ImageButton uqacpedia;
	private ImageView redBip;
	private ImageView redCircle;
	private Button flash;
	private Button getUqacmon;
	private Context ctx;
	private NewProfsDbAdapter mDbHelper;
	
	//Utilisé pour tester la vitesse du flash (trop de lag dans l'émulateur)
	private TextView testview;
	private Button upDistance;
	private Button downDistance;
	
	private LayoutInflater getPopupLayout;
	private View getPopupLayoutView;
	private ImageView uqacmonUI_image;
	private TextView uqacmonUI_name;
	private TextView uqacmonUI_type;
	
	// Les distances sont pour le moment arbitraire, A ajuster une fois la géolocalisation ajoutée
	public Integer distanceToCloser; //Distance entre l'utilisateur et le plus proche Uqacmon non capturé.
	private Float distanceToCapture = 5F; // Si la distance est inférieure a ce chiffre, on capture l'UQACMON
	private Float distanceToShow = 100F; // Si la distance est supérieure a ce chiffre, on ne voit rien
	private Float slowestFlashSpeed = 10000F; //Time in milliseconds
	private Float fastestFlashSpeed = 1000F; //Time in milliseconds
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radar);
		//GUI
		uqacpedia = (ImageButton)findViewById(R.id.b_uqacpedia);
		redBip = (ImageView)findViewById(R.id.redBip);
		redCircle = (ImageView)findViewById(R.id.redCircle);
		flash = (Button)findViewById(R.id.b_flash);
		getUqacmon = (Button)findViewById(R.id.b_get);
		
		
		testview = (TextView)findViewById(R.id.TESTVIEW);
		upDistance = (Button)findViewById(R.id.b_up);
		downDistance = (Button)findViewById(R.id.b_down);
		
		ctx = this.getApplicationContext();
		//Set variables
		redBip.setAlpha(0F);
		redCircle.setAlpha(0F);
		
		//Temporaire Tant que la Geolocalisation n'est pas implémentée
		distanceToCloser = 50;
		KeepFlashing(); //Initialize the automatic Flashing based on distance
		upDistance.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View vue) { distanceToCloser += 5;}
		});
		downDistance.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View vue) { distanceToCloser -= 5;}
		});
		
		
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
				int id=2; // CONSTANTE D'ID POUR PROFESSEUR POUR DES FINS DE TESTS 1=djamal, 2=Verreault, 3=Eric, 4=Bob,5=Pierre
				mDbHelper = new NewProfsDbAdapter(ctx);
				mDbHelper.createDatabase();
				mDbHelper.open();
				Cursor profsdata = mDbHelper.getProfsData();
				profsdata.moveToPosition(id); // On bouge le cursor de la db sur une position, par convention, 1=djamal, 2=Verreault, 3=Eric, 4=Bob,5=Pierre
				int p_id =  profsdata.getInt(profsdata.getColumnIndexOrThrow("image"));
				String p_name = profsdata.getString(profsdata.getColumnIndexOrThrow("name"));
				String p_type = profsdata.getString(profsdata.getColumnIndexOrThrow("bio"));
				mDbHelper.close();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_uqacmonpedia:
	            openUqacmonpedia();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
	    getPopupLayoutView = getPopupLayout.inflate(R.layout.get_uqacmon_ui, null, false);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		uqacmonUI_image = (ImageView)getPopupLayoutView.findViewById(R.id.uqacmonUIPicture);
		uqacmonUI_name = (TextView)getPopupLayoutView.findViewById(R.id.uqacmonUIName);
		uqacmonUI_type =  (TextView)getPopupLayoutView.findViewById(R.id.uqacmonUIType);
		
		uqacmonUI_image.setImageResource(GetUqacmonPicture(id));
		uqacmonUI_name.setText(name);
		uqacmonUI_type.setText(type);
	    

		
	    builder.setView(getPopupLayoutView);
	    builder.setTitle("YOU CAPTURED A WILD UQACMON !");
	    builder.setPositiveButton("VIEW", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	int captured=1;
	        	mDbHelper = new NewProfsDbAdapter(ctx);
	            mDbHelper.open();
	            mDbHelper.updateProf(which, captured);
	            mDbHelper.close();
	            openUqacmonpedia();
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

	private void openUqacmonpedia() {
		Intent i = new Intent(this, UqacmonPedia.class);
        startActivity(i);
	}
	
	private void KeepFlashing() {
		handler.postDelayed(runnable, 5000);
	}
	
	//Timer pour le Flash Automatique
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		   @Override
		   public void run() { 
		      //Calcule du temps avant le prochain Flash();
			  Float timeNextFlash;
			  timeNextFlash = (10F)*fastestFlashSpeed/(((distanceToShow - distanceToCloser)/(distanceToShow-distanceToCapture))*(slowestFlashSpeed / fastestFlashSpeed));
		      if(timeNextFlash > slowestFlashSpeed) {
		    	  timeNextFlash = slowestFlashSpeed;
		    	  distanceToCloser = distanceToShow.intValue();
		      }
		      if(timeNextFlash < fastestFlashSpeed) {
		    	  timeNextFlash = fastestFlashSpeed; 
		      }
		      testview.setText(timeNextFlash.toString());
			  Flash(); // FLASH !
		      //On Rappele L'Handler avec le Delai voulu
		      handler.postDelayed(this, timeNextFlash.intValue());
		   }
		};
	
}
