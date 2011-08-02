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
	private long previous;

	public static final String DECIMAL_FORMAT = "#.##";
	private static final int ARROW_UP = R.drawable.arrow_up;
	private static final int ARROW_DOWN = R.drawable.arrow_down;

	public NotifyInfo(NotifyInfo previousInfo, TrafficType type, TrafficChannel channel) {
		if(previousInfo == null) {
			previousInfo = new NotifyInfo();
		}

		this.previousInfo = previousInfo;

		setChannel(channel);
		setType(type);

		calcDifference();
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
    	
    	long now = previousInfo.previous > 0 ? current - previousInfo.previous : 0;
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
    /**
     * 
     * @param delay Delay between requests
     * @return
     */
    public String getText(long delay) {
    	DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    	
		String txt = String.format("%s", df.format(difference/delay/TrafficType.values().length)+"kB/s");
		return txt;
    }

    public boolean hasChanged() {
    	return difference > 0;
    }
}
