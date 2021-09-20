/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 * Spielregeln: siehe GameEngine.
 */
package ch.ntb.server;

import ch.ntb.server.com.google.gson.Gson;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;
import ch.ntb.client.KI_Client;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andreas.scholz@ost.ch
 */
public class DifferenzlerServer extends javax.swing.JFrame implements CallbackEvent{

    /**
     * Tasks:
     *  - accept/reject new players
     *  - start new/delete game instance 
     *  - generate AI player to fill empty seats
     *  - keep track of current game state
     *  - send game player-related game state to clients (excluding data from other players)
     */
    
    private final int[] playersConnected = {-1, -1, -1, -1};
    private final int[] kiConnected     = {-1, -1, -1, -1};
    private final int maximumPlayers = 4;
    
    // To prevent KI blocking the ports for humans to join, they use a different port set
    private static final int[] SERVER_PORTS_PUBLISH   = {4440,4441,4442,4443};     // send to client (Humans)
    private static final int[] KI_PORTS_PUBLISH       = {4444,4445,4446,4447};     // send to client (KI)
    private static final int   SERVER_PORT_GAME        = 4449;      // recieve from clients
    
    private final ConnectionHandler clientConnection;
    private final ConnectionHandler publishConnection;
    private final ConnectionHandler kiConnection;   
    
    
    private final GameEngine gameEngine;
    private final Gson gson;
    
    /**
     * Creates new form DifferenzlerServer
     */
    public DifferenzlerServer() {
        initComponents();

        clientConnection = new ConnectionHandler(SERVER_PORT_GAME, this);       // INCOMING via unicast
        publishConnection = new ConnectionHandler(SERVER_PORTS_PUBLISH, this);  // OUTGOING via unicast
        kiConnection = new ConnectionHandler(KI_PORTS_PUBLISH, this);       // OUTGOING via unicast
        
        publishConnection.prepare();    // sender has to be prepared
        kiConnection.prepare();
        
        clientConnection.start();       // reciever has to be started
        
        gson = new Gson(); 
        gameEngine   = new GameEngine();
    }
    
    @Override
    public synchronized void  process(String msg){
        Message m = gson.fromJson(msg, Message.class);
        
        switch(m.type){
            case JOINREQUEST: cbJoinrequest(m.data); break;
            case NEWGAME: cbNewgame(m.data, m.player); break;
            case QUITREQUEST: cbQuitrequest(m.data, m.player); break;
            case KI_REQUEST: cbKi_request(m.data); break;
            case JOINED: cbJoined(m.player); break; 
            
            case ESTIMATEREQUEST: cbEstimaterequest(m.data, m.player); break;
            case DRAWREQUEST: cbDrawrequest(m.data, m.player); break;
            case OPPONENTDRAW: cbOpponentdraw(m.data); break;
            case ROUNDWINNER: cbRoundwinner(m.data); break;
            case ROUNDSCORE: cbRoundscore(m.data); break;
            
            case MATCHWINNER: cbScore(m.data); break;
            case MATCHSCORE: cbScore(m.data); break;
            case MATCHESTIMATE: cbEstimate(m.data); break;
            
            case TRUMP: cbTrump(m.data); break;
            case FINISH: cbFinish(m.data); break;
            case REJECTED: cbRejected(m.data); break;
            
        }

    }

    private void cbJoinrequest(long data){
    
        for(int i = 0; i < maximumPlayers; ++i){
            if(playersConnected[i] == -1){
                playersConnected[i] = (int)data;
                kiConnected[i] = -1;    // if a ki was connected, disconnect

                
                Message msg = new Message(MessageType.JOINREQUEST, 0, i);
                publishConnection.publish(gson.toJson(msg), i);
                logging("Spieler " + (i+1) + " beigetreten @ Port "+ SERVER_PORTS_PUBLISH[(int)data]);
                
                for(int j = 0; j < maximumPlayers; ++j){
                    msg = new Message(MessageType.JOINED, data);
                    publish(j, msg);
                }
                return;
            }
        }
        Message msg = new Message(MessageType.JOINREQUEST, -1);
        publishConnection.publish(gson.toJson(msg), (int)data);
    }
    
    private void cbNewgame(long data, int player){
        int playersInRound = 0;
        for(int i = 0; i < maximumPlayers; ++i){
            if(playersConnected[i] > -1) playersInRound++;
            if(kiConnected[i] > -1) playersInRound++;
        }
        
        while(playersInRound < 4){  // fülle mit KI Clients auf wenn ein Spiel gestartet wird
            kiConnected[playersInRound] = playersInRound;
            new KI_Client(playersInRound); // playerID wird dem KI mitgegeben
            playersInRound++;
        }
        
        if(gameEngine.getState() == 0){
            if(player > 0) logging("Spieler " + (player+1) + " startet neues Spiel");
            gameEngine.createNewGame();
            
            
            // send all players their 9 cards
            for(int i = 0; i < maximumPlayers; ++i){
                if(playersConnected[i] > -1){
                    
                    Message msg = new Message(MessageType.NEWGAME, gameEngine.getPlayerCards(i), i);
                    publishConnection.publish(gson.toJson(msg), i);
                    
                }else if(kiConnected[i] > -1){
                    Message msg = new Message(MessageType.NEWGAME, gameEngine.getPlayerCards(i), i);
                    kiConnection.publish(gson.toJson(msg), i);
                    
                }
            }
            
            gameEngine.setState(2);
            int t = gameEngine.getTrump();
            for(int i = 0; i < maximumPlayers; ++i){
                Message msg = new Message(MessageType.TRUMP, t);
                publish(i, msg);
            }
            
            // now request from everyone an estimate
            gameEngine.setState(3);
            for(int i = 0; i < maximumPlayers; ++i){
                
                Message msg = new Message(MessageType.ESTIMATEREQUEST, gameEngine.getMatch());
                publish(i, msg);
            }
            

        }else{
            System.out.println("Ein Spiel läuft bereits.");
            Message msg = new Message(MessageType.REJECTED, gameEngine.getState());
            publish(player, msg);
        }
    }
    
    
    
    private void cbQuitrequest(long data, int player){ 
        Message msg = new Message(MessageType.QUITREQUEST, data);
        publishConnection.publish(gson.toJson(msg), playersConnected[player]);
        playersConnected[player] = -1;
        logging("Spieler " + (player+1) + " verlassen @ Port "+ SERVER_PORTS_PUBLISH[player]);
    }
    
    
    private void cbEstimaterequest(long data, int player){
        if(gameEngine.getState() != 3){
            Message msg = new Message(MessageType.REJECTED, gameEngine.getState());
            publish(player, msg);
        }
        
        gameEngine.setEstimate(data, player); 
        logging("Spieler "+player+" hat geschätzt: "+ (int)data);
        // if everyone has given an estimate, the round can begin with tha draw of the first player.
        if(gameEngine.checkEstimates()){
            gameEngine.setState(4);
            
            for(int i = 0; i < maximumPlayers; ++i){
                
                Message msg = new Message(MessageType.DRAWREQUEST, 0, gameEngine.getCurrentPlayer());
                publish(i, msg);
            }
        }
    }
    
    private void cbDrawrequest(long data, int player){
        if(gameEngine.getState() != 4){
            Message msg = new Message(MessageType.REJECTED, gameEngine.getState());
            publish(player, msg);
        }
        
        if(gameEngine.isDrawValid(data, player)){
        
            gameEngine.setDraw(data, player);
            logging("Spieler "+player+" hat Karte " + (int)data + " gesetzt");

            // forward current draw of player to all others
            for(int i = 0; i < maximumPlayers; ++i){
                Message msg = new Message(MessageType.OPPONENTDRAW, data, player);
                publish(i, msg);
            }
        }else{
            Message msg = new Message(MessageType.REJECTED, 99);
            publish(player, msg);
        }
        
        
        // if all did draw a card, check for round winner 
        if(gameEngine.checkDraws()){
            gameEngine.setState(4);
             
            gameEngine.calculateRoundResult();
                    
            // inform anyone about the round winner (visualize the trick)
            for(int i = 0; i < maximumPlayers; ++i){
                Message msg = new Message(MessageType.ROUNDWINNER, 0, gameEngine.getRoundwinner());
                publish(i, msg);
            }
            
            // The Winner also gehts his score for the trick
            {
            Message msg = new Message(MessageType.ROUNDSCORE, gameEngine.getRoundScore(), gameEngine.getRoundwinner());
            publish(gameEngine.getRoundwinner(), msg);
            }
         
            // if there are still cards to play, reset cards of table and start over with first player who got last trick.

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
            }
            
            if(gameEngine.getcardsLeft() > 0){
                gameEngine.resetRound();
                gameEngine.setState(4);
                
                for(int i = 0; i < maximumPlayers; ++i){
                    Message msg = new Message(MessageType.DRAWREQUEST, 0, gameEngine.getCurrentPlayer());
                    publish(i, msg);
                }
            }else{
                gameEngine.calculateMatchResult();
                gameEngine.calculateGameResult();
                
                // we are done with a match
                // send every player the score and estimate of every player
                for(int i = 0; i < maximumPlayers; ++i){
                    for(int j = 0; j < maximumPlayers; ++j){
                        Message msg = new Message(MessageType.MATCHESTIMATE, gameEngine.getEstimate(i), i);
                        publish(j, msg);
                    }
                }
                
                for(int i = 0; i < maximumPlayers; ++i){
                    for(int j = 0; j < maximumPlayers; ++j){
                        Message msg = new Message(MessageType.MATCHSCORE, gameEngine.getScore(i), i);
                        publish(j, msg);
                    }
                }
                

                
                if(gameEngine.isFinished()){
                    
                    int winner = gameEngine.getGameWinner();
                    // it was the last match, so we declare a winner
                    
                    logging("Spiel beendet");
                    logging("Gamewinner is Player " + winner);
                    logging("-----------------------------");
                    for(int i = 0; i < maximumPlayers; ++i)
                    {
                        logging("Player " + i + "   Differenz: " + gameEngine.getGameDifference(i));
                    }
                    logging("-----------------------------");
                    long encodedDifference = encodeDifferences();
                    
                    for(int i = 0; i < maximumPlayers; ++i){
                        Message msg = new Message(MessageType.FINISH, encodedDifference, winner);
                        publish(i, msg);
                    }
                    gameEngine.resetGame();
                }else{
                    // still matches to play
                    logging("Neues Match");
                    gameEngine.resetMatch();
                    gameEngine.setNextMatch();
                    cbNewgame(0, -1);
                }
            }

        }else{
            for(int i = 0; i < maximumPlayers; ++i){
                Message msg = new Message(MessageType.DRAWREQUEST, 0, gameEngine.getCurrentPlayer());
                publish(i, msg);
            }
        }
 
    }
    
    private void cbRoundwinner(long data){}     // this cb is not expected
    private void cbOpponentdraw(long data){}    // this cb is not expected
    private void cbScore(long data){}           // this cb is not expected
    private void cbTrump(long data){}           // this cb is not expected
    private void cbFinish(long data){}          // this cb is not expected
    private void cbEstimate(long data){}        // this cb is not expected
    private void cbJoined(int player){}         // this cb is not expected
    private void cbRoundscore(long data){}     // this cb is not expected
    private void cbRejected(long data){}       // this cb is not expected
    
    private void cbKi_request(long data){
        for(int i = 0; i < maximumPlayers; ++i){
            if(playersConnected[i] == -1 && kiConnected[i] == -1){
                
                Message msg = new Message(MessageType.JOINREQUEST, 0, i);
                kiConnection.publish(gson.toJson(msg), i);
                logging("KI " + (i+1) + " beigetreten @ Port "+ KI_PORTS_PUBLISH[i]);
                return;
            }
        }
//        Message msg = new Message(MessageType.JOINREQUEST, -1);
//        publishConnection.publish(gson.toJson(msg), (int)data);
    }
    
    
    private void logging(String msg){
        dataTextArea.append(msg+"\n");
	dataTextArea.setCaretPosition(dataTextArea.getDocument().getLength());                          
    }
    
    private void publish(int idx, Message msg){
        if(playersConnected[idx] > -1){
            publishConnection.publish(gson.toJson(msg), idx);
        }else if(kiConnected[idx] > -1){
            kiConnection.publish(gson.toJson(msg), idx);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textField = new javax.swing.JScrollPane();
        dataTextArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mShutDown = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        dataTextArea.setEditable(false);
        dataTextArea.setColumns(20);
        dataTextArea.setRows(5);
        textField.setViewportView(dataTextArea);

        jMenu1.setText("Server");

        mShutDown.setText("Shut down");
        mShutDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mShutDownActionPerformed(evt);
            }
        });
        jMenu1.add(mShutDown);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textField, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textField, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mShutDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mShutDownActionPerformed
        // TODO add your handling code here:
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_mShutDownActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(
            //UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.getSystemLookAndFeelClassName());
            /*
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            */
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DifferenzlerServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DifferenzlerServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DifferenzlerServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DifferenzlerServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DifferenzlerServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea dataTextArea;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem mShutDown;
    private javax.swing.JScrollPane textField;
    // End of variables declaration//GEN-END:variables


    
    /**
    *
    * @author Benjamin
    */
    // encodes the Differences for all players in a long to send a message
    // Bit  0..8    Difference player 0 as a binary number
    // Bit  9..17   Difference player 1 as a binary number
    // Bit 18..26   Difference player 2 as a binary number
    // Bit 27..35   Difference player 3 as a binary number
    private long encodeDifferences()
    {
        long data = 0;        
        
        data = data | gameEngine.getGameDifference(3);
        data = data << 9;
        data = data | gameEngine.getGameDifference(2);
        data = data << 9;
        data = data | gameEngine.getGameDifference(1);
        data = data << 9;
        data = data | gameEngine.getGameDifference(0);      
        
        return data;
    }




}
