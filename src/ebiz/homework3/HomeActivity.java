package ebiz.homework3;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class HomeActivity extends Activity {

		private static final int LOCATION_REQUEST = 1;  // The request code: to identify which activity to go to
		private String address;
		private Twitter twitter;
		ProgressDialog pDialog;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_home);
		}
		
		// the method to execute after user click 'Locate Me'
		public void goToOwnerPage(View view) {
			Intent intent = new Intent(this, CarPostActivity.class);
			startActivity(intent);
		}
		
		// when the subactivity finishes, this method will be called
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    // Check which request we're responding to
		    if (requestCode == LOCATION_REQUEST) {
		        // Make sure the request was successful
		        if (resultCode == RESULT_OK) {
		            if (data.hasExtra(MapActivity.LOCATION)) {
		            	address = data.getExtras().getString(MapActivity.LOCATION);
		            	// debug
		            	/*TextView textView = (TextView)findViewById(R.id.received);
		            	textView.setText(address) ;*/
		            }
		        }
		    }
		}
		
		
		// the method to execute when clicking 'Passenger'
		public void goToPassengerPage(View view) {	
			Intent intent = new Intent(this, CarSearchActivity.class);
			startActivity(intent);
/*		
			// check address set or not
			if(address == null || address.isEmpty()){
				popupDialog("Location not retrieved", "Please click 'Locate Me' to retrieve your current location first");
				return;
			}
			
			// compose together
			String composed = address;
			
			// it's ready to tweet message 
			new updateTwitterStatus().execute(composed);
*/	
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
			builder.setNegativeButton("Cancel", null);
			// display the dialog
			builder.show();
	   
	   }
	   
	   
	   /**
	    * The nested AsyncTask class for tweet update
	    */
	   class updateTwitterStatus extends AsyncTask<String, String, String> {
		   private String message;
		    /**
		     * Before starting background thread Show Progress Dialog
		     * */
		    @Override
		    protected void onPreExecute() {
		        super.onPreExecute();
		        pDialog = new ProgressDialog(HomeActivity.this);
		        pDialog.setMessage("Updating to twitter...");
		        pDialog.setIndeterminate(false);
		        pDialog.setCancelable(false);
		        pDialog.show();
		    }

		    /**
		     * tweet message
		     * */
		    protected String doInBackground(String... args) {
		        Log.d("Tweet Text", "> " + args[0]);
		        message = args[0];
		        try {
		    		// set authentication once for all
		    		TwitterFactory factory = new TwitterFactory();
		    	    twitter = factory.getInstance();
		    	    twitter.setOAuthConsumer("VTnTwukDvOMDu64m5NEvcXQ83", "arBJmEtLDNQVFDRY89XPKkZFtz78a3aF9qlHO7czoLQLOxkOFx");
		    	    AccessToken accessToken = new AccessToken("1873716554-BTcTsV4F8NIUs4d7sM3tpcJLavNL6aHLhiRDmSq", "M5jNNtk4uAt1WMgiZ8BcAd5Mg2pyFQzfbwszFUuBbrrz7");		    
		    	    twitter.setOAuthAccessToken(accessToken);

		            // Update status to @leader304_test!
		            twitter4j.Status response = twitter.updateStatus("@leader304_test " + message);

		            Log.d("Status", "> " + response.getText());
		        } catch (TwitterException e) {
		            // Error in updating status
		            Log.d("Twitter Update Error", e.getMessage());
		            popupDialog("Tweet Failed", e.getMessage());
		        }
		        return null;
		    }

		    /**
		     * After completing background task Dismiss the progress dialog
		     * Always use runOnUiThread(new Runnable()) to send update in main ui thread
		     * **/
		    protected void onPostExecute(String file_url) {
		        // dismiss the dialog after getting all products
		        pDialog.dismiss();
		        // updating UI from Background Thread
		        runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
/*		                
 						Toast.makeText(getApplicationContext(),
		                        "Status tweeted successfully", Toast.LENGTH_SHORT)
		                        .show();
*/
		                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
		                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(HomeActivity.this, R.style.AlertDialogCustom));
		                // creates textview for centre title
		        		TextView myMsg = new TextView(HomeActivity.this);
		        		myMsg.setText("The following populated information has been tweeted successfully");
		        		myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		        		myMsg.setTextSize(18);
		        		myMsg.setTextColor(Color.rgb(160, 60, 212));
		        		// set custom title
		                builder.setCustomTitle(myMsg);
		                // set message
		                builder.setMessage(message);
		                // set button
		                builder.setPositiveButton("OK", null);
		                // display the dialog
		                builder.show();
		            }
		        });
		    }

		}

	   
	   
	   
}

