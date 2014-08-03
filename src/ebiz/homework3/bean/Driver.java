package ebiz.homework3.bean;

public class Driver {
	private String driver;
	private String licenceNumber;
	private double radius;
	private int waitTime;
	private String description;
	private double longitude;
	private double latitude;
	private int maxPassenger;
	
	public Driver(){
		
	}
	
	public Driver(String driver, String licenceNumber,
			double radius, int waitTime, String description, double longitude,
			double latitude, int maxPassenger){
		this.driver = driver;
		this.licenceNumber = licenceNumber;
		this.radius = radius;
		this.waitTime = waitTime;
		this.description = description;
		this.setLongitude(longitude);
		this.setLatitude(latitude);
		this.maxPassenger = maxPassenger;
	}
	
	/**
	 * @return the licenceNumber
	 */
	public String getLicenceNumber() {
		return licenceNumber;
	}
	/**
	 * @param licenceNumber the licenceNumber to set
	 */
	public void setLicenceNumber(String licenceNumber) {
		this.licenceNumber = licenceNumber;
	}
	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}
	/**
	 * @param radius the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
	/**
	 * @return the waitTime
	 */
	public int getWaitTime() {
		return waitTime;
	}
	/**
	 * @param waitTime the waitTime to set
	 */
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
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
	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}
	/**
	 * @param driver the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}
	/**
	 * @return the maxPassenger
	 */
	public int getMaxPassenger() {
		return maxPassenger;
	}
	/**
	 * @param maxPassenger the maxPassenger to set
	 */
	public void setMaxPassenger(int maxPassenger) {
		this.maxPassenger = maxPassenger;
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
		return String.format("%-10s Licence Number: %-15s Wait Time: %d ",
				this.getDriver(), this.getLicenceNumber(), this.getWaitTime());
	}
}
