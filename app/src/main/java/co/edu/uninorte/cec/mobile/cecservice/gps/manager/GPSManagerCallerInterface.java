package co.edu.uninorte.cec.mobile.cecservice.gps.manager;

import android.location.Location;

/**
 * Created by Administrador on 29/04/2017.
 */

public interface GPSManagerCallerInterface {
    void newLocationHasBeenReceived(Location location);
    void isLocationEnabled(boolean isEnabled);


}
