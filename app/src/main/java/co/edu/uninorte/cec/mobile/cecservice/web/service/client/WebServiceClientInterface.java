package co.edu.uninorte.cec.mobile.cecservice.web.service.client;

import co.edu.uninorte.cec.mobile.cecservice.shared.objects.WebServiceCallRequest;

/**
 * Created by Administrador on 05/05/2017.
 */

public interface WebServiceClientInterface {
    void responeFromServerHasBeenReceived(WebServiceCallRequest response);
}
