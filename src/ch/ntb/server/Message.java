/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 */
package ch.ntb.server;

/**
 *
 * @author andreas.scholz@ost.ch
 */
public class Message {
    public MessageType type;
    public long data;
    public int player;
    
    public Message(MessageType t, long d){
        type = t;
        data = d;
        player = -1;
    }
    
    public Message(MessageType t, long d, int p){
        type = t;
        data = d;
        player = p;
    }
}

