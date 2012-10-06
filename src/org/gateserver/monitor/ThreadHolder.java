package org.gateserver.monitor;

public class ThreadHolder {
	Thread t;
	DNSQuery d;
	
	public ThreadHolder(DNSQuery d) {
		this.d = d;
		this.t = new Thread( d );
	}
	
	public long getQueryTime() {
		return d.getQueryTime();
	}
}