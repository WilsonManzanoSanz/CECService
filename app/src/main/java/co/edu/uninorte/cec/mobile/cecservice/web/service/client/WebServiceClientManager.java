package co.edu.uninorte.cec.mobile.cecservice.web.service.client;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import co.edu.uninorte.cec.mobile.cecservice.shared.objects.WebServiceCallRequest;

/**
 * Created by Administrador on 05/05/2017.
 */

public class WebServiceClientManager extends Thread{

    Vector<WebServiceCallRequest> packagesPendingToBeSent=new Vector<>();
    public boolean isThisThreadEnabled=true;
    WebServiceClientInterface caller;

    public WebServiceClientManager(WebServiceClientInterface callerParameter){
        this.caller=callerParameter;
        this.start();
    }

    public void addThisPackageToTheQueue(WebServiceCallRequest request){
        this.packagesPendingToBeSent.add(request);
    }

    @Override
    public void run() {
        try{
            while(this.isThisThreadEnabled){
                while(this.packagesPendingToBeSent.size()>0){
                    WebServiceCallRequest current=this.packagesPendingToBeSent.get(0);
                    this.packagesPendingToBeSent.remove(0);
                    WebServiceCallRequest response=sendThisPackageToServerSide(current);
                    caller.responeFromServerHasBeenReceived(response);
                    this.sleep(1000);
                }
                this.sleep(2000);
            }

        }catch (Exception error){

        }
    }

    private WebServiceCallRequest sendThisPackageToServerSide(WebServiceCallRequest current) {
        HttpURLConnection urlConnection=null;
        try{
            URL url=new URL(current.urlForTheHttpCall);
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod(current.requestMethod);
            urlConnection.setRequestProperty("Content-Type",current.contentType);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            urlConnection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(urlConnection.getOutputStream());
            outputStreamWriter.write(current.messageToBeSent);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            int responseCode=urlConnection.getResponseCode();
            current.responseCode=responseCode;
            if(responseCode==200){
                current.successfully=true;
                current.result=extractOutputFromTheServerResponse(urlConnection);
            }else{
                current.successfully=false;
                current.result=extractOutputFromTheServerResponse(urlConnection);
            }
        }catch (Exception error){

        }
        return current;
    }

    public String extractOutputFromTheServerResponse(HttpURLConnection connection) throws Exception{
        StringBuffer response=new StringBuffer();
        InputStream inputStream=connection.getInputStream();
        int charRead=0;
        while((charRead=inputStream.read())!=-1){
            response.append((char)charRead);
        }
        inputStream.close();
        return response.toString();

    }




}
