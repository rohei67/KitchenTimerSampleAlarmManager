package com.example.home.kitchentimersample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	TextView _tvTime;
	Timer _timer;
	Handler _handler = new Handler();

	ArrayList<String> _strTimeAry = new ArrayList<>();
	long _alarmTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_tvTime = (TextView) findViewById(R.id.textView);
		for (int i = 0; i < 4; i++) {
			_strTimeAry.add("0");
		}
		Button btnStart = (Button) findViewById(R.id.buttonStart);
		Button btnStop = (Button) findViewById(R.id.buttonStop);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startCountDown();
	}

	class MyTask extends TimerTask {
		@Override
		public void run() {
			final long remainingTime = _alarmTime - System.currentTimeMillis();
//			if (remainingTime < 0) return;
			_handler.post(new Runnable() {
				@Override
				public void run() {
					long min = remainingTime / (60 * 1000);
					long sec = remainingTime % (60 * 1000) / 1000;
					_tvTime.setText(min + "分" + sec + "秒");
				}
			});
			if (remainingTime < 0) {
				_timer.cancel();
				SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putLong("time", 0);
				editor.commit();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void numClick(View view) {
		Button bt = (Button) view;
		_strTimeAry.add(bt.getText().toString());
		_strTimeAry.remove(0);
		_tvTime.setText(_strTimeAry.get(0) + _strTimeAry.get(1) + "分" + _strTimeAry.get(2) + _strTimeAry.get(3) + "秒");
	}

	@Override
	public void onClick(View v) {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		long alarmTime = calcAlarmTime();
		if (v.getId() == R.id.buttonStart) {
			alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pending);
			editor.putLong("time", alarmTime);
		} else if (v.getId() == R.id.buttonStop) {
			alarmManager.cancel(pending);
			editor.putLong("time", 0l);
		}
		editor.commit();
		startCountDown();
	}

	private long calcAlarmTime() {
		String min = _strTimeAry.get(0) + _strTimeAry.get(1);
		String sec = _strTimeAry.get(2) + _strTimeAry.get(3);
		long lMin = Long.parseLong(min) * 60 * 1000;
		long lSec = Long.parseLong(sec) * 1000;
		return System.currentTimeMillis() + lMin + lSec;
	}

	void startCountDown() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		_alarmTime = sharedPreferences.getLong("time", 0l);
		if (_alarmTime > 0) {
			_timer = new Timer();
			_timer.schedule(new MyTask(), _alarmTime % 1000, 200);
		}
	}
}
