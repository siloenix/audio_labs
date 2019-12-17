package com.siloenix;

import java.nio.ByteBuffer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.position(2);
        System.out.println(buffer.position());
        buffer.position(2);
        System.out.println(buffer.position());
    }
}
