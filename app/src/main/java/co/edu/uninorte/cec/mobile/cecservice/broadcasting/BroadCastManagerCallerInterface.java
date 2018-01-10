package co.edu.uninorte.cec.mobile.cecservice.broadcasting;

import android.content.Intent;

/**
 * Created by Administrador on 28/04/2017.
 */

public interface BroadCastManagerCallerInterface {
    /**
     * **
     * This method is called when an intent Has Been ReceivedT hrough The BroadCast
     * @param intent This is the intent that was received
     */
    void intentHasBeenReceivedThroughTheBroadCast(Intent intent);
}
