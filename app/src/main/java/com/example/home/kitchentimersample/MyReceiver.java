package com.example.home.kitchentimersample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
	public MyReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "時間です ", Toast.LENGTH_LONG).show();
		MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.se_maoudamashii_jingle04);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		mediaPlayer.start();
		Intent intent2 = new Intent(context, MainActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent2, 0);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setTicker("時間です")
				.setWhen(System.currentTimeMillis())
				.setContentTitle("キッチンタイマー")
				.setContentText("時間になりました")
				.setContentIntent(pendingIntent)
				.build();

		notificationManager.cancelAll();
		notificationManager.notify(R.string.app_name, notification);
	}
}
