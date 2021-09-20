package ch.ntb.client;

import ch.ntb.model.Player;
import ch.ntb.model.VirtualPlayer;
import ch.ntb.server.CallbackEvent;
import ch.ntb.server.ConnectionHandler;
import ch.ntb.server.Message;
import ch.ntb.server.MessageType;
import ch.ntb.server.com.google.gson.Gson;

public class KI_Client implements CallbackEvent {

    VirtualPlayer player = new VirtualPlayer();

    private static final int[] KI_PORTS_RECEIVE = {4444, 4445, 4446, 4447};     // get from server (KI)
    private static final int SERVER_PORT_PUBLISH = 4449;                       // send to server

    private final ConnectionHandler publishConnection;
    private ConnectionHandler receiveConnection;

    private final Gson gson;

    public KI_Client(int playerID) {
        gson = new Gson();
        this.player.setPlayerID(playerID);
        receiveConnection = null;
        publishConnection = new ConnectionHandler(SERVER_PORT_PUBLISH, this);   //! OUTGOING via unicast
        publishConnection.prepare(); // sender has to be prepared  
        JoinGame();
    }

    @Override
    public synchronized void process(String msg) {
        Message m = gson.fromJson(msg, Message.class);

        switch (m.type) {
            case JOINREQUEST:
                mJoinrequest(m.player);
                break;
            case NEWGAME:
                mNewGame(m.data);
                break;
            case TRUMP:
                mTrump(m.data);
                break;
            case ESTIMATEREQUEST:
                mEstimateRequest(m.data);
                break;
            case DRAWREQUEST:
                mDrawRequest(m.player);
                break;
            case OPPONENTDRAW:
                mOpponentDraw(m.data, m.player);
                break;
            case ROUNDWINNER:
                mRoundwinner(m.data, m.player);
                break;
            case ROUNDSCORE:
                mRoundscore(m.data, m.player);
                break;
            case MATCHSCORE:
                mMatchscore(m.data, m.player);
                break;
            case MATCHESTIMATE:
                mMatchestimate(m.data, m.player);
                break;
            case REJECTED:
                mRejected(m.data);
                break;
            case FINISH:
                mFinish(m.data, m.player);
                break;
        }
    }

    private void mJoinrequest(int player) {

    }

    private void mNewGame(long data) {
        this.player.newGame();

        int cnt = 0;                            //ArrayzÃ¤hler zum Karteneinlesen

        for (int i = 0; i < 36; i++) {          //Decodierung der ausgeteiltenKarten

            if ((data & 0x01) != 0) {

                if (cnt < 9) {

                    player.setHandCards(i);
                    cnt++;
                }
            }
            data = data >> 1;
        }

    }

    private void mTrump(long data) {
        player.setTrump((int) data);
    }

    private void mEstimateRequest(long data) {
        player.estimate();
        Estimate(player.getEstimation());
    }

    private void mDrawRequest(int player) {
        // Karte wird von Virtualplayer angefordert
        if (this.player.getPlayerID() == player) {
            int card = this.player.playCard();
            if (card == -1){
                System.err.println("KI hat keine Karte zurück gegeben");
            }
            System.err.println("Player " + this.player.getPlayerID() + " draws " + card);
            DrawCard(card);
        }
    }

    private void mOpponentDraw(long data, int player) {
        if (Player.cardsOnTable.isEmpty()){// nur ein workaround!!
            this.player.cardPlayed((int) data);
        }
    }

    private void mRoundwinner(long data, int player) {

    }

    private void mRoundscore(long data, int player) {
        if (this.player.getPlayerID() == player) {
            this.player.updateScore((int) data);
        }
    }

    private void mMatchestimate(long data, int player) {

    }

    private void mRejected(long data) {

    }

    private void mFinish(long data, int player) {

    }

    private void mMatchscore(long data, int player) {
    }

    /**
     * ***********************************************************************
     */
    /**
     * ***********************************************************************
     */
    public void JoinGame() {
        if ((this.player.getPlayerID() > 0) && (this.player.getPlayerID() < 4)){
            boolean success = false;
                if(receiveConnection != null){
                    receiveConnection.terminate();
                }

                receiveConnection = new ConnectionHandler(KI_PORTS_RECEIVE[this.player.getPlayerID()], this);  
                success = receiveConnection.testPort();

                if(success){
                    System.out.println("connection established on port "+KI_PORTS_RECEIVE[this.player.getPlayerID()]);
                    receiveConnection.start();
                }

            Message msg = new Message(MessageType.KI_REQUEST, this.player.getPlayerID());
            publishConnection.publish(gson.toJson(msg));
        
        }else{
            System.out.println("Sie sind bereits als KI angemeldet.");
        }
    }

    public void DrawCard(int idx) {
        Message msg = new Message(MessageType.DRAWREQUEST, idx, player.getPlayerID());
        publishConnection.publish(gson.toJson(msg));
    }

    public void Estimate(int value) {
        Message msg = new Message(MessageType.ESTIMATEREQUEST, value, player.getPlayerID());
        publishConnection.publish(gson.toJson(msg));
    }
}
