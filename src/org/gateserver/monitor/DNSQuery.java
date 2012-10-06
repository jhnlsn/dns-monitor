package org.gateserver.monitor;

import java.io.IOException;
import java.net.UnknownHostException;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSQuery implements Runnable{
	
	SimpleResolver resolver = null;
	
	String hostname;
	String dns_server;
	
	Name name = null;
	
	Record rec;
	
	Message query;
	Message response;
	
	long startTime;
	long endTime;
	long queryTime;
	
	boolean debug = false;
	
	public long getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(long queryTime) {
		this.queryTime = queryTime;
	}

	public void run() {
		
		LoadSimpleResolver();
    	
    	try {
    		name 	= Name.fromString(hostname, Name.root);
        	rec 	= Record.newRecord(name, Type.A, DClass.IN);
    	}
    	catch(TextParseException e) {
    		System.out.println("Text parse exception: " + e.getMessage());
    	}

    	query = Message.newQuery(rec);
    	
    	if(debug) {
    		System.out.println(query);
    	}
    	
		startTime = System.currentTimeMillis();
		
		try {
			response = resolver.send(query);
	    	endTime = System.currentTimeMillis();
	    	setQueryTime( (endTime-startTime) );
		}
    	catch(IOException e) {
    		System.out.println("Host Unreachable: " + dns_server);
    		setQueryTime(-1);
    		endTime = System.currentTimeMillis();
    	}
    	
    	if(debug) {
    		System.out.println(dns_server + " Query time: " + (endTime-startTime) + " ms");
    	}
	}
	
	DNSQuery(String hostname, String resolver) {
		this.hostname = hostname;
		this.dns_server = resolver;
	}
	
	private void LoadSimpleResolver() {
		try { 
			resolver = new SimpleResolver(dns_server);
			resolver.setTimeout(1);
    	}
    	catch(UnknownHostException e) {
    		System.out.println("Unknown host exception" + e.getMessage());
    	}
	}
}
