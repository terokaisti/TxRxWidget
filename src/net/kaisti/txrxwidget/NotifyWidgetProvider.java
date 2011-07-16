package net.kaisti.txrxwidget;

import net.kaisti.txrxwidget.NotifyInfo.TrafficType;
import net.kaisti.txrxwidget.NotifyInfo.TrafficChannel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

public class NotifyWidgetProvider extends AppWidgetProvider {
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, NotifyWidgetService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notifywidget);
            views.setOnClickPendingIntent(R.id.txrx, pendingIntent);
            
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
	}

	public static class NotifyWidgetService extends Service{
		protected static final long DELAY = 2000; // notification updates every two seconds
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
			manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		public int onStartCommand(Intent intent, int flags, int startId) {
			toggleState();			
			return super.onStartCommand(intent, flags, startId);
		}

		private boolean isActive = false;
		private boolean isRx = false;
		private NotifyInfo previousInfoRx;
		private NotifyInfo previousInfoTx;
		
		private NotifyInfo getNotificationInfo() {
			NotifyInfo nInfo;
			if(isRx == true) {
				nInfo = new NotifyInfo(previousInfoRx);
				nInfo.setType(TrafficType.RX);
				previousInfoRx = nInfo;
			}
			else {
				nInfo = new NotifyInfo(previousInfoTx);
				nInfo.setType(TrafficType.TX);
				previousInfoTx = nInfo;
			}
			isRx = !isRx;
			
			return nInfo;
		}

		private void notifyStatus() {
			long when = System.currentTimeMillis();
			    
			NotifyInfo nInfo = getNotificationInfo();
			TrafficChannel ch = TrafficChannel.TOTAL;  
			nInfo.setChannel(ch);
					
			CharSequence text = nInfo.getText(DELAY);
			CharSequence contentText = text;
			int icon = nInfo.getIcon();
			CharSequence contentTitle = "TxRx Widget running...";
			
			Intent intent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

			Notification notification = new Notification(icon, text, when);

			notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
			manager.notify(Constants.NOTIFICATION_ID, notification);
		}

		private Handler handler = new Handler();
	    private Runnable runnable = new Runnable() {
	    	public void run() {
	    		notifyStatus();
		    	handler.postDelayed(this, DELAY);
	    	}
	    };

	    private void toggleState() {
			isActive = !isActive;
			
			handler.removeCallbacks(runnable);
			int txrx = R.drawable.txrx_off;

			if(isActive == true) {
		        handler.postDelayed(runnable, DELAY);
		        txrx = R.drawable.txrx_on;
			}
			else {
		        manager.cancel(Constants.NOTIFICATION_ID);
		        /*
		        previousInfoRx = null;
		        previousInfoTx = null;
		        */
			}

			RemoteViews views = new RemoteViews(getPackageName(), R.layout.notifywidget);
			views.setInt(R.id.txrx, "setImageResource", txrx);
			ComponentName thisWidget = new ComponentName(this, NotifyWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, views);

		}
	}
}