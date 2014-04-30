package edu.uqac.multimedia.uqacmons;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RadarActivity extends Activity implements LocationListener {

	private ImageButton uqacpedia;
	private ImageView redBip;
	private ImageView redCircle;
	private Button flash;
	private Button getUqacmon;
	private Button releaseUqacmons;
	private Context ctx;
	private NewProfsDbAdapter mDbHelper;
	private LocationManager lm;
	
	//Utilis� pour tester la vitesse du flash (trop de lag dans l'�mulateur)
	private TextView testview;
	private Button upDistance;
	private Button downDistance;
	
	private LayoutInflater getPopupLayout;
	private View getPopupLayoutView;
	private ImageView uqacmonUI_image;
	private TextView uqacmonUI_name;
	private TextView uqacmonUI_type;
	
	// Les distances sont pour le moment arbitraire, A ajuster une fois la g�olocalisation ajout�e
	public Integer distanceToCloser; 			// Distance entre l'utilisateur et le plus proche Uqacmon non captur�.
	private Float distanceToCapture = 5F; 		// Si la distance est inf�rieure a ce chiffre, on capture l'UQACMON
	private Float distanceToShow = 100F; 		// Si la distance est sup�rieure a ce chiffre, on ne voit rien
	private Float slowestFlashSpeed = 10000F; 	// plus petite vitesse possible de flash (milliseconds)
	private Float fastestFlashSpeed = 500F; 	// plus grande vitesse possible de flash (milliseconds)
	private Integer idprof=-1; 					// -1 est la valeur par d�faut !
	
	
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
		releaseUqacmons = (Button)findViewById(R.id.b_release);
		
		testview = (TextView)findViewById(R.id.TESTVIEW);
		upDistance = (Button)findViewById(R.id.b_up);
		downDistance = (Button)findViewById(R.id.b_down);
		
		ctx = this.getApplicationContext();
		//Set variables
		redBip.setAlpha(0F);
		redCircle.setAlpha(0F);
		
		//Temporaire Tant que la Geolocalisation n'est pas impl�ment�e
		distanceToCloser = 50; // <-- A ENLEVER LORSQUE SUR UNE VRAI MACHINE, sinon crash sur VM
		KeepFlashing(); //Initialize the automatic Flashing based on distance
		//Bouton utilis� a fin de tests seulement : 
		upDistance.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View vue) { distanceToCloser += 5;}
		});
		downDistance.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View vue) { distanceToCloser -= 5;}
		});
		
		
		uqacpedia.setOnClickListener(new View.OnClickListener() { //bouton pour changer d'�cran
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
				mDbHelper = new NewProfsDbAdapter(ctx);
				mDbHelper.createDatabase();
				mDbHelper.open();
				Cursor profsdata = mDbHelper.getProfsData();
				if(idprof>-1){
					profsdata.moveToPosition(idprof); // On bouge le cursor de la db sur une position, par convention, 1=djamal, 2=Verreault, 3=Eric, 4=Bob,5=Pierre
					if(profsdata.getInt(profsdata.getColumnIndex("captured"))!=1){
						int captured=1;
						mDbHelper.updateProf(idprof, captured);
						int p_id =  profsdata.getInt(profsdata.getColumnIndexOrThrow("image"));
						String p_name = profsdata.getString(profsdata.getColumnIndexOrThrow("name"));
						String p_type = profsdata.getString(profsdata.getColumnIndexOrThrow("bio"));
						profsdata.close();
						mDbHelper.close();

						GetUqacmon(p_id,p_name,p_type);
					}
					else{
						Toast.makeText(ctx, "L'uqacmon a d�j� �t� captur�", Toast.LENGTH_LONG).show();
					}
				}
				else{
					Toast.makeText(ctx, "Aucun uqacmon dans le secteur", Toast.LENGTH_LONG ).show();
				}
			}
		});
		releaseUqacmons.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDbHelper = new NewProfsDbAdapter(ctx);
				mDbHelper.createDatabase();
				mDbHelper.open();
				mDbHelper.releaseAllProf();
				mDbHelper.close();
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
		//int captured=1;
		
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
			case -1:
				return(R.drawable.uqacmon_mistery); //on envoie -1 quand l'uqacmon n'est pas captur� encore
			case 0:
				return(R.drawable.uqacmon_djamal);
			case 1:
				return(R.drawable.uqacmon_verreault);
		    case 2:
		    	return(R.drawable.uqacmon_dallaire);
		    case 3:
		    	return(R.drawable.uqacmon_bob);
		    case 4:
		    	return(R.drawable.uqacmon_pierre);
		    case 5:
		    	return(R.drawable.uqacmon_alexandre);
		    case 6:
		    	return(R.drawable.uqacmon_tarik);
		    case 7:
		    	return(R.drawable.uqacmon_bruno);
		    case 8:
		    	return(R.drawable.uqacmon_jean);
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

	/**
	 * G�OLOCALISATION
	 */
	@Override
	protected void onResume() {
		super.onResume();
		lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			//Ici pourquoi on envoie "this" en 4iem parametre au lieu d'un LocationListener() ? 
			//Est-ce parce qu'on implemente LocationListener dans la classe ?
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this); 
		//Pourquoi ce code est ici deux fois ? Erreur ?
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this); //Chang� "NETWORK_PROVIDER" pour "GPS_PROVIDER" sinon �a crash dans mon emulateur.
	}

	@Override
	protected void onPause() {
		super.onPause();
		lm.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		Float accuracy = location.getAccuracy();
		flash.setText(accuracy.toString()); //Test pour voir l'accuracy a chaque update.
		String msg = String.format(
				getResources().getString(R.string.geoloc_update), latitude,
				longitude, accuracy);
		
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		idprof=rechercheProfetDistance(latitude, longitude, accuracy);
	}
	// � inclure D�pendant de l'accuracy du gps : la variable "DistanceToCapture" indique la distance minimum entre l'uqacmon et la personne
	// Pour le capturer. Par contre, si la valeur d'accuracy est trop grande sa vaut peut etre pas la peine.
	private int rechercheProfetDistance(double latitude,double longitude,float accuracy){
		float RayonTerre=6371.0F;
		int distanceAvecProf;
		Integer id;
		mDbHelper = new NewProfsDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		Cursor profsdata = mDbHelper.getProfsData();
		profsdata.moveToFirst();
		
		do {
			distanceToCloser= (int) (RayonTerre*Math.acos(Math.sin(latitude)*Math.sin(profsdata.getColumnIndex("latitude")+Math.cos(latitude)*Math.cos(profsdata.getColumnIndex("latitude")*Math.cos(longitude-profsdata.getColumnIndex("longitude"))))));
			if(profsdata.getInt(profsdata.getColumnIndex("captured"))!=0){
				profsdata.moveToNext();
			}
		}
		while(profsdata.getInt(profsdata.getColumnIndex("captured"))!=0 && profsdata!=null);
		
		while(profsdata!=null){
			distanceAvecProf=(int) (RayonTerre*Math.acos(Math.sin(latitude)*Math.sin(profsdata.getColumnIndex("latitude")+Math.cos(latitude)*Math.cos(profsdata.getColumnIndex("latitude")*Math.cos(longitude-profsdata.getColumnIndex("longitude")))))+accuracy);
			if((distanceAvecProf<distanceToCloser) && (profsdata.getInt(profsdata.getColumnIndex("captured"))!=1)){
				distanceToCloser= distanceAvecProf;
			}/*
			if ((latitude < profsdata.getDouble(profsdata.getColumnIndex("latitude")))&&(latitude > profsdata.getDouble(profsdata.getColumnIndex("latitude"))) ){
				if ((longitude <profsdata.getDouble(profsdata.getColumnIndex("longitude")))&&(longitude > profsdata.getDouble(profsdata.getColumnIndex("longitude")))){
					id=profsdata.getPosition();
					return(id);
				}
			}*/
			if(distanceToCloser<=(distanceToCapture)+accuracy){
				id=profsdata.getPosition();
				//String msg = ("Captured Uqacmon : " + id.toString());
				//Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				return(id);
			}
			profsdata.moveToNext();
		}
		return -1;
	}

	@Override
	public void onProviderDisabled(String provider) {
		String msg = String.format(getResources().getString(R.string.gps_desactive), provider);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		String msg = String.format(getResources().getString(R.string.gps_active), provider);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		String newStatus = "";
		switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				newStatus = "OUT_OF_SERVICE";
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				newStatus = "TEMPORARILY_UNAVAILABLE";
				break;
			case LocationProvider.AVAILABLE:
				newStatus = "AVAILABLE";
				break;
		}
		String msg = String.format(getResources().getString(R.string.gps_desactive), provider, newStatus);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
}
