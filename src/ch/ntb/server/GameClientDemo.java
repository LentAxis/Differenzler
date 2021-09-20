/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 * 
 */
package ch.ntb.server;

import ch.ntb.server.com.google.gson.Gson;

/**
 *
 * @author andreas.scholz@ost.ch
 */
public class GameClientDemo implements CallbackEvent{
    private final int[] cards = new int [9];
 
    private static final int[] SERVER_PORT_RECEIVE   = {4440,4441,4442,4443};     // get from server
    private static final int SERVER_PORT_PUBLISH     = 4449;                       // send to server
    private int boundPort = -1;
    private int playerID = -1;
    
    private final ConnectionHandler publishConnection;
    private ConnectionHandler receiveConnection;
    
    private final Gson gson;
    
    /**
     * Creates new form GameClient
     */
    public GameClientDemo(){
        //initComponents();
        
        gson = new Gson(); 
        receiveConnection = null;
        
        publishConnection = new ConnectionHandler(SERVER_PORT_PUBLISH, this);   //! OUTGOING via unicast
        publishConnection.prepare(); // sender has to be prepared
  
    }

    // Example of the implementation of the callback process
    @Override
    public synchronized void  process(String msg){
        Message m = gson.fromJson(msg, Message.class);
        
        switch(m.type){
            case JOINREQUEST: cbJoinrequest(m.player); break;
        }
    }

    // Example of processing a callback answer from a join request
    private void cbJoinrequest(int player){
    
        // if connection was successful, a player ID 0-3 arrives, else -1 for fail
        playerID = player;
        
        if(playerID < 0){
            System.out.println("Es sind keine freien Plätze verfügbar");
        }else{
            System.out.println("Sie sind Spieler "+(playerID+1));
        }
        
    }
    
    // Example of a join request 
    private void JoinGame() {                                                
        
        if (playerID < 0){
            boolean success = false;
            for(int i = 0; i < 4; ++i){
                if(receiveConnection != null){
                    receiveConnection.terminate();
                }

                receiveConnection = new ConnectionHandler(SERVER_PORT_RECEIVE[i], this);  
                success = receiveConnection.testPort();

                if(success){
                    boundPort = i;
                    System.out.println("connection established on port "+SERVER_PORT_RECEIVE[i]);
                    receiveConnection.start();
                    break;
                }
            }

            Message msg = new Message(MessageType.JOINREQUEST, boundPort);
            publishConnection.publish(gson.toJson(msg));
        
        }else{
            System.out.println("Sie sind bereits als Spieler " + (playerID+1) + " angemeldet.");
        }
    }  
}
