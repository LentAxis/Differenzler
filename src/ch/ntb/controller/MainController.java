package ch.ntb.controller;

import ch.ntb.client.Client;
import ch.ntb.gui.SceneChanger;
import ch.ntb.gui.SceneChanger.View;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainController implements Initializable {

    /*
    Datafields
     */
    private Client client;
    private SceneChanger view;
    private Stage stage;
    private final int[] handCards = new int[9];
    private int match = 0;
    private int lastCard = -1;
    private int currentPlayer = 0;

    /*
    FXML Datafields
     */
    @FXML
    private Label lb_EndScore01,
            lb_EndScore02,
            lb_EndScore03,
            lb_EndScore04,
            lb_Estimate01_Round01,
            lb_Estimate02_Round01,
            lb_Estimate03_Round01,
            lb_Estimate04_Round01,
            lb_Estimate01_Round02,
            lb_Estimate02_Round02,
            lb_Estimate03_Round02,
            lb_Estimate04_Round02,
            lb_Estimate01_Round03,
            lb_Estimate02_Round03,
            lb_Estimate03_Round03,
            lb_Estimate04_Round03,
            lb_Estimate01_Round04,
            lb_Estimate02_Round04,
            lb_Estimate03_Round04,
            lb_Estimate04_Round04,
            lb_Score01_Round01,
            lb_Score02_Round01,
            lb_Score03_Round01,
            lb_Score04_Round01,
            lb_Score01_Round02,
            lb_Score02_Round02,
            lb_Score03_Round02,
            lb_Score04_Round02,
            lb_Score01_Round03,
            lb_Score02_Round03,
            lb_Score03_Round03,
            lb_Score04_Round03,
            lb_Score01_Round04,
            lb_Score02_Round04,
            lb_Score03_Round04,
            lb_Score04_Round04,
            lb_Diff01_Round01,
            lb_Diff02_Round01,
            lb_Diff03_Round01,
            lb_Diff04_Round01,
            lb_Diff01_Round02,
            lb_Diff02_Round02,
            lb_Diff03_Round02,
            lb_Diff04_Round02,
            lb_Diff01_Round03,
            lb_Diff02_Round03,
            lb_Diff03_Round03,
            lb_Diff04_Round03,
            lb_Diff01_Round04,
            lb_Diff02_Round04,
            lb_Diff03_Round04,
            lb_Diff04_Round04,
            lb_PlayerRight,
            lb_PlayerTop,
            lb_PlayerLeft,
            lb_Winner;

    @FXML
    private AnchorPane ap_Estimate,
            ap_HandCards,
            ap_Finish,
            ap_Carpet;

    @FXML
    private TextArea ta_Estimation;

    @FXML
    private ImageView iv_HandCard01,
            iv_HandCard02,
            iv_HandCard03,
            iv_HandCard04,
            iv_HandCard05,
            iv_HandCard06,
            iv_HandCard07,
            iv_HandCard08,
            iv_HandCard09,
            iv_TableCard01,
            iv_TableCard02,
            iv_TableCard03,
            iv_TableCard04,
            iv_Trump;


    /*
    Constructor
     */
    public MainController() {
    }

    /*
    Methodes
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setView(SceneChanger view) {
        this.view = view;
    }

    public void changeView(View v, double width, double height) {
        view.setCurrentView(v);
        stage.setWidth(width + 15);
        stage.setHeight(height + 40);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4);
    }

    public void init(Client client, Stage stage) {
        this.client = client;
        this.stage = stage;

        // Dropshadow Effekt
        DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.YELLOW);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(20f);
        borderGlow.setHeight(100);
        borderGlow.setWidth(0);
        borderGlow.setBlurType(BlurType.GAUSSIAN);
        borderGlow.setRadius(50);
        ap_Carpet.setEffect(borderGlow);

    }

    public void displayCard(int value, int idx) {

        // Karten werden in der im GUI angezeigten Reihenfolge zwischengespeichert.
        handCards[idx] = value;

        // Der Bildpfad wird hier zusammengesetzt und der richtigen Karte zugeteilt.
        String s;

        if (value < 10) {
            s = "/ch/ntb/gui/view/Ressourcen/0" + value + ".png";
        } else {
            s = "/ch/ntb/gui/view/Ressourcen/" + value + ".png";
        }

        Image img = new Image(s);

        switch (idx) {

            case 0:
                iv_HandCard01.setImage(img);
                iv_HandCard01.setFitWidth(0);
                iv_HandCard01.setVisible(true);
                break;

            case 1:
                iv_HandCard02.setImage(img);
                iv_HandCard02.setFitWidth(0);
                iv_HandCard02.setVisible(true);
                break;

            case 2:
                iv_HandCard03.setImage(img);
                iv_HandCard03.setFitWidth(0);
                iv_HandCard03.setVisible(true);
                break;

            case 3:
                iv_HandCard04.setImage(img);
                iv_HandCard04.setFitWidth(0);
                iv_HandCard04.setVisible(true);
                break;

            case 4:
                iv_HandCard05.setImage(img);
                iv_HandCard05.setFitWidth(0);
                iv_HandCard05.setVisible(true);
                break;

            case 5:
                iv_HandCard06.setImage(img);
                iv_HandCard06.setFitWidth(0);
                iv_HandCard06.setVisible(true);
                break;

            case 6:
                iv_HandCard07.setImage(img);
                iv_HandCard07.setFitWidth(0);
                iv_HandCard07.setVisible(true);
                break;

            case 7:
                iv_HandCard08.setImage(img);
                iv_HandCard08.setFitWidth(0);
                iv_HandCard08.setVisible(true);
                break;

            case 8:
                iv_HandCard09.setImage(img);
                iv_HandCard09.setFitWidth(0);
                iv_HandCard09.setVisible(true);
                break;
        }
    }

    public void displayTrump(int value) {
        String s = "/ch/ntb/gui/view/Ressourcen/t0" + value + ".png";
        Image img = new Image(s);
        iv_Trump.setImage(img);
    }

    public void estimate(int match) {
        ap_Estimate.setVisible(true);
        this.match = match;
    }

    public void setPlayer() {

        // Die Tischaufteilung visuell darstellen
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                int playerID = client.player.getPlayerID();

                if (playerID == 0) {
                    lb_PlayerRight.setText("Player 2");
                    lb_PlayerTop.setText("Player 3");
                    lb_PlayerLeft.setText("Player 4");
                } else if (playerID == 1) {
                    lb_PlayerRight.setText("Player 3");
                    lb_PlayerTop.setText("Player 4");
                    lb_PlayerLeft.setText("Player 1");
                } else if (playerID == 2) {
                    lb_PlayerRight.setText("Player 4");
                    lb_PlayerTop.setText("Player 1");
                    lb_PlayerLeft.setText("Player 2");
                } else if (playerID == 3) {
                    lb_PlayerRight.setText("Player 1");
                    lb_PlayerTop.setText("Player 2");
                    lb_PlayerLeft.setText("Player 3");
                } else {
                }
            }
        });
    }

    public void currentPlayer(int player) {

        // Der Spieler, welcher am Zug ist, wird mit einem Leuchteffekt angezeigt
        // Anhand der PlayerID wird der korrekte Player hervorgehoben
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                int playerID = client.player.getPlayerID();

                if (playerID == 0) {
                    switch (player) {
                        case 0:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 1:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(1);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 2:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(1);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            break;
                        case 3:
                            lb_PlayerLeft.setOpacity(1);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                    }
                } else if (playerID == 1) {
                    switch (player) {
                        case 0:
                            lb_PlayerLeft.setOpacity(1);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 1:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 2:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(1);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 3:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(1);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            break;
                    }
                } else if (playerID == 2) {
                    switch (player) {
                        case 0:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(1);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            break;
                        case 1:
                            lb_PlayerLeft.setOpacity(1);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 2:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 3:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(1);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerTop.setStyle("");
                            break;
                    }
                } else if (playerID == 3) {
                    switch (player) {
                        case 0:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(1);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 1:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(1);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            break;
                        case 2:
                            lb_PlayerLeft.setOpacity(1);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("-fx-effect: dropshadow(gaussian, rgba(240, 240, 140, .65), 100, 0.95, 0, 0);");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                        case 3:
                            lb_PlayerLeft.setOpacity(0.5);
                            lb_PlayerRight.setOpacity(0.5);
                            lb_PlayerTop.setOpacity(0.5);
                            lb_PlayerLeft.setStyle("");
                            lb_PlayerRight.setStyle("");
                            lb_PlayerTop.setStyle("");
                            break;
                    }
                } else {
                }
            }
        });
    }

    public void cardPlayed(int playerID, int value) {

        // Der Bildpfad wird hier zusammengesetzt und der richtigen gespielten Karte zugeteilt.        
        String s;

        if (value < 10) {
            s = "/ch/ntb/gui/view/Ressourcen/0" + value + ".png";
        } else {
            s = "/ch/ntb/gui/view/Ressourcen/" + value + ".png";
        }

        Image img = new Image(s);

        switch (client.player.getPlayerID()) {
            case 0:
                switch (playerID) {
                    case 0:
                        iv_TableCard01.setImage(img);
                        iv_TableCard01.setVisible(true);
                        break;
                    case 1:
                        iv_TableCard02.setImage(img);
                        iv_TableCard02.setVisible(true);
                        break;
                    case 2:
                        iv_TableCard03.setImage(img);
                        iv_TableCard03.setVisible(true);
                        break;
                    case 3:
                        iv_TableCard04.setImage(img);
                        iv_TableCard04.setVisible(true);
                        break;
                }
                break;
            case 1:
                switch (playerID) {
                    case 0:
                        iv_TableCard04.setImage(img);
                        iv_TableCard04.setVisible(true);
                        break;
                    case 1:
                        iv_TableCard01.setImage(img);
                        iv_TableCard01.setVisible(true);
                        break;
                    case 2:
                        iv_TableCard02.setImage(img);
                        iv_TableCard02.setVisible(true);
                        break;
                    case 3:
                        iv_TableCard03.setImage(img);
                        iv_TableCard03.setVisible(true);
                        break;
                }
                break;
            case 2:
                switch (playerID) {
                    case 0:
                        iv_TableCard03.setImage(img);
                        iv_TableCard03.setVisible(true);
                        break;
                    case 1:
                        iv_TableCard04.setImage(img);
                        iv_TableCard04.setVisible(true);
                        break;
                    case 2:
                        iv_TableCard01.setImage(img);
                        iv_TableCard01.setVisible(true);
                        break;
                    case 3:
                        iv_TableCard02.setImage(img);
                        iv_TableCard02.setVisible(true);
                        break;
                }
                break;
            case 3:
                switch (playerID) {
                    case 0:
                        iv_TableCard02.setImage(img);
                        iv_TableCard02.setVisible(true);
                        break;
                    case 1:
                        iv_TableCard03.setImage(img);
                        iv_TableCard03.setVisible(true);
                        break;
                    case 2:
                        iv_TableCard04.setImage(img);
                        iv_TableCard04.setVisible(true);
                        break;
                    case 3:
                        iv_TableCard01.setImage(img);
                        iv_TableCard01.setVisible(true);
                        break;
                }
                break;
        }
    }

    public void roundReset() {
        iv_TableCard01.setVisible(false);
        iv_TableCard02.setVisible(false);
        iv_TableCard03.setVisible(false);
        iv_TableCard04.setVisible(false);
    }

    public void resetDraw(int playerID) {

        iv_TableCard01.setVisible(false);

        switch (lastCard) {
            case -1:
                System.out.println("No Card played");
                break;

            case 0:
                iv_HandCard01.setFitWidth(0);
                iv_HandCard01.setVisible(true);
                break;
            case 1:
                iv_HandCard02.setFitWidth(0);
                iv_HandCard02.setVisible(true);
                break;
            case 2:
                iv_HandCard03.setFitWidth(0);
                iv_HandCard03.setVisible(true);
                break;
            case 3:
                iv_HandCard04.setFitWidth(0);
                iv_HandCard04.setVisible(true);
                break;
            case 4:
                iv_HandCard05.setFitWidth(0);
                iv_HandCard05.setVisible(true);
                break;
            case 5:
                iv_HandCard06.setFitWidth(0);
                iv_HandCard06.setVisible(true);
                break;
            case 6:
                iv_HandCard07.setFitWidth(0);
                iv_HandCard07.setVisible(true);
                break;
            case 7:
                iv_HandCard08.setFitWidth(0);
                iv_HandCard08.setVisible(true);
                break;
            case 8:
                iv_HandCard09.setFitWidth(0);
                iv_HandCard09.setVisible(true);
                break;
        }
    }

    public void enableDraw() {
        ap_HandCards.setVisible(false);
    }

    public void disableDraw() {
        ap_HandCards.setVisible(true);
    }

    public void displayEstimate(int playerID, int round, int value) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                switch (round) {
                    case 0:
                        switch (playerID) {
                            case 0:
                                lb_Estimate01_Round01.setText(Integer.toString(value));
                                break;
                            case 1:
                                lb_Estimate02_Round01.setText(Integer.toString(value));
                                break;
                            case 2:
                                lb_Estimate03_Round01.setText(Integer.toString(value));
                                break;
                            case 3:
                                lb_Estimate04_Round01.setText(Integer.toString(value));
                                break;
                        }
                        break;
                    case 1:
                        switch (playerID) {
                            case 0:
                                lb_Estimate01_Round02.setText(Integer.toString(value));
                                break;
                            case 1:
                                lb_Estimate02_Round02.setText(Integer.toString(value));
                                break;
                            case 2:
                                lb_Estimate03_Round02.setText(Integer.toString(value));
                                break;
                            case 3:
                                lb_Estimate04_Round02.setText(Integer.toString(value));
                                break;
                        }
                        break;
                    case 2:
                        switch (playerID) {
                            case 0:
                                lb_Estimate01_Round03.setText(Integer.toString(value));
                                break;
                            case 1:
                                lb_Estimate02_Round03.setText(Integer.toString(value));
                                break;
                            case 2:
                                lb_Estimate03_Round03.setText(Integer.toString(value));
                                break;
                            case 3:
                                lb_Estimate04_Round03.setText(Integer.toString(value));
                                break;
                        }
                        break;
                    case 3:
                        switch (playerID) {
                            case 0:
                                lb_Estimate01_Round04.setText(Integer.toString(value));
                                break;
                            case 1:
                                lb_Estimate02_Round04.setText(Integer.toString(value));
                                break;
                            case 2:
                                lb_Estimate03_Round04.setText(Integer.toString(value));
                                break;
                            case 3:
                                lb_Estimate04_Round04.setText(Integer.toString(value));
                                break;
                        }
                        break;
                }
            }
        });
    }

    public void displayScore(int playerID, int round, int value) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                // Aus der Schätzung und der erreichten Punkte wird die Differenz berechnet und angezeigt
                int diff;

                switch (round) {
                    case 0:
                        switch (playerID) {
                            case 0:
                                lb_Score01_Round01.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate01_Round01.getText()));
                                lb_Diff01_Round01.setText(Integer.toString(diff));
                                break;
                            case 1:
                                lb_Score02_Round01.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate02_Round01.getText()));
                                lb_Diff02_Round01.setText(Integer.toString(diff));
                                break;
                            case 2:
                                lb_Score03_Round01.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate03_Round01.getText()));
                                lb_Diff03_Round01.setText(Integer.toString(diff));
                                break;
                            case 3:
                                lb_Score04_Round01.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate04_Round01.getText()));
                                lb_Diff04_Round01.setText(Integer.toString(diff));
                                break;
                        }
                        break;
                    case 1:
                        switch (playerID) {
                            case 0:
                                lb_Score01_Round02.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate01_Round02.getText()));
                                lb_Diff01_Round02.setText(Integer.toString(diff));
                                break;
                            case 1:
                                lb_Score02_Round02.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate02_Round02.getText()));
                                lb_Diff02_Round02.setText(Integer.toString(diff));
                                break;
                            case 2:
                                lb_Score03_Round02.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate03_Round02.getText()));
                                lb_Diff03_Round02.setText(Integer.toString(diff));
                                break;
                            case 3:
                                lb_Score04_Round02.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate04_Round02.getText()));
                                lb_Diff04_Round02.setText(Integer.toString(diff));
                                break;
                        }
                        break;
                    case 2:
                        switch (playerID) {
                            case 0:
                                lb_Score01_Round03.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate01_Round03.getText()));
                                lb_Diff01_Round03.setText(Integer.toString(diff));
                                break;
                            case 1:
                                lb_Score02_Round03.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate02_Round03.getText()));
                                lb_Diff02_Round03.setText(Integer.toString(diff));
                                break;
                            case 2:
                                lb_Score03_Round03.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate03_Round03.getText()));
                                lb_Diff03_Round03.setText(Integer.toString(diff));
                                break;
                            case 3:
                                lb_Score04_Round03.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate04_Round03.getText()));
                                lb_Diff04_Round03.setText(Integer.toString(diff));
                                break;
                        }
                        break;
                    case 3:
                        switch (playerID) {
                            case 0:
                                lb_Score01_Round04.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate01_Round04.getText()));
                                lb_Diff01_Round04.setText(Integer.toString(diff));
                                break;
                            case 1:
                                lb_Score02_Round04.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate02_Round04.getText()));
                                lb_Diff02_Round04.setText(Integer.toString(diff));
                                break;
                            case 2:
                                lb_Score03_Round04.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate03_Round04.getText()));
                                lb_Diff03_Round04.setText(Integer.toString(diff));
                                break;
                            case 3:
                                lb_Score04_Round04.setText(Integer.toString(value));
                                diff = Math.abs(value - Integer.parseInt(lb_Estimate04_Round04.getText()));
                                lb_Diff04_Round04.setText(Integer.toString(diff));
                                break;
                        }
                        break;
                }
            }
        });
    }

    public void displayEndScore(int player, int[] value) {

        // Endoverlay wird angezeigt mit Gewinner 
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                lb_Winner.setText("Winner: Player " + (player + 1));
                ap_Finish.setVisible(true);

                lb_EndScore01.setText(Integer.toString(value[0]));
                lb_EndScore02.setText(Integer.toString(value[1]));
                lb_EndScore03.setText(Integer.toString(value[2]));
                lb_EndScore04.setText(Integer.toString(value[3]));
            }
        });
    }

    public void resetScoreBoard() {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                lb_Estimate01_Round01.setText("");
                lb_Estimate02_Round01.setText("");
                lb_Estimate03_Round01.setText("");
                lb_Estimate04_Round01.setText("");
                lb_Estimate01_Round02.setText("");
                lb_Estimate02_Round02.setText("");
                lb_Estimate03_Round02.setText("");
                lb_Estimate04_Round02.setText("");
                lb_Estimate01_Round03.setText("");
                lb_Estimate02_Round03.setText("");
                lb_Estimate03_Round03.setText("");
                lb_Estimate04_Round03.setText("");
                lb_Estimate01_Round04.setText("");
                lb_Estimate02_Round04.setText("");
                lb_Estimate03_Round04.setText("");
                lb_Estimate04_Round04.setText("");

                lb_Score01_Round01.setText("");
                lb_Score02_Round01.setText("");
                lb_Score03_Round01.setText("");
                lb_Score04_Round01.setText("");
                lb_Score01_Round02.setText("");
                lb_Score02_Round02.setText("");
                lb_Score03_Round02.setText("");
                lb_Score04_Round02.setText("");
                lb_Score01_Round03.setText("");
                lb_Score02_Round03.setText("");
                lb_Score03_Round03.setText("");
                lb_Score04_Round03.setText("");
                lb_Score01_Round04.setText("");
                lb_Score02_Round04.setText("");
                lb_Score03_Round04.setText("");
                lb_Score04_Round04.setText("");

                lb_Diff01_Round01.setText("");
                lb_Diff02_Round01.setText("");
                lb_Diff03_Round01.setText("");
                lb_Diff04_Round01.setText("");
                lb_Diff01_Round02.setText("");
                lb_Diff02_Round02.setText("");
                lb_Diff03_Round02.setText("");
                lb_Diff04_Round02.setText("");
                lb_Diff01_Round03.setText("");
                lb_Diff02_Round03.setText("");
                lb_Diff03_Round03.setText("");
                lb_Diff04_Round03.setText("");
                lb_Diff01_Round04.setText("");
                lb_Diff02_Round04.setText("");
                lb_Diff03_Round04.setText("");
                lb_Diff04_Round04.setText("");

                lb_EndScore01.setText("");
                lb_EndScore02.setText("");
                lb_EndScore03.setText("");
                lb_EndScore04.setText("");
            }
        });
    }

    /*
    FXML Methodes
     */
    @FXML
    void submitEstimation(ActionEvent event) {
        String s = ta_Estimation.getText();

        // Nur Zahlen im möglichen Bereich werden zugelassen (0-157)
        if (s.matches("[0-9]+")) {
            int temp = Integer.parseInt(s);
            if (temp >= 0 && temp <= 157) {
                client.Estimate(Integer.parseInt(s));
                displayEstimate(client.player.getPlayerID(), match, Integer.parseInt(s));
                ap_Estimate.setVisible(false);
            } else {
                ta_Estimation.setText("");
            }
        } else {
            ta_Estimation.setText("");
        }
    }

    @FXML
    void clickHandCard01(ActionEvent event) {
        lastCard = 0;
        iv_HandCard01.setFitWidth(1);
        iv_HandCard01.setVisible(false);
        client.DrawCard(handCards[0]);
    }

    @FXML
    void clickHandCard02(ActionEvent event) {
        lastCard = 1;
        iv_HandCard02.setFitWidth(1);
        iv_HandCard02.setVisible(false);
        client.DrawCard(handCards[1]);
    }

    @FXML
    void clickHandCard03(ActionEvent event) {
        lastCard = 2;
        iv_HandCard03.setFitWidth(1);
        iv_HandCard03.setVisible(false);
        client.DrawCard(handCards[2]);
    }

    @FXML
    void clickHandCard04(ActionEvent event) {
        lastCard = 3;
        iv_HandCard04.setFitWidth(1);
        iv_HandCard04.setVisible(false);
        client.DrawCard(handCards[3]);
    }

    @FXML
    void clickHandCard05(ActionEvent event) {
        lastCard = 4;
        iv_HandCard05.setFitWidth(1);
        iv_HandCard05.setVisible(false);
        client.DrawCard(handCards[4]);
    }

    @FXML
    void clickHandCard06(ActionEvent event) {
        lastCard = 5;
        iv_HandCard06.setFitWidth(1);
        iv_HandCard06.setVisible(false);
        client.DrawCard(handCards[5]);
    }

    @FXML
    void clickHandCard07(ActionEvent event) {
        lastCard = 6;
        iv_HandCard07.setFitWidth(1);
        iv_HandCard07.setVisible(false);
        client.DrawCard(handCards[6]);
    }

    @FXML
    void clickHandCard08(ActionEvent event) {
        lastCard = 7;
        iv_HandCard08.setFitWidth(1);
        iv_HandCard08.setVisible(false);
        client.DrawCard(handCards[7]);
    }

    @FXML
    void clickHandCard09(ActionEvent event) {
        lastCard = 8;
        iv_HandCard09.setFitWidth(1);
        iv_HandCard09.setVisible(false);
        client.DrawCard(handCards[8]);
    }

    @FXML
    void newGameFinished(ActionEvent event) {
        resetScoreBoard();
        ap_Finish.setVisible(false);
        client.NewGame();
    }
}
