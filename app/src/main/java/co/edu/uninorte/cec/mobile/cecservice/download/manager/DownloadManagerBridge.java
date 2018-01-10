package co.edu.uninorte.cec.mobile.cecservice.download.manager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.Vector;

import co.edu.uninorte.cec.mobile.cecservice.shared.objects.DownloadRequest;

/**
 * Created by Administrador on 06/05/2017.
 */

public class DownloadManagerBridge extends BroadcastReceiver  {

    Context applicationContext;
    DownloadManagerBridgeInterface caller;
    Vector<DownloadRequest> downloadsPendingToBeProcessed=new Vector<>();
    public boolean isThisThreadEnabled=true;

    DownloadManager downloadManager;

    public DownloadManagerBridge(Context applicationContext,DownloadManagerBridgeInterface caller){
        this.applicationContext=applicationContext;
        this.caller=caller;
        this.downloadManager=(DownloadManager)
                this.applicationContext.getSystemService(applicationContext.DOWNLOAD_SERVICE);
    }

    public void registerDownloadReceiver(){
        try{
            applicationContext.registerReceiver(this,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }catch (Exception error){
            Log.e("DownloadManagerBridge","registerDownloadReceiver: "+error.toString());
        }
    }

    public void unRegisterDownloadReceiver(){
        try{
            applicationContext.unregisterReceiver(this);
        }catch (Exception error){
            Log.e("DownloadManagerBridge","registerDownloadReceiver: "+error.toString());
        }
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action=intent.getAction();
            if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                long downloadId=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);
                DownloadManager.Query query=new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor=this.downloadManager.query(query);
                if(cursor.moveToFirst()){
                    int columnIndex=cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if(DownloadManager.STATUS_SUCCESSFUL==cursor.getInt(columnIndex)){
                        int fileUriIdx=cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String fileUri=cursor.getString(fileUriIdx);
                        if(fileUri!=null){
                            File file=new File(Uri.parse(fileUri).getPath());
                            caller.onDownloadComplete(downloadId,file.getAbsolutePath());
                        }

                    }
                    else{
                        caller.onDownloadError(downloadId,"Error code: "+cursor.getInt(columnIndex));
                    }
                }
            }
        }catch (Exception error){

        }
    }



    public long processThisDownloadRequest(DownloadRequest downloadRequest) {
        long enqueueId=-1;
        try{
            DownloadManager.Request request=new
                    DownloadManager.Request(Uri.parse(downloadRequest.urlForTheDownload));
            if(!downloadRequest.canMobileNetworkBeUsed){
                request.setAllowedNetworkTypes(request.NETWORK_WIFI);
            }else{
                request.setAllowedNetworkTypes(request.NETWORK_MOBILE|request.NETWORK_WIFI);
            }
            enqueueId= this.downloadManager.enqueue(request);
            return enqueueId;
        }catch (Exception error){

            Log.e("DownloadManagerBridge","processThisDownloadRequest: "+error.toString());
            return enqueueId;
        }

    }
}
