package co.edu.uninorte.cec.mobile.cecservice.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Administrador on 29/04/2017.
 */

public class Utils {

    public static void showNotification(Context context,String title,String message,
                                 int icon, int notificationIdParam){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(icon);
        Notification notification=builder.build();
        ((NotificationManager)context.getSystemService(
                Context.NOTIFICATION_SERVICE)).notify(notificationIdParam,notification);
    }

    public static void removeNotification(Context context,int notificationIdParam){
        ((NotificationManager)context.getSystemService(
                Context.NOTIFICATION_SERVICE)).cancel(notificationIdParam);
    }
}
