package com.swg7.dalpolls.notification;

import android.content.Context;

import com.swg7.dalpolls.MainActivity;
import com.swg7.dalpolls.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationCreator{

    private static final String CHANNEL_ID = "Notifications";
    private static final String NEW_ELECTION = "Dal Polls";

    private Context context;

    public NotificationCreator(Context context){
        this.context = context;
    }

    public boolean isContentLengthValid(String content) {
        return content.length()<60 && content.length()>0;
    }

    public void createNewNotification(String textContent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(NEW_ELECTION)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //
        if(isContentLengthValid(textContent)){
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, builder.build());
        }
    }

}