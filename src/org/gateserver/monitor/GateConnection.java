package org.gateserver.monitor;

/**
 * Created with IntelliJ IDEA.
 * User: jnelson
 * Date: 10/6/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.me.JSONException;
import org.json.me.JSONObject;

public class GateConnection implements Runnable {
    Socket client;
    String hostname;
    String dns_server;
    String count;
    String request;

    PrintWriter pout;

    int total_threads;

    boolean printQuery = false;
    GateConnection (Socket client) throws SocketException {
        this.client = client;
    }

    public void run() {
        try {

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "8859_1" ) );
            OutputStream out = client.getOutputStream();
            pout = new PrintWriter(
                    new OutputStreamWriter(out, "8859_1"), true );
            request = in.readLine();
            System.out.println(request);

            try {
                JSONObject js = new JSONObject( request );

                hostname 	= js.get("hostname").toString();
                dns_server 	= js.get("resolver").toString();
                count 		= js.get("query_count").toString();

                total_threads = Integer.parseInt(count);
                ThreadHolder[] th = new ThreadHolder[total_threads];
                JSONObject json_response = new JSONObject();

                long totalStart = System.currentTimeMillis();

                for(int i = 0; i < total_threads; i++) {
                    th[i] = new ThreadHolder(new DNSQuery(hostname,dns_server));
                    th[i].t.start();
                }

                for(int x =0; x < total_threads; x++) {
                    try {
                        th[x].t.join();
                        json_response.put("query_time_"+x,th[x].getQueryTime());
                    }
                    catch(InterruptedException e) {
                        System.out.println("Interrupted Exception: " + e.getMessage());
                    }
                }

                pout.println(json_response.toString());

                long totalEnd = System.currentTimeMillis();
                System.out.println("total exec time: " + (totalEnd-totalStart));
                client.close();
            }
            catch(JSONException e) {
                pout.println( "400 Bad Request" );
            }
        }
        catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
            pout.println( "400 Bad Request" );
        }
    }
}