package com.juliankrone.todolist;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra ("message");
        String title = intent.getStringExtra ("title");

        Intent notIntent = new Intent (context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(message);

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                .setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_image_timer))
                ;

        //Generate a notification with just short text and small icon
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_image_timer)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(style)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .extend(wearableExtender);

        Notification notification = builder.build();
        manager.notify(0, notification);
    }
}