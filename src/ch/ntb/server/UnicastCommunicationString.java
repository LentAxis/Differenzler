/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 */
package ch.ntb.server;

import java.net.*;

import javax.swing.JOptionPane;

/**
 * This class creates a unicast communication between server and client.
 * Both client and server are able to receive and transmit messages.
 *
 * @author andreas.scholz@ost.ch, Roman Bruelisauer
 *
 */
public class UnicastCommunicationString {
    private  byte[]          receiveBuffer;
    private  DatagramPacket  receivePacket;
    private  DatagramSocket  socket;

    /**
     * Creates a unicast communication.
     *
     */
    public UnicastCommunicationString() {
        receiveBuffer = new byte[1500];
        receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
    }

    /**
     * Opens a datagram socket. The port
     * will be choosen automatically.
     *
     */
    public void open() throws Exception {
        socket = new DatagramSocket ();
    }

    /**
     * Opens a datagram socket at the specific port.
     *
     * @param port port of the server
     * @throws SocketException
     */
    public void open(int port) throws SocketException {
    	socket = new DatagramSocket (port);

    }

    /**
     * Waits until a message is received.
     *
     */
    public void waitForMessage () throws Exception {
        receivePacket.setLength(receiveBuffer.length);
        socket.receive(receivePacket);
    }

    /**
     * Translates the message into a string.
     *
     * @return the message as a string
     */
    public String getMessageAsString () throws Exception {

        return new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
    }

    /**
     * Sends the specific message to the specified host and port.
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
        socket.send (packet);
    }

    /**
     * Check if socket is connected or not.
     *
     * @return true if the socket is connected
     */
    public boolean isConnected() {
    	return socket.isConnected();
    }

    /**
     * Closes the unicast socket.
     *
     */
    public void close () {
        if (socket != null)
        	socket.close();
    }
}
