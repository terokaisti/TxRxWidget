package net.kaisti.txrxwidget;

import java.text.DecimalFormat;
import java.util.HashMap;

import net.kaisti.txrxwidget.NotifyInfo.TrafficType;
import net.kaisti.txrxwidget.NotifyInfo.TrafficChannel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NotifyWidgetService extends Service{
	protected static final long DELAY = 3000; // notification updates every two seconds
    private static int TXRX_ON = R.drawable.txrx_on;  // widget button on
    private static int TXRX_OFF = R.drawable.txrx_off; // widget button off
	private NotificationManager manager = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		toggleState(false);
	}

	private boolean isActive = false;
	public int onStartCommand(Intent intent, int flags, int startId) {
		isActive = !isActive; // change between on and off states
		toggleState(isActive);
		return super.onStartCommand(intent, flags, startId);
	}

	private HashMap<TrafficType, NotifyInfo> previousInfo = new HashMap<TrafficType, NotifyInfo>();
	private TrafficType currentType = TrafficType.RX;
	
	private NotifyInfo getNotificationInfo() {
		currentType = (currentType == TrafficType.RX ? TrafficType.TX : TrafficType.RX);
		NotifyInfo info = new NotifyInfo(previousInfo.get(currentType), currentType, TrafficChannel.TOTAL);
		previousInfo.put(currentType, info);
		return info;
		
	}

	private CharSequence previousNotifyText;
	private void notifyStatus(NotifyInfo info) {
		CharSequence text = info.getText(DELAY);
		
		// skip unchanged notifies
		if(previousNotifyText != null && previousNotifyText.equals(text)) {
			manager.cancel(Constants.NOTIFICATION_ID);
		}
		else {	
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
			Notification notification = new Notification(info.getIcon(), text, System.currentTimeMillis());
			notification.setLatestEventInfo(this, getText(R.string.txrx_notifytext), text, contentIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			
			manager.notify(Constants.NOTIFICATION_ID, notification);
		}
		previousNotifyText = text;
	}

	private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
    	public void run() {
    		notifyStatus( getNotificationInfo() );
	    	handler.postDelayed(this, DELAY);
    	}
    };
    
    private long startTx;
    private long startRx;
    
    private void toggleState(boolean state) {
		handler.removeCallbacks(runnable);

		RemoteViews views = new RemoteViews(getPackageName(), R.layout.notifywidget);
		if(state == true) {
			views.setInt(R.id.txrx, "setImageResource", TXRX_ON);
	        handler.postDelayed(runnable, DELAY);
	        // Tell the user we stopped.
	        Toast.makeText(this, getText(R.string.txrx_started), Toast.LENGTH_SHORT).show();	
	        
	        startTx = TrafficStats.getTotalTxBytes();
	        startRx = TrafficStats.getTotalRxBytes();
		}
		else {
			views.setInt(R.id.txrx, "setImageResource", TXRX_OFF);
	        manager.cancel(Constants.NOTIFICATION_ID);
	        
	        long currentTx = TrafficStats.getTotalTxBytes()-startTx;
	        long currentRx = TrafficStats.getTotalRxBytes()-startRx;
	        
	        String txt = getText(R.string.txrx_stopped)+".\n\nTotal transmitted:\nTX:"+formatBytes(currentTx)+"\nRX: "+formatBytes(currentRx);    	
	        
	        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();		
		}

		ComponentName thisWidget = new ComponentName(this, NotifyWidgetProvider.class);
        AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);

	}
    private String formatBytes(long bytes) {
	    DecimalFormat df = new DecimalFormat(NotifyInfo.DECIMAL_FORMAT);
		return String.format("%s", df.format(bytes/1000)+"kB");
	}
}