package net.kaisti.txrxwidget;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TurnOffActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
		
		Intent serviceIntent = new Intent(this, NotifyWidgetService.class);
		stopService(serviceIntent);
		
		this.finish();
	}

}
