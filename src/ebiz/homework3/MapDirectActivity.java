package ebiz.homework3;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapDirectActivity extends FragmentActivity implements 	
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
	        
	        myMarker = myMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
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
	        Toast.makeText(getApplicationContext(), "Calculating direction...", Toast.LENGTH_LONG).show();						
			
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
	        addressText = getAddressText(location.getLatitude(), location.getLongitude());	        
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
			    //Toast.makeText(getApplicationContext(), ownername + " (" + location.getLatitude() + "," + location.getLongitude() + ")\n" + addressText, Toast.LENGTH_LONG).show();
	        }

		    
	        Log.v("info", "onlocatiochanged end");
	        
	    	timer ++;
	    	if(timer == 2){
	    		myLocationClient.removeLocationUpdates(this);
	    		timer = 0;
	    		//Toast.makeText(getApplicationContext(), "Your car has been marked in Red", Toast.LENGTH_SHORT).show();
		    	
	    		if(myMap!=null){
	    		
	    			// Enable MyLocation Button in the Map
	    			myMap.setMyLocationEnabled(true);		
	    			
	    			// Get the message from the intent
	    		    Intent intent = getIntent();
	    			LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());//markerPoints.get(0);
	    			LatLng dest = (LatLng) intent.getExtras().get("toPosition");//markerPoints.get(1);
	    			
	    			// add mark to destination
	    			String destAddText = getAddressText(dest.latitude, dest.longitude);
	    			myMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
	    	        .position(dest).title(destAddText).alpha(1.0f).visible(true));
	    			//Toast.makeText(getApplicationContext(), "The pessenger has been marked in Green", Toast.LENGTH_SHORT).show();
	    			
	    			// Getting URL to the Google Directions API
	    			String url = getDirectionsUrl(origin, dest);				
	    			
	    			DownloadTask downloadTask = new DownloadTask();
	    			
	    			// Start downloading json data from Google Directions API
	    			downloadTask.execute(url);
	    			
	    						    	
	    		}		
	        }
	    }
	    private String getAddressText(double lat, double lng){	    	
	        List<Address> addresses = null;
	        try {
				addresses = myGeocoder.getFromLocation(lat, lng, 1);
			} catch (IOException e) {
				Log.v("info", e.getMessage());
				myLocationClient.removeLocationUpdates(this);
				popupDialog("Google address look up unavailable", "Please check your network availability and try again later");         				
				return null;
			}
	        Address address = addresses.get(0);
	        String addressText = String.format(
	                "%s, %s, %s",
	                // If there's a street address, add it
	                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                // Locality is usually a city
	                address.getLocality(),
	                // The country of the address
	                address.getCountryName());
	        return addressText;
	    }
	    private String getDirectionsUrl(LatLng origin,LatLng dest){
			
			// Origin of route
			String str_origin = "origin="+origin.latitude+","+origin.longitude;
			
			// Destination of route
			String str_dest = "destination="+dest.latitude+","+dest.longitude;		
			
						
			// Sensor enabled
			String sensor = "sensor=false";			
						
			// Building the parameters to the web service
			String parameters = str_origin+"&"+str_dest+"&"+sensor;
						
			// Output format
			String output = "json";
			
			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
			
			
			return url;
		}
		
		/** A method to download json data from url */
	    private String downloadUrl(String strUrl) throws IOException{
	        String data = "";
	        InputStream iStream = null;
	        HttpURLConnection urlConnection = null;
	        try{
	                URL url = new URL(strUrl);

	                // Creating an http connection to communicate with url 
	                urlConnection = (HttpURLConnection) url.openConnection();

	                // Connecting to url 
	                urlConnection.connect();

	                // Reading data from url 
	                iStream = urlConnection.getInputStream();

	                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

	                StringBuffer sb  = new StringBuffer();

	                String line = "";
	                while( ( line = br.readLine())  != null){
	                        sb.append(line);
	                }
	                
	                data = sb.toString();

	                br.close();

	        }catch(Exception e){
	                Log.d("Exception while downloading url", e.toString());
	        }finally{
	                iStream.close();
	                urlConnection.disconnect();
	        }
	        return data;
	     }

		
		
		// Fetches data from url passed
		private class DownloadTask extends AsyncTask<String, Void, String>{			
					
			// Downloading data in non-ui thread
			@Override
			protected String doInBackground(String... url) {
				// For storing data from web service
				String data = "";
						
				try{
					// Fetching the data from web service
					data = downloadUrl(url[0]);
				}catch(Exception e){
					Log.d("Background Task",e.toString());
				}
				return data;		
			}
			
			// Executes in UI thread, after the execution of
			// doInBackground()
			@Override
			protected void onPostExecute(String result) {			
				super.onPostExecute(result);			
				
				ParserTask parserTask = new ParserTask();
				
				// Invokes the thread for parsing the JSON data
				parserTask.execute(result);
					
			}		
		}
		
		/** A class to parse the Google Places in JSON format */
	    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
	    	
	    	// Parsing the data in non-ui thread    	
			@Override
			protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
				
		    	
				JSONObject jObject;	
				List<List<HashMap<String, String>>> routes = null;			           
	            
	            try{
	            	jObject = new JSONObject(jsonData[0]);
	            	DirectionsJSONParser parser = new DirectionsJSONParser();
	            	
	            	// Starts parsing data
	            	routes = parser.parse(jObject);    
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	            return routes;
			}
			
			// Executes in UI thread, after the parsing process
			@Override
			protected void onPostExecute(List<List<HashMap<String, String>>> result) {
				ArrayList<LatLng> points = null;
				PolylineOptions lineOptions = null;
				MarkerOptions markerOptions = new MarkerOptions();
				
				// Traversing through all the routes
				for(int i=0;i<result.size();i++){
					points = new ArrayList<LatLng>();
					lineOptions = new PolylineOptions();
					
					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);
					
					// Fetching all the points in i-th route
					for(int j=0;j<path.size();j++){
						HashMap<String,String> point = path.get(j);					
						
						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);	
						
						points.add(position);						
					}
					
					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(8);
					lineOptions.color(Color.MAGENTA);	
					
				}
				
				// Drawing polyline in the Google Map for the i-th route
				myMap.addPolyline(lineOptions);							
			}			
	    }   
	    
	    
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}	
		
		
		
	   private void gotoMyLocation(double lat, double lng) {
	       changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
	    		   													.target(new LatLng(lat, lng)).zoom(14.5f).bearing(0).tilt(60).build()
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
