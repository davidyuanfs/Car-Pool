package ebiz.homework3.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ebiz.homework3.bean.Driver;
import ebiz.homework3.bean.Passenger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpClientConnection{

	private final String USER_AGENT = "Mozilla/5.0";
	private final String EC2_URL = "http://ec2-54-186-15-130.us-west-2.compute.amazonaws.com:8080/Restful/mobile/";
	
	public class DriverList{
		private ArrayList<Driver> drivers;

		public ArrayList<Driver> getDrivers() {
			return drivers;
		}

		public void setDrivers(ArrayList<Driver> drivers) {
			this.drivers = drivers;
		}
	}
	
	public class PassengerList{
		private List<Passenger> passenger;

		public List<Passenger> getPassengers() {
			return passenger;
		}

		public void setPassengers(List<Passenger> passenger) {
			this.passenger = passenger;
		}
	}
	
	
	public String sendGet(String url) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : "
				+ response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
		return result.toString();
	}

	public ArrayList<Passenger> convertToPassengerList(String passengerJson) {
		passengerJson = passengerJson.replace("\"Passenger\":", "");
		passengerJson = passengerJson.substring(1,passengerJson.length()-1);
		
		ObjectMapper objectMapper = new ObjectMapper();
		List<Passenger> passengers = null;		
		 try {
			passengers = objectMapper.readValue(
					 passengerJson,
			            objectMapper.getTypeFactory().constructCollectionType(
			                    List.class, Passenger.class));
			if(passengers.size() != 0)
				passengers.remove(passengers.size()-1);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Passenger>)passengers;
	}
	
	
	public ArrayList<Driver> convertToDriverList(String driverJson){
		
		driverJson = driverJson.replace("\"Driver\":", "");
		driverJson = driverJson.substring(1,driverJson.length()-1);
		
		ObjectMapper objectMapper = new ObjectMapper();
		List<Driver> drivers = null;		
		 try {
			 drivers = objectMapper.readValue(
					driverJson,
			            objectMapper.getTypeFactory().constructCollectionType(
			                    List.class, Driver.class));
			 if(drivers.size() != 0)
				 drivers.remove(drivers.size()-1);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (ArrayList<Driver>)drivers;
	}
	
	public String getPostDriverURL(Driver driver){
		//http://localhost:8080/Restful/mobile/postDriver?driver=david&licenceNumber=213&radius=1000&waitTime=123&description=123123&longitude=54253.00&latitude=500359.00&maxPassenger=5
		StringBuffer url = new StringBuffer(EC2_URL+"postDriver?");
		url.append("driver="+driver.getDriver()+"&");
		url.append("licenceNumber="+driver.getLicenceNumber()+"&");
		url.append("radius="+driver.getRadius()+"&");
		url.append("waitTime="+driver.getWaitTime()+"&");
		url.append("description="+driver.getDescription()+"&");
		url.append("longitude="+driver.getLongitude()+"&");
		url.append("latitude="+driver.getLatitude()+"&");
		url.append("maxPassenger="+driver.getMaxPassenger());
		return url.toString();
	}
	
	public String getConfirmedPassengersURL(String driver){
		//http://localhost:8080/Restful/mobile/getConfirmedPassengers?driver=david
		StringBuffer url = new StringBuffer(EC2_URL+"getConfirmedPassengers?");
		url.append("driver="+driver);
		System.out.println(url.toString());
		return url.toString();
	}
	
	public String getAvailableDriver(double distance, double latitude, double longitude){
		//http://localhost:8080/Restful/mobile/getAvailableDriver?distance=1000&latitude=583838&longitude=0030412
		StringBuffer url = new StringBuffer(EC2_URL+"getAvailableDriver?");
		url.append("distance="+distance+"&");
		url.append("latitude="+latitude+"&");
		url.append("longitude="+longitude);
		System.out.println(url.toString());
		return url.toString();
	}
	
	public String confirmTheDriver(String driver, Passenger passenger){
		//http://localhost:8080/Restful/mobile/confirmedPassengers?driver=david&name=test&description=123123&phone=123123&longitude=123.123&latitude=123.123
		StringBuffer url = new StringBuffer(EC2_URL+"confirmedPassengers?");
		url.append("driver="+driver+"&");
		url.append("name="+passenger.getName()+"&");
		url.append("description="+passenger.getDescription()+"&");
		url.append("phone="+passenger.getPhone()+"&");
		url.append("longitude="+passenger.getLongitude()+"&");
		url.append("latitude="+passenger.getLatitude());
		System.out.println(url.toString());
		return url.toString();
	}
	
	
}
