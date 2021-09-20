package ch.ntb.client;

import ch.ntb.controller.ConnectionController;
import ch.ntb.controller.MainController;
import ch.ntb.gui.SceneChanger;
import ch.ntb.model.Player;
import ch.ntb.server.CallbackEvent;
import ch.ntb.server.ConnectionHandler;
import ch.ntb.server.Message;
import ch.ntb.server.MessageType;
import ch.ntb.server.com.google.gson.Gson;
import java.util.concurrent.TimeUnit;


public class Client implements CallbackEvent {

    public Player player = new Player();
    ConnectionController connectionCtrl;
    MainController mainCtrl;

    private static final int[] SERVER_PORT_RECEIVE = {4440, 4441, 4442, 4443};     // get from server
    private static final int SERVER_PORT_PUBLISH = 4449;                       // send to server

    private final ConnectionHandler publishConnection;
    private ConnectionHandler receiveConnection;

    private final Gson gson;

    public Client() {

        gson = new Gson();
        receiveConnection = null;
        publishConnection = new ConnectionHandler(SERVER_PORT_PUBLISH, this);   //! OUTGOING via unicast
        publishConnection.prepare(); // sender has to be prepared  
    }

    public void setController(ConnectionController cc, MainController mc) {
        mainCtrl = mc;
        connectionCtrl = cc;
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

    // Methoden mit m... werden nach Nachrichten vom Server aufgerufen
    
    private void mJoinrequest(int player) {
        this.player.setPlayerID(player);
        connectionCtrl.joinSuccess();
    }

    private void mNewGame(long data) {
        this.player.newGame();

        connectionCtrl.changeView(SceneChanger.View.MAIN, 1680, 1050);

        int cnt = 0;                            //Arrayzähler zum Karteneinlesen

        for (int i = 0; i < 36; i++) {          //Decodierung der ausgeteiltenKarten

            if ((data & 0x01) != 0) {

                if (cnt < 9) {

                    // Die Handkarten werden einzeln im Player abgespeichert
                    // Der Controller zeigt nacheinander die Karten im GUI an
                    player.setHandCards(i);
                    mainCtrl.displayCard(i, cnt);
                    cnt++;
                }
            }
            data = data >> 1;
        }
        mainCtrl.setPlayer();
    }

    private void mTrump(long data) {
        player.setTrump((int) data);
        mainCtrl.displayTrump((int) data);
    }

    private void mEstimateRequest(long data) {
        player.setMatch((int) data);
        mainCtrl.estimate(player.getMatch());
        if (player.getPlayerID() == 0) {
            mainCtrl.enableDraw();
        } else {
            mainCtrl.disableDraw();
        }
    }

    private void mDrawRequest(int player) {
        System.out.println(player);
        mainCtrl.currentPlayer(player);
        if(player == this.player.getPlayerID()){
            mainCtrl.enableDraw();
        }
    }

    private void mOpponentDraw(long data, int player) {
        if (player != this.player.getPlayerID()){
            System.err.println("Player " + player + " has drawn " + (int) data);
            this.player.cardPlayed((int) data);
        }
        mainCtrl.cardPlayed(player, (int) data);
    }

    private void mRoundwinner(long data, int player) {
        System.err.println("Table cleared");
        this.player.clearTable();
        
        // Warte vier Sekunden bevor der Tisch geräumt wird
        
        try {
                TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException ex) {}
        mainCtrl.roundReset();   
    }

    private void mRoundscore(long data, int player) {
        this.player.updateScore((int) data);
        mainCtrl.displayScore(player, this.player.getMatch(), this.player.getScore());
    }

    private void mMatchscore(long data, int player) {
        mainCtrl.displayScore(player, this.player.getMatch(), (int) data);
    }

    private void mMatchestimate(long data, int player) {
        mainCtrl.displayEstimate(player, this.player.getMatch(), (int) data);
    }

    private void mRejected(long data) {
        // 99 heisst, dass die gespielte Karte nicht regelkonform ist
        if ((int) data == 99) {
            mainCtrl.enableDraw();
            mainCtrl.resetDraw(player.getPlayerID());
            player.addLastCard();
        }
    }

    private void mFinish(long data, int player) {
        int[] differences = new int[4];
        // Aus data werden die binär codierten Werte für alle Spieler ausgelesen 
        for (int i = 0; i < 4; i++) {
            differences[i] = (int) data & 0x1FF;
            data = data >> 9;
        }
        
        mainCtrl.displayEndScore(player, differences);   
    }

    /**
     * ***********************************************************************
     */
    /**
     * ***********************************************************************
     */
    
    public void JoinGame() {

        if (player.getPlayerID() < 0) {
            boolean success = false;
            for (int i = 0; i < 4; ++i) {
                if (receiveConnection != null) {
                    receiveConnection.terminate();
                }

                receiveConnection = new ConnectionHandler(SERVER_PORT_RECEIVE[i], this);
                success = receiveConnection.testPort();

                if (success) {
                    player.setPlayerID(i);
                    receiveConnection.start();
                    break;
                }
            }

            Message msg = new Message(MessageType.JOINREQUEST, player.getPlayerID());
            publishConnection.publish(gson.toJson(msg));

        } else {
            player.setPlayerID(-1);
        }
    }

    public void NewGame() {
        Message msg = new Message(MessageType.NEWGAME, 0, player.getPlayerID());
        publishConnection.publish(gson.toJson(msg));
    }

    public void DrawCard(int idx) {
        if (player.playCard(idx)) {
            mainCtrl.disableDraw();
            Message msg = new Message(MessageType.DRAWREQUEST, idx, player.getPlayerID());
            publishConnection.publish(gson.toJson(msg));
        }
    }

    public void Estimate(int value) {
        player.setEstimation(value);
        Message msg = new Message(MessageType.ESTIMATEREQUEST, value, player.getPlayerID());
        publishConnection.publish(gson.toJson(msg));
    }

    public void QuitRequest() {
        if (player.getPlayerID() < 0) {

        } else {
            player.clearTable();// player = new Player(); als Option für reset
            mainCtrl.changeView(SceneChanger.View.CONNECTION, 400, 400);
            Message msg = new Message(MessageType.QUITREQUEST, -1, player.getPlayerID());
            publishConnection.publish(gson.toJson(msg));
        }
    }
}
