package ebiz.homework3;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ebiz.homework3.bean.Passenger;
import ebiz.homework3.twitteroauth.TweetActivity;

public class MapActivity extends FragmentActivity implements 	
					GooglePlayServicesClient.ConnectionCallbacks,
					GooglePlayServicesClient.OnConnectionFailedListener,
        			com.google.android.gms.location.LocationListener{
	    private final static int
	    CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	    private int timer = 0;
	    private boolean locationSent = false;
		public static final String LOCATION = "LOCATION"; 
	    private GoogleMap myMap;            // map reference
	    private Marker myMarker;
	    private LocationClient myLocationClient;
		private Geocoder myGeocoder;
		private String addressText;
	    private static final LocationRequest REQUEST = LocationRequest.create()
	            .setInterval(5000)         // 5 seconds
	            .setFastestInterval(16)    // 16ms = 60fps
	            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    
	    
	// FragmentActivity - Start
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_map);
	        
	    	// check network connectivity. if not, pop up a dialog
	    	if(!isNetworkAvailable()){
	    		popupDialog("Network unavailable", "Please check and try again later");
	    	}
	
	    }
	    private boolean isNetworkAvailable() {
	        ConnectivityManager connectivityManager 
	              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	    }
	
	    private void initializeMap() {
	        if(myMap == null){
	            myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
	                .getMap();
	        }
	        if(myMap != null){
	            myMap.setMyLocationEnabled(true);
	        }
	        
	        myMarker = myMap.addMarker(new MarkerOptions()
	        .position(new LatLng(10, 10))
	        .title("Rendering").visible(false));
	
	    }
	    
	    @Override
	    protected  void onResume(){	        
	    	locationSent = false;
	        super.onResume();
	        initializeMap();
	        activateLocationClient();
	        myLocationClient.connect();

	    }
	
	    private void activateLocationClient() {
	        if(myLocationClient == null){
	            myLocationClient = new LocationClient(getApplicationContext(),
	                    this,       // Connection Callbacks
	                    this);      // OnConnectionFailedListener
	        }
	    }
	    
	
	    @Override
	    public void onPause(){
	        super.onPause();
	        if(myLocationClient != null){
	            myLocationClient.disconnect();
	        }
	    }
	// FragmentActivity - End
	    
	    
	    
	// ConnectionCallbacks - Start
	    @Override
	    public void onConnected(Bundle bundle) {
	        myLocationClient.requestLocationUpdates(
	                REQUEST,
	                this); // LocationListener
	        Log.v("info", "connected");
	        
	    }
	
	    @Override
	    public void onDisconnected() {
	
	    }
	// ConnectionCallbacks - End    
	    
	    
	    
	// OnConnectionFailedListener - Start
	    /**
	     * if location client fails to connect, try resolution for help, otherwise pop up an error message dialog.
	     * this method should be used with onActivityResult.
	     */
	    @Override
	    public void onConnectionFailed(ConnectionResult connectionResult) {
	    	// basically due to LocationClient fails to connect to Google Play Location Services
	        if (connectionResult.hasResolution()) {
	            try {
	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
	                /*
	                 * Thrown if Google Play services canceled the original PendingIntent
	                 */
	            } catch (IntentSender.SendIntentException e) {
	                // Log the error
	            	popupDialog("Google Play services canceled the original PendingIntent", e.getMessage());
	            }
	        } else {
	            /*
	             * If no resolution is available, display a dialog to the user with the error.
	             */
	            popupDialog("No resolution is available", "Error Code: " + connectionResult.getErrorCode());
	        }
	
	    }    
	// OnConnectionFailedListener - End
	    /**
	     * this is Activity protected method to override.
	     * if the user has tried the resolution, this callback will follow. reinitialize location client and do connect again.
	     * this method should be used with onConnectionFailed.
	     */
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // Decide what to do based on the original request code
	        switch (requestCode) {
	            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
	            /*
	             * If the result code is Activity.RESULT_OK, try
	             * to connect again
	             */
	                switch (resultCode) {
	                    case Activity.RESULT_OK :
	                    onResume();
	                    break;
	                }
	        }
	     }
	   
	
	    
	    
	// LocationListener - Start      
	    /**
	     * Callback from LocationClient every time our location is changed
	     */
	    @Override
	    public void onLocationChanged(Location location) {
	    	timer ++;
	    	Log.v("info", "onlocatiochanged start");

	    	// check if the location is null
	    	if (location == null) {
	    		myLocationClient.removeLocationUpdates(this);
	    		popupDialog("Google location service unavailable", "Please check your network availability and try again later"); 
	    		return;
			}
	    	
	    	gotoMyLocation(location.getLatitude(), location.getLongitude());
	        myMarker.setAlpha(1.0f);
	        myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
	        
	        myGeocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
	        List<Address> addresses = null;
	        try {
				addresses = myGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			} catch (IOException e) {
				Log.v("info", e.getStackTrace().toString());
				myLocationClient.removeLocationUpdates(this);
				popupDialog("Google address look up unavailable", "Please check your network availability and try again later");         				
				return;
			}
	        Address address = addresses.get(0);
	        addressText = String.format(
	                "%s, %s, %s",
	                // If there's a street address, add it
	                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                // Locality is usually a city
	                address.getLocality(),
	                // The country of the address
	                address.getCountryName());
	
	        
	        myMarker.setTitle(addressText);
	        myMarker.setVisible(true);
	        
	        // get the Extra in father intent and post all together with location to server
	        if(!locationSent){
	        	// reset locationSent
	        	locationSent = true;
	        	// retrieve owner info
		        Intent intent = getIntent();
			    String ownername = intent.getStringExtra(CarPostActivity.OWNERNAME);
			    String ownertime = intent.getStringExtra(CarPostActivity.OWNERTIME);
			    String ownermile = intent.getStringExtra(CarPostActivity.OWNERMILE);
			    String ownerplno = intent.getStringExtra(CarPostActivity.OWNERPLNO);
			    String ownerarea = intent.getStringExtra(CarPostActivity.OWNERAREA);
			    
			    //[todo]send location+above to server
			    Toast.makeText(getApplicationContext(), ownername + " (" + location.getLatitude() + "," + location.getLongitude() + ")\n" + addressText, Toast.LENGTH_LONG).show();
	        }		    
	        Log.v("info", "onlocatiochanged end");
	        
	    	if(timer == 2){
	    		myLocationClient.removeLocationUpdates(this);
	    		timer = 0;
	    		
	        	// retrieve owner info
		        Intent intent = getIntent();
			    String ownername = intent.getStringExtra(CarPostActivity.OWNERNAME);
			    String ownertime = intent.getStringExtra(CarPostActivity.OWNERTIME);
			    String ownermile = intent.getStringExtra(CarPostActivity.OWNERMILE);
			    String ownerplno = intent.getStringExtra(CarPostActivity.OWNERPLNO);
			    String ownerarea = intent.getStringExtra(CarPostActivity.OWNERAREA);
				DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
				Date date = new Date();
				String currentDT = dateFormat.format(date);	
			    /*String composedInfo = "Hi, This is " + ownername + " !" + " My car (Plate:" +ownerplno+ ") is available to pick you up within " + ownermile + " miles. " + "The service is available for " + ownertime + " minutes. " + 
			    "I am serving such areas: " + ownerarea + ". Please check the CarPool app to see if I could serve you. Thank You!";
			    */
			    String composedInfo = "Hey! My car (Plate:" +ownerplno+ ") is available to pick you up. Please check CarPool to see if I could serve you! @" + currentDT;
	
			    // prepare a dialog for twitter
	    		popupDialogForTwitter("Tweet Your Carpooling Post", "Would you like to post your service on Twitter?", composedInfo);
	    	}
	    }
		private void popupDialogForTwitter(String title, String message, final String tweetMessage){
			final SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
			   	// create a pop up window to let the user know
			   	AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize));
			   	// create textview for centre title
				TextView myMsg = new TextView(this);
				myMsg.setText(title);
				myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
				myMsg.setTextSize(18);
				myMsg.setTextColor(Color.WHITE);
				// set custom title
				builder.setCustomTitle(myMsg);
				// set message
				builder.setMessage(message);
				// set button
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int id) {
				    	// connect to twitter for authentication
				        Intent intent = new Intent(MapActivity.this, TweetActivity.class);
				        intent.putExtra("tweet", tweetMessage);
				        SharedPreferences.Editor editor = app_preferences.edit().putString("tweet", tweetMessage);
				        editor.commit();
				        startActivity(intent);
				    }
				});
				builder.setNegativeButton("No", null);
				// display the dialog
				builder.show();
		   
		   }
	   private void gotoMyLocation(double lat, double lng) {
	       changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
	    		   													.target(new LatLng(lat, lng)).zoom(15.5f).bearing(0).tilt(25).build()
	       ), new GoogleMap.CancelableCallback() {
	           @Override
	           public void onFinish() {
	               // after the Map is rendered
	           }
	
	           @Override
	           public void onCancel() {
	               // after the Map rendering is cancelled
	           }
	       });
	   }
	   private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
	       myMap.moveCamera(update);
	   }
	   private void popupDialog(String title, String message){
			// also create a pop up window to let the user know
	       	AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
	       	// creates textview for centre title
			TextView myMsg = new TextView(this);
			myMsg.setText(title);
			myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
			myMsg.setTextSize(18);
			myMsg.setTextColor(Color.RED);
			// set custom title
			builder.setCustomTitle(myMsg);
			// set message
			builder.setMessage(message);
			// set button
			builder.setPositiveButton("OK", null);
			// display the dialog
			builder.show();
	   
	   }
	// LocationListener - End   
	    
	
	   /**
	    * send data back to caller when the sub activity is finished
	    */
	   @Override
	   public void finish() {
		     // Prepare data intent 
		     Intent data = new Intent();
		     data.putExtra(LOCATION, addressText);
		     // Activity finished ok, return the data
		     setResult(RESULT_OK, data);
		     super.finish();
	   } 
	
	
	    
}
