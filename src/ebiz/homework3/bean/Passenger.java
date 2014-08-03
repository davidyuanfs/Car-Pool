package ebiz.homework3.bean;

public class Passenger {
	private String name;
	private String phone;
	private String description;
	private double longitude;
	private double latitude;
	
	public Passenger(){
		
	}
	
	public Passenger(String name, String phone, String description,
			double longitude, double latitude) {
		this.name = name;
		this.phone = phone;
		this.description = description;
		this.setLongitude(longitude);
		this.setLatitude(latitude);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return String.format("%-10s Phone: %-15s Description: %s",
				this.getName(), this.getPhone(), this.getDescription());
	}
}
