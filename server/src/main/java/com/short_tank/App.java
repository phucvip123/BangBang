package com.short_tank;

import com.short_tank.server.GameServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        GameServer server = new GameServer();
        server.start();
    }
}
