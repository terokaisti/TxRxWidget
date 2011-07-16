package net.kaisti.txrxwidget;

import java.text.DecimalFormat;

import android.net.TrafficStats;

public class NotifyInfo {
	
	private NotifyInfo previousInfo;

	public enum TrafficType {
		TX, RX
	}
	public enum TrafficChannel {
		MOBILE, TOTAL
	}
	
	public TrafficType type;
	public TrafficChannel channel;
	
	private float difference;
	private long previous = 0L;

	private static final String DECIMAL_FORMAT = "#.##";
	private static final int ARROW_UP = R.drawable.arrow_up;
	private static final int ARROW_DOWN = R.drawable.arrow_down;

	public NotifyInfo(NotifyInfo previous) {
		if(previous == null) {
			previous = new NotifyInfo();
		}
		this.previousInfo = previous;
		setType(previous.type);
		setChannel(previous.channel);
	}
	/*
	 * Start with TX
	 */
	private NotifyInfo() {
		this.type = TrafficType.TX;
		this.channel = TrafficChannel.TOTAL;
	}
	
	public void setType(TrafficType type) {
		this.type = type;
	}
	
	public void setChannel(TrafficChannel ch) {
		this.channel = ch;
	}

	public int getIcon() {
		if(this.type == TrafficType.RX) {
			return ARROW_DOWN;
		}	
		return ARROW_UP;
	}

    private Float getDifference(long current) {
    	if(previousInfo == null)
	    	return (float) 0;
    	
    	long now = previousInfo.previous > 0L ? current - previousInfo.previous : 0L;
    	previous = current;
    	return new Long(now).floatValue();
    }

    private void calcDifference() {
		long bytes = 0;
		switch(type) {
		case TX:
			bytes = channel.equals(TrafficChannel.MOBILE) ? TrafficStats.getMobileTxBytes() : TrafficStats.getTotalTxBytes();
			break;
		case RX:
			bytes = channel.equals(TrafficChannel.MOBILE) ? TrafficStats.getMobileRxBytes() : TrafficStats.getTotalRxBytes();
			break;
		}
		difference = getDifference(bytes);
    }
    
    public String getText(long delay) {
    	calcDifference();
    	DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    	
    	// the last /2 comes from TX/RX
		String txt = String.format("%s", df.format(difference/delay/2)+"kB/s");
		return txt;
    }

}
