/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 */
package ch.ntb.server;

import java.io.*;
import java.net.*;

/**
 * This class creates a broadcast communication between server and client.
 * The server sends data message to all clients, which are joining this
 * group.
 *
 * @author andreas.scholz@ost.ch, Roman Bruelisauer
 *
 */

//Information about broadcast communication:
//https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/networking/datagrams/examples/MulticastClient.java
//https://docs.oracle.com/javase/tutorial/networking/datagrams/broadcasting.html

public class BroadcastCommunicationString {
    private byte[]          receiveBuffer;
    private DatagramPacket  receivePacket;
    private MulticastSocket socket;
    private InetAddress 	address;

    /**
     * Creates a broadcast communication with the address
     * of the specific host.
     *
     * @param host host of the server
     */
    public BroadcastCommunicationString(String host) throws UnknownHostException {
        receiveBuffer = new byte[1500];
        receivePacket = new DatagramPacket (receiveBuffer, receiveBuffer.length);
        address = InetAddress.getByName(host);
    }

    /**
     * Opens a multicast socket at the specific port and
     * joins the group of broadcast server.
     *
     * @param port port of the server
     */
    public void open(int port) throws Exception {
    	socket = new MulticastSocket(port);
    	socket.joinGroup(address);
    	socket.setTimeToLive(1);
    }

    /**
     * Waits until a message is received.
     *
     */
    public void waitForMessage () throws Exception {
    	socket.receive (receivePacket);
    }

    /**
     * Translates the message into a string by reading
     * the received byte array
     *
     * @return the message as a string
     */
    public String getMessageAsString() throws Exception {

        return new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
    }


    /**
     * Sends the specific message as a byte array
     * to the specified host and port. Use this for sending
     * messages to the game engine. Because they cannot convert
     * objects into strings without problems.
     *
     * @param host host of the receiver
     * @param port port of the receiver
     * @param message message to be sent to the receiver
     */
    public void sendMessageAsString (String host, int port, String message) throws Exception {
        // Note: SocketAddress is a combination of address and port
    	InetSocketAddress address = new InetSocketAddress(InetAddress.getByName (host), port);

        byte[] data = message.getBytes();
    	DatagramPacket packet = new DatagramPacket(data, data.length, address);

        // send packet
        socket.send(packet);
    }

    /**
     * Client can leave the group. The server will not send
     * messages anymore to this client.
     *
     */
    public void leaveGroup() throws IOException {
    	socket.leaveGroup(address);
    }

    /**
     * Closes the multicast socket.
     *
     */
    public void close () {
        if (socket != null)
        	socket.close();
    }
}
