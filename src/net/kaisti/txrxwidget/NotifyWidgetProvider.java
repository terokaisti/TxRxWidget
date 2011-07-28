package net.kaisti.txrxwidget;

import java.util.HashMap;

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
import android.widget.Toast;

public class NotifyWidgetProvider extends AppWidgetProvider {
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		stopService(context);
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

	/**
	 * Stop the reporting service when the widget is removed from home screen
	 */
	private void stopService(Context context) {
		Intent serviceIntent = new Intent(context, NotifyWidgetService.class);
		context.stopService(serviceIntent);
	}
	
	

}