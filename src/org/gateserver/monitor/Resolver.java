package org.gateserver.monitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 */
public class Resolver
{
    public static void main(String argv[]) throws IOException
    {
        Executor executor = Executors.newFixedThreadPool(3);
        ServerSocket ss = new ServerSocket( Integer.parseInt(argv[0]));
        while( true )
            executor.execute( new GateConnection( ss.accept()));
    }
}