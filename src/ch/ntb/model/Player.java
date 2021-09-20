package ch.ntb.model;

import ch.ntb.server.Card;
import ch.ntb.server.Cards;
import ch.ntb.server.Cards.Color;
import java.util.ArrayList;

public class Player {

    public static ArrayList<Integer> cardsOnTable;  //Karten die auf dem Tisch liegen
    
    static ArrayList<Integer> eichelnGone;      //Eicheln die bereits gespielt wurden
    static ArrayList<Integer> rosenGone;        //Rosen ...
    static ArrayList<Integer> schiltenGone;     //Schilten ...
    static ArrayList<Integer> schellenGone;     //Schellen ...

    public ArrayList<Integer> cardsOnHand;      //Karten die der Spieler auf der Hand hat

    int playerID = -1;      //Spieler-ID
    int trump = -1;         //Trupf-ID
    int estimation = -1;    //abgegebene Schätzung
    int score = -1;         //aktueller Punktestand
    int lastCard = -1;      //ID der letzten ausgespielten Karte
    int match = 0;          //Gamescore

    Color trumpColor;       //Trumpf-Farbe
    Color firstColor;       //Farbe der ersten Karte in einer Runde

    Card highestCardOnTable;    //höchste Karte die auf dem Tisch liegt

    final Cards cards = new Cards();

    public Player() {
        cardsOnHand = new ArrayList<>();
    }

    public boolean playCard(int card) {// versuche eine Karte auszuspielen und merke die Karte
        lastCard = card;
        cardPlayed(card);
        return cardsOnHand.remove(new Integer(card));
    }
    
    public void addLastCard()   // Nimm die zuletzt ausgespielte Karte zurück auf die Hand
    {
        cardsOnHand.add(lastCard);
    }
    
    public void cardPlayed(int card){// Lege die ausgespielte Karte auf den Tisch und merke welche Karten bereits gegangen sind
        cardsOnTable.add(card);
        Color color = cards.getCard(card).color;
        switch (color) {
            case Eicheln:
                eichelnGone.add(card);
                break;
            case Rosen:
                rosenGone.add(card);
                break;
            case Schilten:
                schiltenGone.add(card);
                break;
            case Schellen:
                schellenGone.add(card);
                break;
        }
    }

    public void clearTable() {// räume den Tisch auf
        cardsOnTable = new ArrayList<>();
    }
    
    public void newGame(){// Triff Vorbereitungen für ein neues Spiel
        score = 0;
        clearTable();
        eichelnGone = new ArrayList<>();
        rosenGone = new ArrayList<>();
        schiltenGone = new ArrayList<>();
        schellenGone = new ArrayList<>();
    }

    public ArrayList<Integer> getValidDraws() {// Bestimme anhand der Situation welche Karten ausgespielt werden dürfen
        firstColor = cards.getCard(cardsOnTable.get(0)).color;  //Bestimme die Farbe der ersten Karte auf dem Tisch
        ArrayList<Integer> possibleDraws = getCardsOfOneColor(firstColor);  //Alle Karten auf der Hand von der Farbe der ersten Karte auf dem Tisch sind spielbar
        if (firstColor == trumpColor) {// Wenn die erste Karte auf dem Tisch Trumpf ist...
            int numberOfTrumpCards = possibleDraws.size();  //Bestimme die Anzahl Trümpfe auf der Hand
            if (numberOfTrumpCards == 0) {// Wenn die Anzahl Null ist, dann sind alle Karten auf der Hand spielbar
                possibleDraws = (ArrayList<Integer>) cardsOnHand.clone();  
            } else if (numberOfTrumpCards == 1) {// Wenn die Anzahl eins ist, dann muss überprüft werden ob diese Karte der Trumpf Buur ist
                Card card = cards.getCard(possibleDraws.get(0));
                if (card.face == Cards.Face.under) {    //Wenn die Karte der Trumpf Buur ist, dann sind alle Karten auf der Hand spielbar
                    possibleDraws = (ArrayList<Integer>) cardsOnHand.clone();
                }
            }
        } else {// Wenn die erste Karte auf dem tisch kein Trumpf ist, dann gibt es noch weitere spielbare Karten
            setHighestCardOnTable();    // Bestimme die höchste Karte auf dem Tisch
            if (possibleDraws.isEmpty()){// Wenn keine Karte auf der Hand von der Farbe der ersten Karte auf dem Tisch ist, dann sind alle Karten auf der Hand spielbar
                possibleDraws = (ArrayList<Integer>) cardsOnHand.clone();
            } else if (highestCardOnTable.color == trumpColor) {// Wenn die höchste Karte auf dem Tisch Trumpf ist, dann sind alle Trümpfe spielbar, die höher als die höchste Karte auf dem Tisch sind
                possibleDraws.addAll(getWinningCards(cardsOnHand));
            } else {// Ansonsten sind alle Trümpfe spielbar
                possibleDraws.addAll(getCardsOfOneColor(trumpColor));
            }
        }
        if (possibleDraws.size() == 0){// Hier wird eine Fehlermeldung ausgegeben, falls keine Karte auf der Hand als spielbar befunden werden sollte
            System.err.println("Client-side determination of drawable cards failed");
        }
        return possibleDraws;
    }

    public ArrayList<Integer> getCardsOfOneColor(Color color) {// Bestimme alle Karten einer bestimmten Farbe auf der Hand des Spielers
        ArrayList<Integer> cardsOfOneColor = new ArrayList<>();
        for (int card : cardsOnHand) {
            if (cards.getCard(card).color == color) {
                cardsOfOneColor.add(card);
            }
        }
        return cardsOfOneColor;
    }

    public void setHighestCardOnTable() {// Bestimme die höchste Karte auf dem Tisch
        highestCardOnTable = cards.getCard(cardsOnTable.get(0));
        int highestRank = Cards.getRanking(highestCardOnTable, trumpColor);
        for (int i = 1; i < cardsOnTable.size(); i++) {
            int rank = Cards.getRanking(cards.getCard(cardsOnTable.get(i)), trumpColor, firstColor);
            if (highestRank < rank) {
                highestRank = rank;
                highestCardOnTable = cards.getCard(cardsOnTable.get(i));
            }
        }
    }

    public int getHighestCard(ArrayList<Integer> listOfCards) {// Bestimme die ID der höchsten Karte aus einer ArrayList mit Karten-IDs
        int highestCard = listOfCards.get(0);
        int highestRank = Cards.getRanking(cards.getCard(highestCard), trumpColor);
        for (int i = 1; i < listOfCards.size(); i++) {
            int rank = Cards.getRanking(cards.getCard(listOfCards.get(i)), trumpColor);
            if (highestRank < rank) {
                highestRank = rank;
                highestCard = listOfCards.get(i);
            }
        }
        return highestCard;
    }

    public int getLowestCard(ArrayList<Integer> listOfCards) {// Bestimme die ID der niedrigsten Karte aus einer ArrayList mit Karten-IDs
        int lowestCard = listOfCards.get(0);
        int lowestRank = Cards.getRanking(cards.getCard(lowestCard), trumpColor);
        for (int i = 1; i < listOfCards.size(); i++) {
            int rank = Cards.getRanking(cards.getCard(listOfCards.get(i)), trumpColor);
            if (lowestRank > rank) {
                lowestRank = rank;
                lowestCard = listOfCards.get(i);
            }
        }
        return lowestCard;
    }

    public ArrayList<Integer> getWinningCards(ArrayList<Integer> cardsToCheck) {// Bestimme alle Karten auf der Hand des Spielers die gegen eine bestimmte Karte gewinnen würden
        ArrayList<Integer> winningCards = new ArrayList<>();
        int rank = Cards.getRanking(highestCardOnTable, trumpColor, firstColor);
        for (int card : cardsToCheck) {
            if (Cards.getRanking(cards.getCard(card), trumpColor, firstColor) > rank) {
                winningCards.add(card);
            }
        }
        return winningCards;
    }

    public ArrayList<Integer> getLoosingCards(ArrayList<Integer> cardsToCheck) {// Bestimme alle Karten auf der Hand des Spielers die gegen eine bestimmte Karte verlieren würden
        ArrayList<Integer> loosingCards = new ArrayList<>();
        int rank = Cards.getRanking(highestCardOnTable, trumpColor, firstColor);
        for (int card : cardsToCheck) {
            if (Cards.getRanking(cards.getCard(card), trumpColor, firstColor) < rank) {
                loosingCards.add(card);
            }
        }
        return loosingCards;
    }

    public int getPointsOnTable() { //Bestimme die Summe der Punkte aller Karten auf dem Tisch
        int pointsOnTable = 0;
        for (int card : cardsOnTable) {
            pointsOnTable += cards.getValue(card, trumpColor);
        }
        return pointsOnTable;
    }

    public ArrayList<Integer> sortedByValue(ArrayList<Integer> toSort) {//sortiere eine ArrayList mit Karten-IDs nach dem Wert der Karten (wird aktuell nicht benötigt)
        ArrayList<Integer> sortedList = new ArrayList<>();
        for (int card : toSort) {// Für alle zu sortierenden Karten
            if (sortedList.isEmpty()) {
                sortedList.add(card);
            } else {
                int currentCard = card;
                for (int i = 0; i < sortedList.size(); i++) {// Füge die Karte an der richtigen Stelle ein und verschiebe alle höheren Karte um eins nach hinten
                    if (cards.getValue(currentCard, trumpColor) < cards.getValue(sortedList.get(i), trumpColor)) {
                        int t = currentCard;
                        currentCard = sortedList.get(i);
                        sortedList.set(i, t);
                    }
                }
                sortedList.add(currentCard);
            }
        }
        return sortedList;
    }
    
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setHandCards(int id) {
        cardsOnHand.add(id);
    }

    public int getHandCard(int position) {
        return cardsOnHand.get(position);
    }

    public void setEstimation(int estimation) {
        this.estimation = estimation;
    }

    public int getEstimation() {
        return estimation;
    }

    public void setTrump(int trump) {
        this.trump = trump;
    }

    public int getTrump() {
        return trump;
    }

    public int getLastCard() {
        return lastCard;
    }

    public void setLastCard(int lastCard) {
        this.lastCard = lastCard;
    }
    
    public void setMatch(int match){
        this.match = match;
    }
    
    public int getMatch(){
        return match;
    }

    public void updateScore(int roundscore){// Aktualisiere die Punktezahl um die in einer Runde erzielten Punkte
        score += roundscore;
    }

    public int getScore() {
        return score;
    }
    
}
