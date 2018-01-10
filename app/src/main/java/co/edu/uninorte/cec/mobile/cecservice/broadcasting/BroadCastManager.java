package co.edu.uninorte.cec.mobile.cecservice.broadcasting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Administrador on 28/04/2017.
 */

public class BroadCastManager extends BroadcastReceiver {

    BroadCastManagerCallerInterface caller;
    Context context;
    String incommingBroadCastString;
    String outgoingBroadCastString;

    public BroadCastManager(Context context,
                            String incommingBroadCastStringParam,
                            String outgoingBroadCastStringParam, 
                            BroadCastManagerCallerInterface caller){
        this.context=context;
        this.incommingBroadCastString=incommingBroadCastStringParam;
        this.outgoingBroadCastString=outgoingBroadCastStringParam;
        this.caller=caller;

    }

    public void registerBroadCastReceiver(){
        try{
            IntentFilter intentFilter=new IntentFilter(this.incommingBroadCastString);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            this.context.registerReceiver(this,intentFilter);
        }catch(Exception error){
            Log.e("BroadCastManager","registerBroadCastReceiver: "+error.toString());
        }
    }

    public void unregisterBroadCastReceiver(){
        try{
            this.context.unregisterReceiver(this);
        }catch (Exception error){
            Log.e("BroadCastManager","unregisterBroadCastReceiver: "+error.toString());
        }
    }

    public void sendBroadCastMessage(String messageType,String message){
        try{
            Intent intentToBeSent=new Intent(this.outgoingBroadCastString);
            intentToBeSent.addCategory(Intent.CATEGORY_DEFAULT);
            intentToBeSent.putExtra("MessageType",messageType);
            intentToBeSent.putExtra("MessagePayload",message);
            this.context.sendBroadcast(intentToBeSent);
        }catch (Exception error){
            Log.e("BroadCastManager","sendBroadCastMessage: "+error.toString());
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        this.caller.intentHasBeenReceivedThroughTheBroadCast(intent);

    }
}
