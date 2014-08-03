package ebiz.homework3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ebiz.homework3.bean.Driver;
import ebiz.homework3.util.HttpClientConnection;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ClipData.Item;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;

public class CarPostActivity extends ListActivity {
	private MyService mBoundService;
	private HttpClientConnection client = new HttpClientConnection();
	private EditText ownerName;
	private EditText ownerTime;
	private EditText ownerMile;
	private EditText ownerPlateNo;
	private EditText ownerArea;
	public final static String OWNERNAME = "OWNERNAME";
	public final static String OWNERTIME = "OWNERTIME";
	public final static String OWNERMILE = "OWNERMILE";
	public final static String OWNERPLNO = "OWNERPLNO";
	public final static String OWNERAREA = "OWNERAREA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_post);
		ownerName = (EditText) findViewById(R.id.owner_name);
		ownerTime = (EditText) findViewById(R.id.owner_time);
		ownerMile = (EditText) findViewById(R.id.owner_mile);
		ownerPlateNo = (EditText) findViewById(R.id.owner_plateno);
		ownerArea = (EditText) findViewById(R.id.owner_area);
	}

	public void onViewPosts(View view) {

		String ownerNameVal = ownerName.getText().toString().trim();
		String ownerTimeVal = ownerTime.getText().toString().trim();
		String ownerMileVal = ownerMile.getText().toString().trim();
		String ownerPlateNoVal = ownerPlateNo.getText().toString().trim();
		String ownerAreaVal = ownerArea.getText().toString().trim();
		
		// check if any miss field
		if (ownerName == null) {
			Toast.makeText(getApplicationContext(),
					"name field required, please make sure provide it",
					Toast.LENGTH_LONG).show();
			return;
		}

		// go to ViewRequestsActivity with bringing all the fields
		Intent intent = new Intent(this, ViewRequestsActivity.class);
		intent.putExtra(OWNERNAME, ownerNameVal);
		intent.putExtra(OWNERTIME, ownerTimeVal);
		intent.putExtra(OWNERMILE, ownerMileVal);
		intent.putExtra(OWNERPLNO, ownerPlateNoVal);
		intent.putExtra(OWNERAREA, ownerAreaVal);
		Toast.makeText(getApplicationContext(),
				"Retrieving all requests sent to you", Toast.LENGTH_LONG)
				.show();
		startActivity(intent);
	}

	public void onClickPost(View view) {
		try {
			String ownerNameVal = ownerName.getText().toString().trim();
			String ownerTimeVal = ownerTime.getText().toString().trim();
			String ownerMileVal = ownerMile.getText().toString().trim();
			String ownerPlateNoVal = ownerPlateNo.getText().toString().trim();
			String ownerAreaVal = ownerArea.getText().toString().trim();

			int maxPassenger = 5;
			Location location = mBoundService.getCurrentLocation();
			double lat = location.getLatitude();
			double lon = location.getLongitude();

			Driver driver = new Driver(ownerNameVal, ownerPlateNoVal,
					Double.valueOf(ownerMileVal),
					Integer.valueOf(ownerTimeVal), ownerAreaVal, lon, lat,
					maxPassenger);
			final String url = client.getPostDriverURL(driver);
			System.out.println(url);
			final String[] result = new String[1];
			new Thread(new Runnable() {
				public void run() {
					try {
						result[0] = client.sendGet(url);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			 
			// call map service to populate the owner/car's location
			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra(OWNERNAME, ownerNameVal);
			intent.putExtra(OWNERTIME, ownerTimeVal);
			intent.putExtra(OWNERMILE, ownerMileVal);
			intent.putExtra(OWNERPLNO, ownerPlateNoVal);
			intent.putExtra(OWNERAREA, ownerAreaVal);
			Toast.makeText(getApplicationContext(),
					"Directing to GoogleMap...", Toast.LENGTH_LONG).show();
			startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get the name, time, mile, plate number, and area

	}
	
	
	 private ServiceConnection mConnection = new ServiceConnection() {
		    public void onServiceConnected(ComponentName className, IBinder service) {
		        // This is called when the connection with the service has been
		        // established, giving us the service object we can use to
		        // interact with the service.  Because we have bound to a explicit
		        // service that we know is running in our own process, we can
		        // cast its IBinder to a concrete class and directly access it.
		        mBoundService = ((MyService.LocalBinder)service).getService();

		        // Tell the user about this for our demo.
		        Toast.makeText(CarPostActivity.this, "Identifying current location",
		                Toast.LENGTH_SHORT).show();
		    }

		    public void onServiceDisconnected(ComponentName className) {
		        // This is called when the connection with the service has been
		        // unexpectedly disconnected -- that is, its process crashed.
		        // Because it is running in our same process, we should never
		        // see this happen.
		        mBoundService = null;
		        Toast.makeText(CarPostActivity.this, "disconnected",
		                Toast.LENGTH_SHORT).show();
		    }
		};

		boolean mIsBound;

	   void doBindService() {
		    // Establish a connection with the service.  We use an explicit
		    // class name because we want a specific service implementation that
		    // we know will be running in our own process (and thus won't be
		    // supporting component replacement by other applications).
		    bindService(new Intent(this, 
		            MyService.class), mConnection, Context.BIND_AUTO_CREATE);
		    mIsBound = true;
		}

		void doUnbindService() {
		    if (mIsBound) {
		        // Detach our existing connection.
		        unbindService(mConnection);
		        mIsBound = false;
		    }
		}
		
	    @Override
	    protected void onStart() {
	        super.onStart();
	        doBindService();
	    }

		@Override
		protected void onDestroy() {
		    super.onDestroy();
		    doUnbindService();
		}
	
}
