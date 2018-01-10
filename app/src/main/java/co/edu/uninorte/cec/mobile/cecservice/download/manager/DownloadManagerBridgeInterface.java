package co.edu.uninorte.cec.mobile.cecservice.download.manager;

import co.edu.uninorte.cec.mobile.cecservice.shared.objects.DownloadRequest;

/**
 * Created by Administrador on 06/05/2017.
 */

public interface DownloadManagerBridgeInterface {
    void onDownloadComplete(long downloadId,String urlOfDownloadFile);
    void onDownloadError(long downloadId,String errorMessage);
}
