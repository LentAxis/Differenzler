package ch.ntb.model;

import ch.ntb.server.Card;
import ch.ntb.server.Cards;
import ch.ntb.server.Cards.*;
import java.util.ArrayList;

public class VirtualPlayer extends Player {

    private ArrayList<Integer> eichelnOnHand;   //Eicheln die die KI auf der Hand hat
    private ArrayList<Integer> rosenOnHand;     //Rosen ...
    private ArrayList<Integer> schiltenOnHand;  //Schilten ...
    private ArrayList<Integer> schellenOnHand;  //Schellen ...

    public VirtualPlayer() {
        super();
    }

    public void estimate() {// Schätze wie viele Punkte zu erzielen versucht werden und sortiere die Karten auf der Hand zuerst nach Wert dann nach Farbe 
        estimation = 0; //Beginne mit einem Wert von Null
        trumpColor = cards.getColor(trump); //Bestimme die Trumpf-Farbe anhand der Trumpf-ID
        ArrayList<Integer> sortedList = new ArrayList<>();
        for (int card : cardsOnHand) {// Für alle Karten-IDs der Karten auf der Hand...
            int currentIndex = card;
            Card currentCard = cards.getCard(card); //Bestimme die Karte anhand der Karten-ID
            int currentValue;
            if (currentCard.color == trumpColor) {// Wenn die Karte Trumpf ist, dann füge der Schätzung den Wert der Karte hinzu
                currentValue = currentCard.valueT;
                estimation += currentValue;
            } else {// Wenn die Karte kein Trumpf, es sich dabei aber um ein Ass handelt, dann füge der Schätzung den Wert der Karte hinzu
                currentValue = currentCard.valueS;
                if (currentCard.face == Cards.Face.ass) {
                    estimation += currentValue;
                }
            }
            if (sortedList.isEmpty()) {// Erster Eintrag in die sortierte Liste
                sortedList.add(card);
            } else {
                for (int i = 0; i < sortedList.size(); i++) {// Füge die Karte an der richtigen Stelle ein und verschiebe alle höheren Karte um eins nach hinten
                    if (currentValue < cards.getValue(sortedList.get(i), trumpColor)) {
                        int t = currentIndex;
                        currentIndex = sortedList.get(i);
                        currentValue = cards.getValue(currentIndex, trumpColor);
                        sortedList.set(i, t);
                    }
                }
                sortedList.add(currentIndex);
            }
        }
        cardsOnHand = sortedList;   //Ersetze die Karten auf der Hand durch die sortierte Liste
        sortByColor();  //Sortiere die Karten auf der Hand noch nach ihrer Farbe
    }

    public int playCard() {// Bestimme anhand der Situation welche Karte ausgespielt wird und versuche sie auszuspielen
        int cardToPlay = -1;    //Für den Fall, dass keine auszuspielende Karte bestimmt werden kann
        if (cardsOnTable.isEmpty()) {// Wenn die KI die erste Karte in einer Runde ausspielt
            Color[] probableBockColors = getProbableBockColors();   //Bestimme die Reihenfolge in welcher die Farben am ehesten Bock werden
            if (score < estimation) {// Wenn noch Punkte fehlen zum Erreichen der Schätzung
                cardToPlay = leastWorthyCardOfNonBockColor(probableBockColors);  //Die auszuspielende Karte ist die Karte mit dem kleinsten Wert der Farbe, die am ehesten nicht Bock wird
            } else {// Wenn die Schätzung bereits erreicht oder sogar übertroffen wurde
                cardToPlay = lowestCardOfBockColor(probableBockColors); //Die auszuspielende Karte ist die niedrigste Karte der Farbe, die am ehesten Bock wird
            }
        } else {// Wenn die KI nicht die erste Karte in einer Runde ausspielt
            ArrayList<Integer> possibleDraws = getValidDraws(); //Bestimme anhand der Situation welche Karten ausgespielt werden dürfen
            if (possibleDraws.size() == 1) {// Wenn nur eine Karte als spielbar befunden wurde, ist diese die auszuspielende Karte
                cardToPlay = possibleDraws.get(0);
            } else {// Wenn mehr als eine eine Karte als spielbar befunden wurden
                if (score < estimation) {// Wenn noch Punkte fehlen zum Erreichen der Schätzung
                    setHighestCardOnTable();    // Bestimme die höchste Karte auf dem Tisch
                    ArrayList<Integer> winningCards = getWinningCards(possibleDraws);   //Bestimme von den spielbaren Karten diejenigen, die höher sind als die höchste Karte auf dem Tisch
                    if (winningCards.isEmpty()) {// Wenn keine Karte auf der Hand höher ist als die höchste Karte auf dem Tisch
                        cardToPlay = possibleDraws.get(0); //Die auszuspielende Karte ist die Karte mit dem kleinsten Wert unter den spielbaren Karten
                    } else if (cardsOnTable.size() == 3) {// Wenn die KI die letzte Karte in einer Runde spielt
                        int difference = estimation - score;    //Bestimme die bis zum Erzielen der Schätzung fehlenden Punkte
                        int pointsOnTable = getPointsOnTable(); //Bestimme die Summe der Punkte aller Karten auf dem Tisch
                        int toBeat = difference;    //Differenz die es zu schlagen gilt
                        for (int card : winningCards) {// Für jede Karte auf der Hand der KI die höher ist als die höchste Karte auf dem Tisch
                            if (Math.abs(difference - pointsOnTable - cards.getValue(card, trumpColor)) < Math.abs(toBeat)) {// Wenn die Differenz zur Schätzung durch einen Stich mit dieser Karte kleiner wird, dann ist dies die auszuspielende Karte
                                toBeat = difference - pointsOnTable - cards.getValue(card, trumpColor); //Aktualisiere die zu schlagende Differenz
                                cardToPlay = card;
                            }
                        }
                        if (cardToPlay < 0) { //Wenn mit keiner dieser Karten durch einen Stich die Differenz zur Schätzung kleiner wird
                            cardToPlay = noMorePointsWanted(possibleDraws); //Die auszuspielende Karte ist die entweder die Karte mit dem grössten Wert unter den spielbaren Karten die niedriger sind als die höchste Karte auf dem Tisch oder die niedrigste Karte unter den spielbaren Karten
                        }
                    } else {
                        for (int card : winningCards) {// Für jede Karte auf der Hand der KI die höher ist als die höchste Karte auf dem Tisch
                            if (cards.getCard(card).color != trumpColor) {// Vermeide das Ausspielen von Trupfkarten
                                if (cardPlayed(new Integer(card))) {// Versuche diese Karte auszuspielen
                                    return card;
                                }
                            }
                        }
                        cardToPlay = getLowestCard(possibleDraws);  //Die auszuspielende Karte ist die niedrigste Karte unter den spielbaren Karten
                    }
                } else {
                    cardToPlay = noMorePointsWanted(possibleDraws); //Die auszuspielende Karte ist die entweder die Karte mit dem grössten Wert unter den spielbaren Karten die niedriger sind als die höchste Karte auf dem Tisch oder die niedrigste Karte unter den spielbaren Karten
                }
            }
        }
        if (cardPlayed(new Integer(cardToPlay))) {// Fall noch nicht geschehen, versuche die auszuspielende Karte auszuspielen
            return cardToPlay;
        } else {// An dieser Stelle wird ein Fehlercode weitergegeben, wenn die auszuspielende Karte gar nicht auf der Hand ist
            return -1;   
        }
    }

    private int leastWorthyCardOfNonBockColor(Color[] probableBockColors) {// Bestimme welche Karte ausgespielt wird, wenn noch Punkte erzielt werden sollen und die KI erster in der Runde ist oder keine Karte auf ihrer Hand höher als die höchste Karte auf dem Tisch ist 
        ArrayList<Integer> cardsOfOneColor;
        for (int i = 3; i >= 0; i--) {// Gehe von der Farbe, die am wahrscheinlichsten Bock wird bis zu derjenigen, bei der das am unwahrscheinlichsten ist
            if (probableBockColors[i] == trumpColor) {// Vermeide das Ausspielen von Trupfkarten
                continue;
            }
            cardsOfOneColor = getCardsOfOneColor(probableBockColors[i]);    //Bestimme alle Karten auf der Hand von dieser Farbe
            if (!cardsOfOneColor.isEmpty()) {// Falls Karten von dieser Farbe auf der Hand sind, versuche davon die mit dem kleinsten Wert auszuspielen
                return cardsOfOneColor.get(0);
            }
        }
        cardsOfOneColor = getCardsOfOneColor(trumpColor);   //Falls nur noch Trümpfe auf der Hand sind, versuche davon die mit dem kleinsten Wert auszuspielen
        return cardsOfOneColor.get(0);
    }

    private int lowestCardOfBockColor(Color[] probableBockColors) {
        ArrayList<Integer> cardsOfOneColor;
        for (Color color : probableBockColors) {// Gehe von der Farbe, die am unwahrscheinlichsten Bock wird bis zu derjenigen, bei der das am wahrscheinlichsten ist
            if (color == trumpColor) {// Vermeide das Ausspielen von Trupfkarten
                continue;
            }
            cardsOfOneColor = getCardsOfOneColor(color); //Bestimme alle Karten auf der Hand von dieser Farbe
            if (!cardsOfOneColor.isEmpty()) {// Falls Karten von dieser Farbe auf der Hand sind, versuche davon die niedrigste auszuspielen
                return getLowestCard(cardsOfOneColor);
            }
        }
        cardsOfOneColor = getCardsOfOneColor(trumpColor);   //Falls nur noch Trümpfe auf der Hand sind, versuche davon die niedrigste auszuspielen
        return getLowestCard(cardsOfOneColor);
    }

    private int noMorePointsWanted(ArrayList<Integer> possibleDraws) {// Bestimme welche Karte ausgespielt wird, wenn die KI nicht erster in der Runde ist und keine Punkte erzielt werden sollen
        ArrayList<Integer> loosingCards = getLoosingCards(possibleDraws);
        if (loosingCards.isEmpty()) {
            return getLowestCard(possibleDraws);
        } else {
            return loosingCards.get(loosingCards.size() - 1);
        }
    }

    public boolean cardPlayed(Integer card) {// versuche eine Karte auszuspielen
        Color color = cards.getCard(card).color;
        switch (color) {
            case Eicheln:
                eichelnOnHand.remove(card);
                break;
            case Rosen:
                rosenOnHand.remove(card);
                break;
            case Schilten:
                schiltenOnHand.remove(card);
                break;
            case Schellen:
                schellenOnHand.remove(card);
                break;
        }
        return cardsOnHand.remove(card);
    }

    @Override
    public void newGame() {// Triff Vorbereitungen für ein neues Spiel
        score = 0;
        eichelnOnHand = new ArrayList<>();
        rosenOnHand = new ArrayList<>();
        schiltenOnHand = new ArrayList<>();
        schellenOnHand = new ArrayList<>();
    }

    private void sortByColor() {// Sortiere die Karten auf der Hand nach ihrer Farbe
        for (int card : cardsOnHand) {
            Card currentCard = cards.getCard(card);
            switch (currentCard.color) {
                case Eicheln:
                    eichelnOnHand.add(card);
                    break;
                case Rosen:
                    rosenOnHand.add(card);
                    break;
                case Schilten:
                    schiltenOnHand.add(card);
                    break;
                case Schellen:
                    schellenOnHand.add(card);
                    break;
            }
        }
    }

    private Color[] getProbableBockColors() {// Bestimme die Reihenfolge in welcher die Farben am ehesten Bock werden
        Color[] probableBockColors = {Color.Eicheln, Color.Rosen, Color.Schilten, Color.Schellen};
        int[] cardCount = new int[4];
        cardCount[0] = eichelnGone.size() + eichelnOnHand.size();   //Anzahl bekannter Eicheln
        cardCount[1] = rosenGone.size() + rosenOnHand.size();       //Anzahl bekannter Rosen
        cardCount[2] = schiltenGone.size() + schiltenOnHand.size(); //Anzahl bekannter Schilten
        cardCount[3] = schellenGone.size() + schellenOnHand.size(); //Anzahl bekannter Schellen
        for (int i = 0; i < 3; i++) {// Bubble sort Algorithmus
            for (int j = 0; j < 3 - i; j++) {
                if (cardCount[j] > cardCount[j + 1]) {
                    Color tempColor = probableBockColors[j];
                    int tempCount = cardCount[j];
                    probableBockColors[j] = probableBockColors[j + 1];
                    cardCount[j] = cardCount[j + 1];
                    probableBockColors[j + 1] = tempColor;
                    cardCount[j + 1] = tempCount;
                }
            }
        }
        return probableBockColors;
    }

}
