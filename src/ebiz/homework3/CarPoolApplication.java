package ebiz.homework3;

import android.app.Application;

public class CarPoolApplication extends Application {

    private boolean isPickingUp = false;
    
    private boolean approved = false;
    
    private String pickDriver = "";
    private String plateNumber = "";
    private String passengerName = "";
    
	public boolean isPickingUp() {
		return isPickingUp;
	}

	public void setPickingUp(boolean isPickingUp) {
		this.isPickingUp = isPickingUp;
	}

	public String getPickDriver() {
		return pickDriver;
	}

	public void setPickDriver(String pickDriver) {
		this.pickDriver = pickDriver;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
}