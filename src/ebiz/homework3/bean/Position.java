package ebiz.homework3.bean;

public class Position {
	private double longitude;
	private double latitude;

	public Position(double longitude, double latitude) {
		this.setLongitude(longitude);
		this.setLatitude(latitude);
	}

	public Position() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
