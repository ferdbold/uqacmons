package edu.uqac.multimedia.uqacmons;

import java.util.Iterator;

import android.location.GpsStatus;
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

public class RadarActivity extends Activity {// implements LocationListener {

	private ImageButton uqacpedia;
	private ImageView redBip;
	private ImageView redCircle;
	private Button flash;
	private Button getUqacmon;
	private Button releaseUqacmons;
	private Context ctx;
	private NewProfsDbAdapter mDbHelper;
	private LocationManager lm;
	private LocationListener ll;
	
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
	public double distanceToCloser = Double.POSITIVE_INFINITY;  // Distance entre l'utilisateur et le plus proche Uqacmon non captur�.
	private double distanceToCapture = 5; 						// Si la distance est inf�rieure a ce chiffre, on capture l'UQACMON
	private double distanceToShow = 100F; 						// Si la distance est sup�rieure a ce chiffre, on ne voit rien
	private double slowestFlashSpeed = 10000; 					// plus petite vitesse possible de flash (milliseconds)
	private double fastestFlashSpeed = 500; 					// plus grande vitesse possible de flash (milliseconds)
	private Integer idprof = -1;
	
	//Tab long/lat
	private ProfPosition[] positionProfs;
	
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
		
		positionProfs = getPositionProfs();
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
		  Double timeNextFlash;
		  timeNextFlash = (10F)*fastestFlashSpeed/(((distanceToShow - distanceToCloser)/(distanceToShow-distanceToCapture))*(slowestFlashSpeed / fastestFlashSpeed));
	      if(timeNextFlash > slowestFlashSpeed) {
	    	  timeNextFlash = slowestFlashSpeed;
	    	  distanceToCloser = distanceToShow;
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
		
		// Acquire a reference    dto the system Location Manager
		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		updateLocation(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		
		// Define a listener that responds to location updates
		ll = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	updateLocation(location);
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    	updateStatus(provider, status);
		    }

		    public void onProviderEnabled(String provider) {
		    	updateProviderEnabled(provider, true);
		    }

		    public void onProviderDisabled(String provider) {
		    	updateProviderEnabled(provider, false);
		    }
		  };

		// Register the listener with the Location Manager to receive location updates
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ll);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		lm.removeUpdates(ll);
	}
	
	public void updateLocation(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		Float accuracy = location.getAccuracy();
		flash.setText(accuracy.toString()); //Test pour voir l'accuracy a chaque update.
		/*String msg = String.format(
				getResources().getString(R.string.geoloc_update), latitude,
				longitude, accuracy);
		
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();*/
		
		refreshNearestUqacmon(location);
		
		String msg = String.format(
				getResources().getString(R.string.nearest_uqacmon), this.distanceToCloser, this.idprof);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	public void updateStatus(String provider, int status) {
		Toast.makeText(this, String.format(getResources().getString(R.string.geoloc_status_update), provider, status), Toast.LENGTH_LONG).show();
	}
	
	public void updateProviderEnabled(String provider, Boolean isEnabled) {
		Toast.makeText(this, String.format(getResources().getString(R.string.geoloc_enabled_update), provider, isEnabled.toString()), Toast.LENGTH_LONG).show();
	}
	
	private void refreshNearestUqacmon(Location location) {		
		double distanceToBeat = Double.POSITIVE_INFINITY;
		int nearestUqacmon = -1;
		
		for(int i = 0; i < positionProfs.length; i++) {
			double deltaX = Math.abs(location.getLatitude() - positionProfs[i].latitude);
			double deltaY = Math.abs(location.getLongitude() - positionProfs[i].longitude);
			
			double dist = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			
			if (distanceToBeat > dist) {
				nearestUqacmon = i;
				distanceToBeat = dist;
			}
		}
		
		this.distanceToCloser = (int)distanceToBeat;
		this.idprof = nearestUqacmon;
	}
	
	private ProfPosition[] getPositionProfs() {
		int i=0;
		ProfPosition[] positions = new ProfPosition[9];
		mDbHelper = new NewProfsDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		Cursor profsdata = mDbHelper.getProfsData();
		profsdata.moveToFirst();
		while(!profsdata.isAfterLast()){
			positions[i].id=profsdata.getInt(profsdata.getColumnIndex("id"));
			positions[i].latitude=profsdata.getDouble(profsdata.getColumnIndex("latitude"));
			positions[i].longitude=profsdata.getDouble(profsdata.getColumnIndex("longitude"));
			i++;
			profsdata.moveToNext();
		}
		
		return(positions);
	}
	
	class ProfPosition {
		public int id;
		public double latitude;
		public double longitude;
		
		public ProfPosition(int id, double latitude, double longitude) {
			this.id = id;
			this.latitude = latitude;
			this.longitude = longitude;
		}
	};
}
