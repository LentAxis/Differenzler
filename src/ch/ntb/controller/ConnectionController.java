
package ch.ntb.controller;

import ch.ntb.client.Client;
import ch.ntb.gui.SceneChanger;
import ch.ntb.gui.SceneChanger.View;
import ch.ntb.server.DifferenzlerServer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class ConnectionController implements Initializable {
    
    
    /*
    Datafields
    */
    
    private Client client;
    private DifferenzlerServer server;
    private SceneChanger view;
    private Stage stage;
    
    
    /*
    FXML Datafields
    */   


    @FXML
    private Button btn_StartServer;

    @FXML
    private TextFlow tf_StartUpInfo;

    @FXML
    private Button btn_NewGame;

    @FXML
    private Button btn_ConnectToServer;
    
    @FXML
    private ImageView iv_LobbyTitle;


    /*
    Constructor
    */
    
    public ConnectionController(){}
    
    
    /*
    Methodes
    */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {} 

    public void setView(SceneChanger view) {
        this.view = view;
    }

    public void changeView(View v, double width, double height){
        view.setCurrentView(v);
        stage.setWidth(width+15);
        stage.setHeight(height+40);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2); 
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4);  
    }
    
    public void joinSuccess(){
        // Wird nach erfolgreichem Verbinden aufgerufen
        btn_NewGame.setDisable(false);
    }
    
    public void init(Client client, Stage stage){
        this.client = client;
        this.stage = stage;
        btn_NewGame.setDisable(true);
    }
    
    /*
    FXML Methodes
    */
    
    @FXML
    void startServer(ActionEvent event) {
        String[] arguments = new String[] {};
        server.main(arguments);
        btn_StartServer.setDisable(true);
    }

    @FXML
    void connectToServer(ActionEvent event) {
        client.JoinGame();
    }

    @FXML
    void newGame(ActionEvent event) throws IOException {
        client.NewGame();
        btn_StartServer.setDisable(false);
    }   
}