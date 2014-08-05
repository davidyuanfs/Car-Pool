package ebiz.homework3;

import ebiz.homework3.bean.Driver;
import ebiz.homework3.bean.Passenger;
import ebiz.homework3.util.HttpClientConnection;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmDriverActivity extends ListActivity {
	private EditText ownerName;
	private EditText ownerPhone;
	private EditText ownerDescription;
	private String ownerPlateNoVal;
	private String ownerNameVal;
	private MyService mBoundService;
	private HttpClientConnection client = new HttpClientConnection();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conf_driver);
		ownerName = (EditText) findViewById(R.id.passenger_name);
		ownerPhone = (EditText) findViewById(R.id.passenger_phone);
		ownerDescription = (EditText) findViewById(R.id.passenger_decrip);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// get owner name and plate no from father intent
		Intent intent = getIntent();
		ownerNameVal = intent.getStringExtra(CarSearchActivity.OWNERNAME);
		ownerPlateNoVal = intent.getStringExtra(CarSearchActivity.OWNERPLNO);
		// set textview with name and plate no
		TextView ownerPlateNo = (TextView) findViewById(R.id.passenger_driver);
		ownerPlateNo.setText(ownerNameVal + " (Plate Number:" + ownerPlateNoVal
				+ ")");
	}
	
	
	public void onClickConfirm(View view) {
		String passengerName = ownerName.getText().toString().trim();
		String passengerPhone = ownerPhone.getText().toString().trim();
		String passengerDesc = ownerDescription.getText().toString().trim();


		Location location;
		try {
			location = mBoundService.getCurrentLocation();
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			Passenger passenger = new Passenger(passengerName, passengerPhone,
					passengerDesc, lon, lat, false);
			final String url = client.confirmTheDriver(ownerNameVal, passenger);
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
			
			if(result[0] != ""){
				((CarPoolApplication) this.getApplication()).setPickingUp(true);
				((CarPoolApplication) this.getApplication()).setPickDriver(ownerNameVal);
				((CarPoolApplication) this.getApplication()).setPlateNumber(ownerPlateNoVal);
				((CarPoolApplication) this.getApplication()).setPassengerName(passenger.getName());
			}
			finish();
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
		        Toast.makeText(ConfirmDriverActivity.this, "Identifying current location",
		                Toast.LENGTH_SHORT).show();
		    }

		    public void onServiceDisconnected(ComponentName className) {
		        // This is called when the connection with the service has been
		        // unexpectedly disconnected -- that is, its process crashed.
		        // Because it is running in our same process, we should never
		        // see this happen.
		        mBoundService = null;
		        Toast.makeText(ConfirmDriverActivity.this, "disconnected",
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
