package co.edu.uninorte.cec.mobile.cecservice.core;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import co.edu.uninorte.cec.mobile.cecservice.R;
import co.edu.uninorte.cec.mobile.cecservice.broadcasting.BroadCastManager;
import co.edu.uninorte.cec.mobile.cecservice.broadcasting.BroadCastManagerCallerInterface;

/**
 * Created by Administrador on 29/04/2017.
 */

public class PermissionsActivity extends AppCompatActivity implements View.OnClickListener,BroadCastManagerCallerInterface {

    private static final int PERMISSIONS_REQUEST_FOR_LOCATION =2001 ;
    public String incommingBroadCastString="com.example.administrador.myapplication.in";
    public String outgoingBroadCastString="co.edu.uninorte.cec.mobile.cecservice.core.in";
    BroadCastManager broadCastManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissions_activity);
        ((Button)findViewById(R.id.request_permissions_button)).setOnClickListener(this);
        initializeBroadCast();
    }

    public void initializeBroadCast(){
        this.broadCastManager=new
                BroadCastManager(this,incommingBroadCastString,
                outgoingBroadCastString,this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_FOR_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    this.broadCastManager.sendBroadCastMessage("InternalComm","LocationPermissionOk");
                    finish();

                } else {

                    try{
                        Intent intentToCallTheService=
                                new Intent("co.edu.uninorte.cec.mobile.cecservice.core.CECService");
                        intentToCallTheService.
                                setPackage("co.edu.uninorte.cec.mobile.cecservice");
                        stopService(intentToCallTheService);
                        Toast.makeText(this,"No hay permiso no hay servicio",Toast.LENGTH_SHORT).show();
                    }catch (Exception error){
                        Log.e("IntentsFragment","stopBackgroundService: "+error.toString());
                    }
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.request_permissions_button){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission. ACCESS_FINE_LOCATION,
                            Manifest.permission. ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_FOR_LOCATION);
        }
    }

    @Override
    public void intentHasBeenReceivedThroughTheBroadCast(Intent intent) {

    }
}
