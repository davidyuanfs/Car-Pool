package ebiz.homework3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ebiz.homework3.bean.Driver;
import ebiz.homework3.bean.Passenger;
import ebiz.homework3.util.HttpClientConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipData.Item;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
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
import android.widget.Button;

public class CarSearchActivity extends ListActivity {
	private ListView carsListView;
	private TextView selectedItem;
	private MyService mBoundService;
	private Button buttonConfirm;
	private Button buttonSearch;
	private Button buttonrefresh;
	private TextView driverInfo;
	private EditText searchBox;
	
	private HttpClientConnection client = new HttpClientConnection();
	public final static String OWNERNAME = "OWNERNAME";
	public final static String OWNERPLNO = "OWNERPLNO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_search);
		buttonConfirm = (Button) findViewById(R.id.button_confirm);
		buttonSearch = (Button) findViewById(R.id.button_search);
		buttonrefresh = (Button) findViewById(R.id.button_search_refresh);
		driverInfo = (TextView)findViewById(R.id.driver_info);
		searchBox = (EditText)findViewById(R.id.edit_mile);
		carsListView = (ListView) findViewById(android.R.id.list);
		buttonConfirm.setBackgroundColor(Color.RED);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// get owner name and plate no from father intent
		if(((CarPoolApplication) this.getApplication()).isPickingUp()){
			buttonConfirm.setVisibility(View.VISIBLE);
			driverInfo.setVisibility(View.VISIBLE);
			String driver = ((CarPoolApplication) this.getApplication()).getPickDriver();
			String plateNumber = ((CarPoolApplication) this.getApplication()).getPlateNumber();
			driverInfo.setText("Waiting for "+driver+ " to pick you up! Plate Number:" + plateNumber);
			buttonSearch.setVisibility(View.INVISIBLE);
			buttonrefresh.setVisibility(View.INVISIBLE);
			searchBox.setVisibility(View.INVISIBLE);
			carsListView.setVisibility(View.INVISIBLE);
		}else{
			buttonSearch.setVisibility(View.VISIBLE);
			buttonrefresh.setVisibility(View.VISIBLE);
			searchBox.setVisibility(View.VISIBLE);
			carsListView.setVisibility(View.VISIBLE);
			
			driverInfo.setVisibility(View.INVISIBLE);
			buttonConfirm.setVisibility(View.INVISIBLE);
		}
		
	}
	public void onClickRefresh(View view) throws Exception {
		onClickSearch(view);
	}

	
	public void completedRide(View view) throws Exception {
		((CarPoolApplication) this.getApplication()).setPickingUp(false);
		onResume();
	}
	
	public void onClickSearch(View view) throws Exception {

		// get input radis from edit box
		EditText editMessage = (EditText) findViewById(R.id.edit_mile);
		double radius = Double.valueOf(editMessage.getText().toString().trim());

		// instead of using activity, connect to my local service to get the
		// passenger's location in the behind!
		Location location = mBoundService.getCurrentLocation();
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		Log.i("Latitude Longitude - ", lat + "," + lon);
		Geocoder myGeocoder = new Geocoder(getApplicationContext(),
				Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = myGeocoder.getFromLocation(lat, lon, 1);
		} catch (IOException e) {
			Log.v("info", e.getStackTrace().toString());
			popupDialog("Google address look up unavailable",
					"Please check your network availability and try again later");
			return;
		}
		Address address = addresses.get(0);
		String addressText = String.format(
				"%s, %s, %s",
				// If there's a street address, add it
				address.getMaxAddressLineIndex() > 0 ? address
						.getAddressLine(0) : "",
				// Locality is usually a city
				address.getLocality(),
				// The country of the address
				address.getCountryName());
		Log.i("Address", addressText);

		// [debug]set the location info to the textView below the two buttons
		TextView textMessage = (TextView) findViewById(R.id.passenger_location);
		textMessage.setText("Your location:" + addressText);

		// [todo] query all qualified car posts using passenger's location and
		// the input radis from server and store as array

		final ArrayList<Driver> drivers = new ArrayList<Driver>();

		final String url = client.getAvailableDriver(radius, lat, lon);

		Thread sendRequest = new Thread(new Runnable() {
			public void run() {
				try {
					String driverJson = client.sendGet(url);
					ArrayList<Driver> tmp = client
							.convertToDriverList(driverJson);
					for (Driver driver : tmp) {
						drivers.add(driver);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		try {
			sendRequest.start();
			sendRequest.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set adapter
		carsListView = getListView();
		carsListView.setAdapter(new ArrayAdapter<Driver>(this,
				android.R.layout.simple_list_item_1, drivers));
		// set single item click listener and override the callback method
		carsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// drivers.get((int)id

				Intent intent = new Intent(CarSearchActivity.this,
						ConfirmDriverActivity.class);

				intent.putExtra(OWNERNAME, drivers.get((int) id).getDriver());
				intent.putExtra(OWNERPLNO, drivers.get((int) id)
						.getLicenceNumber());
				startActivity(intent);
				
//				popupDialog("Send Request to Driver", drivers.get((int) id)
//						.toString());
			}
		});

	}

	private void popupDialog(String title, String Message) {
		// create a pop up window to let the user know
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.AlertDialogCustom));
		// create textview for centre title
		TextView myMsg = new TextView(this);
		myMsg.setText(title);
		myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		myMsg.setTextSize(18);
		myMsg.setTextColor(Color.RED);
		// set custom title
		builder.setCustomTitle(myMsg);
		// set message
		builder.setMessage(Message);
		// set button
		builder.setPositiveButton("Request",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						selectedItem.setBackgroundColor(Color.GREEN);
					}
				});
		builder.setNegativeButton("Cancel", null);
		// display the dialog
		builder.show();

	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((MyService.LocalBinder) service).getService();

			// Tell the user about this for our demo.
			Toast.makeText(CarSearchActivity.this,
					"Identifying current location", Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
			Toast.makeText(CarSearchActivity.this, "disconnected",
					Toast.LENGTH_SHORT).show();
		}
	};

	boolean mIsBound;

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(new Intent(this, MyService.class), mConnection,
				Context.BIND_AUTO_CREATE);
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
