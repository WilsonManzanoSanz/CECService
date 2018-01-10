package co.edu.uninorte.cec.mobile.cecservice.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

import co.edu.uninorte.cec.mobile.cecservice.broadcasting.BroadCastManager;
import co.edu.uninorte.cec.mobile.cecservice.broadcasting.BroadCastManagerCallerInterface;
import co.edu.uninorte.cec.mobile.cecservice.database.manager.DataBaseManager;
import co.edu.uninorte.cec.mobile.cecservice.download.manager.DownloadManagerBridge;
import co.edu.uninorte.cec.mobile.cecservice.download.manager.DownloadManagerBridgeInterface;
import co.edu.uninorte.cec.mobile.cecservice.gps.manager.GPSManager;
import co.edu.uninorte.cec.mobile.cecservice.gps.manager.GPSManagerCallerInterface;
import co.edu.uninorte.cec.mobile.cecservice.shared.objects.DownloadRequest;
import co.edu.uninorte.cec.mobile.cecservice.shared.objects.GPSPosition;
import co.edu.uninorte.cec.mobile.cecservice.shared.objects.InsertSQLRequest;
import co.edu.uninorte.cec.mobile.cecservice.shared.objects.QuerySQLRequest;
import co.edu.uninorte.cec.mobile.cecservice.shared.objects.WebServiceCallRequest;
import co.edu.uninorte.cec.mobile.cecservice.utils.Utils;
import co.edu.uninorte.cec.mobile.cecservice.web.service.client.WebServiceClientInterface;
import co.edu.uninorte.cec.mobile.cecservice.web.service.client.WebServiceClientManager;

/**
 * Created by Administrador on 28/04/2017.
 */

public class CECService extends Service implements BroadCastManagerCallerInterface,
        GPSManagerCallerInterface,WebServiceClientInterface,DownloadManagerBridgeInterface {

    BroadCastManager broadCastManager;
    public static String incommingString="co.edu.uninorte.cec.mobile.cecservice.core.in";
    public static String outgoingString="com.example.administrador.myapplication.in";
    private int notificationId=1001;

    GPSManager gpsManager;
    private Gson gsonObject;

    WebServiceClientManager webServiceClientManager;
    DownloadManagerBridge downloadManagerBridge;
    DataBaseManager dataBaseManager;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeService();
        return super.onStartCommand(intent, flags, startId);
    }

    public void initializeService(){
        try{
            //android.os.Debug.waitForDebugger();
            initializeBroadCastManager();
            this.broadCastManager.sendBroadCastMessage("ServiceInformation","The service is started");
            initializeGPSManager();
            initializeWebServiceManager();
            initializeDownloadManager();
            initializeDataBaseManager();
            Log.i("CECService","initializeService: service started");

            Utils.showNotification(this,
                    "CEC Service","Service is running",
                    android.R.drawable.ic_media_play,this.notificationId);
        }catch (Exception error){

        }
    }
    public void initializeBroadCastManager(){
        try{
            this.broadCastManager=new BroadCastManager(this,incommingString,outgoingString,this);
            broadCastManager.registerBroadCastReceiver();
        }catch (Exception error){

        }

    }

    public void initializeGPSManager(){
        try{
            this.gpsManager=new GPSManager(this,this);
            this.gpsManager.createGooglePlayServicesClient();
        }catch (Exception error){

        }
    }

    public void initializeWebServiceManager(){
        try{
           this.webServiceClientManager=new WebServiceClientManager(this);
        }catch (Exception error){

        }
    }

    public void initializeDownloadManager(){
        try{
            this.downloadManagerBridge=new DownloadManagerBridge(this,this);
            this.downloadManagerBridge.registerDownloadReceiver();
        }catch (Exception error){

        }
    }

    public void initializeDataBaseManager(){
        try{
            this.dataBaseManager=new DataBaseManager(this);
        }catch (Exception error){

        }
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        this.broadCastManager.unregisterBroadCastReceiver();
        Utils.removeNotification(this,this.notificationId);
        super.onDestroy();
    }

    @Override
    public void intentHasBeenReceivedThroughTheBroadCast(Intent intent) {

        String messageType=intent.getExtras().getString("MessageType");
        String payload=intent.getExtras().getString("MessagePayload");
        if(messageType.equals("PlainText")){
            Toast.makeText(this,"Plain text received: "+payload,Toast.LENGTH_SHORT).show();
        }
        if(messageType.equals("InternalComm")){
            if(gpsManager!=null){
                if(payload.equals("LocationPermissionOk")) {
                    gpsManager.startLocationUpdates();
                }
            }
        }
        if(messageType.equals("WebServiceCallRequest")){
            WebServiceCallRequest request= this.getGson().fromJson(payload,WebServiceCallRequest.class);
            if(request!=null){
                this.webServiceClientManager.addThisPackageToTheQueue(request);
            }

        }

        if(messageType.equals("DownloadRequest")){
            DownloadRequest request= this.getGson().fromJson(payload,DownloadRequest.class);
            if(request!=null){
                this.downloadManagerBridge.processThisDownloadRequest(request);
            }

        }

        if(messageType.equals("QuerySQLRequest"))
        {
            QuerySQLRequest request=getGson().fromJson(payload,QuerySQLRequest.class);
            request.result=dataBaseManager.executeQuery(request.queryString);
            if(request.result==null){
                request.successfullY=false;
            }else{
                request.successfullY=true;
            }
            this.broadCastManager.sendBroadCastMessage("QuerySQLRequest",this.getGson().toJson(request));

        }

        if(messageType.equals("InsertSQLRequest"))
        {
            InsertSQLRequest request=getGson().fromJson(payload,InsertSQLRequest.class);
            String result=dataBaseManager.insertIntoTable(request.tableName,request.contentValues);
            if(result.indexOf("Rows affected")!=-1){
                request.successfully=true;
            }else{
                request.successfully=false;
                request.errorMessage=result;
            }
            this.broadCastManager.sendBroadCastMessage("InsertSQLRequest",this.getGson().toJson(request));

        }

    }




    public Gson getGson(){
        if(gsonObject==null) {
            gsonObject = new Gson();
        }
        return gsonObject;
    }

    @Override
    public void newLocationHasBeenReceived(Location location) {
        Log.i("CECService","newLocationHasBeenReceived: "+location);

        GPSPosition gpsPosition=new GPSPosition();
        gpsPosition.longitude=location.getLongitude();
        gpsPosition.latitude=location.getLatitude();
        gpsPosition.dateTime= Calendar.getInstance().getTime();

        this.broadCastManager.sendBroadCastMessage("GPSPosition",this.getGson().toJson(gpsPosition));



    }

    @Override
    public void isLocationEnabled(boolean isEnabled) {

    }

    @Override
    public void responeFromServerHasBeenReceived(WebServiceCallRequest response) {
        this.broadCastManager.sendBroadCastMessage("WebServiceCallRequest",getGson().toJson(response));

    }

    @Override
    public void onDownloadComplete(long downloadId, String urlOfDownloadFile) {
        DownloadRequest downloadResponse=new DownloadRequest();
        downloadResponse.downloadedFilePath=urlOfDownloadFile;
        downloadResponse.successfully=true;
        this.broadCastManager.sendBroadCastMessage("DownloadRequest",this.getGson().toJson(downloadResponse));

    }

    @Override
    public void onDownloadError(long downloadId, String errorMessage) {
        DownloadRequest downloadResponse=new DownloadRequest();
        downloadResponse.errorMessage=errorMessage;
        downloadResponse.successfully=false;
        this.broadCastManager.sendBroadCastMessage("DownloadRequest",this.getGson().toJson(downloadResponse));

    }
}
