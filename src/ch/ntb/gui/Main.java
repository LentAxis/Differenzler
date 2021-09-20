
package ch.ntb.gui;

import ch.ntb.client.Client;
import ch.ntb.controller.ConnectionController;
import ch.ntb.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

// Einstiegsklasse für die Application

public class Main extends Application{
  
    Client client = new Client();
    
    @Override
    public void start(Stage stage) throws Exception {
        
        // Die beiden möglichen Scenen werden vorgeladen und einem Zustand im Scenechanger zugeteilt.
        // Jeder Scene wird ein Controller zugeteilt.
        SceneChanger view = new SceneChanger(); 

        // Main Scene
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("view/Main.fxml"));
        Parent main = mainLoader.load();
        MainController mainCtrl = mainLoader.getController();
        mainCtrl.setView(view);
        mainCtrl.init(client, stage);

        // Connection Scene
        FXMLLoader connectionLoader = new FXMLLoader(getClass().getResource("view/Connection.fxml"));
        Parent connection = connectionLoader.load();
        ConnectionController connectionCtrl = connectionLoader.getController();
        connectionCtrl.setView(view);
        connectionCtrl.init(client, stage);

        client.setController(connectionCtrl, mainCtrl);
        
        Scene scene = new Scene(main);

        scene.rootProperty().bind(Bindings.createObjectBinding(() -> {
            if (view.getCurrentView() == SceneChanger.View.MAIN) {
                return main ;
            } else if (view.getCurrentView() == SceneChanger.View.CONNECTION) {
                return connection ;
            } else {
                return null ;
            }
        }, view.currentViewProperty()));
        
        // Eventhandler für Fenster schliessen
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.setScene(scene);
        stage.show();
    }
    
    
    
    
    public static void main(String[] args) { launch(args); }
}
