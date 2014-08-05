package ebiz.homework3;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person.Name;

import ebiz.homework3.bean.Passenger;
import ebiz.homework3.bean.Position;
import ebiz.homework3.bean.RowItem;
import ebiz.homework3.util.HttpClientConnection;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class ViewRequestsActivity extends ListActivity {
	
	ListView requestListView;
	List<RowItem> rowItems;
	

	//==================
		//private ListView requestListView;
		private String ownerPlateNoVal;
		private String ownerNameVal;
		private HttpClientConnection client = new HttpClientConnection();

		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_view_requests);
		}
		
	    @Override
	    protected void onResume(){	        
	        super.onResume();
	        
			// get owner name and plate no from father intent
		    Intent intent = getIntent();
		    ownerPlateNoVal = intent.getStringExtra(CarPostActivity.OWNERPLNO);
		    ownerNameVal = intent.getStringExtra(CarPostActivity.OWNERNAME);
			// set textview with name and plate no
		    TextView ownerPlateNo = (TextView)findViewById(R.id.owner_plateno);
		    ownerPlateNo.setText(ownerNameVal + " (Plate Number:" + ownerPlateNoVal+")") ;
		    
		    // retrieve requests
		    onClickRefresh();
	
	    }
	    
	    public void onClickRefresh(View view) {	 
	    	onClickRefresh();
	    }
		
	    // this method will send query to server to retrieve all requests sent to the car owner
        public void onClickRefresh() {	 		    
		    
		    // [todo] query all qualified car posts from server using 'ownerPlateNoVal', and store all results as array
		
		    
		    Intent intent = getIntent();
		    String ownername = intent.getStringExtra(CarPostActivity.OWNERNAME);
		    final ArrayList<Passenger> passengers = new ArrayList<Passenger>();
		    
		    final String url = client.getConfirmedPassengersURL(ownername);
		    
		    Thread sendRequest = new Thread(new Runnable() {
				public void run() {
					try {
						String passengerJson = client.sendGet(url);
						ArrayList<Passenger> tmp = client.convertToPassengerList(passengerJson);
						for(Passenger passenger : tmp){
							passengers.add(passenger);
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
		    //requestListView = getListView();
		    //requestListView.setAdapter(new ArrayAdapter<Passenger>(this, android.R.layout.simple_list_item_1, passengers));
		    // new code as follows:
	        rowItems = new ArrayList<RowItem>();
	        for (int i = 0; i < passengers.size(); i++) {
	            RowItem item = new RowItem(R.drawable.passenger, passengers.get(i).getName(), 
	            		"TEL: "+passengers.get(i).getPhone(), "DES: " + passengers.get(i).getDescription());
	            rowItems.add(item);
	        }	 
	        requestListView = getListView();//(ListView) findViewById(R.id.list);
	        CustomBaseAdapter adapter = new CustomBaseAdapter(this, rowItems);
	        requestListView.setAdapter(adapter);
		    
		    
		    
		    // set single item click listener and override the callback method
		    requestListView.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		          popupDialog("Pick up", passengers.get((int)id));
		      }
		    });
		    
		}
	
	   private void popupDialog(String title,final Passenger passenger){
		   	// create a pop up window to let the user know
		   	AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
		   	// create textview for centre title
			TextView myMsg = new TextView(this);
			myMsg.setText(title);
			myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
			myMsg.setTextSize(18);
			myMsg.setTextColor(Color.RED);
			// set custom title
			builder.setCustomTitle(myMsg);
			// set message
			builder.setMessage("Pick up " + passenger.toString());
			// set button
			builder.setPositiveButton("Approve and Track", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int id) {
				    // [todo]leverage passenger's location(should be a field of a retrieved object from server since we alredy hold
			    	//		 Name and plate number) and owner's location(retrieved from server again, based on the clicked list item) 
			    	//       and draw a path in a pathactivity
			        //LatLng  fromPosition = new LatLng(40.663837, -79.147297);// owner's location
			    	
			    	try {
				    							
						final String url = client.setApprovedURL(passenger.getName());
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
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			        LatLng  toPosition = new LatLng(passenger.getLatitude(), passenger.getLongitude());  // passenger's location
			        //LatLng  toPosition = new LatLng(40.457263, -79.934059);  // passenger's location
			        Intent intent = new Intent(ViewRequestsActivity.this, MapDirectActivity.class);
					//intent.putExtra("fromPosition", fromPosition);
					intent.putExtra("toPosition", toPosition);
			        startActivity(intent);
			    }
			});
			builder.setNegativeButton("Cancel", null);
			// display the dialog
			builder.show();
	   
	   }
}
