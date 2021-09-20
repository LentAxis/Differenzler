/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 * Spielregeln: siehe unten.
 */
package ch.ntb.server;

import static java.lang.Math.abs;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author andreas.scholz@ost.ch
 */
public class GameEngine {
    
    private int state;  
    private int match;                  // match currently played
    private int numberMatches;          // how many matches to play
    private int roundsLeft;             // how many cards left
    private int firstPlayer;            // player opening the round
    private int currentPlayer;          // player currently drawing
    private int roundWinner;            // who did win the round
    private int roundScore;             // stack value
    private Cards.Color currentTrump;   // default: null
    private Cards.Color currentCall ;   // First color to be on table, default: null
    private int[] estimates;            // estimates for current match
    private int[] matchScores;          // cumulative scores of current match, default: 0
    private int[] matchDifferences;     // differences of current matches, default:  0
    private int[] gameDifferences;      // cumulative differences of all matches, default:  0
    private int[] cardsOnTable;         // all cards drawn, default: -1
    private int[][] cardsOnHand;        // all cards each player has on his hand, max 9, default: -1
    private int[] possibleDraws;        // all possible cards which can be drawn
    
    private final Cards card = new Cards();
    
    public GameEngine(){
        this.state              = 0;
        this.match              = 0;
        this.numberMatches      = 3;                // 4 Matches, but it begins with Match 0
        this.estimates          = new int[4];      // 4 players
        this.matchScores        = new int[4];      // 4 players
        this.matchDifferences   = new int[4];      // 4 players
        this.gameDifferences    = new int[4];      // 4 players
        this.cardsOnTable       = new int[4];      // 4 players
        this.cardsOnHand        = new int[4][9];   // 4 players, 9 cards each
        this.possibleDraws      = new int[9];      // up to 9 cards
        resetRound();
        resetMatch();
    }
        
    public final void resetRound(){
        for(int i = 0; i < 4; ++i){
            cardsOnTable[i] = -1;
        }
        currentPlayer = roundWinner;
        firstPlayer = roundWinner;
    }
    
    public final void resetMatch(){
        this.state = 0;
        for(int i = 0; i < 4; ++i){
            matchScores[i] = 0;
            cardsOnTable[i] = -1;
            estimates[i] = -1;
            matchDifferences[i] = 0;
        }
    }
    
    public void resetGame(){
        this.state = 0;
        this.match = 0;
        for(int i = 0; i < 4; ++i){
            matchDifferences[i] = 0;
            matchScores[i] = 0;
            gameDifferences[i] = 0;
            estimates[i] = -1;
            cardsOnTable[i] = -1;
            for(int j = 0; j < 4; ++j){
                cardsOnHand[i][j] = -1;
            }
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    public void createNewGame(){
        // ToDo: initialize all game parameters, reset scores, shuffle the cards 
        this.state = 1;
        this.roundsLeft = 9;
        this.currentPlayer = this.match;
        
        int[] allCards = new int[36];
        for(int i = 0; i < 36; ++i){
            allCards[i] = i;
        }
        shuffleArray(allCards);
        
        for(int p = 0; p < 4;++p){
            System.out.print("Player "+(p+1)+": ");
            for(int c = 0; c < 9;++c){
                this.cardsOnHand[p][c] = allCards[p*9+c];
                System.out.print(allCards[p*9+c]+", ");
            }
            System.out.println();
        }
        
        // get a random color value to be trump
        currentTrump = Cards.Color.values()[new SplittableRandom().nextInt(0, 4)];
    }
    
    public long getPlayerCards(int player){
        // returns the 9 cards of a player binary encoded inside a long, e.g. 0.....01011010001 for cards 1,5,7,8,10
        long data = 0L;
        
        for(int c = 0; c < 9;++c){
            data |= (1L << this.cardsOnHand[player][c]);
//            System.out.print(this.cardsOnHand[player][c] + " ");
        }

        /*
        for(int i = 36; i >= 0; --i){
            System.out.print((data & (1L << i)) > 0?"1":"0");
        }
        */
        return data;
    }
    
    public void setEstimate(long data, int player){
        estimates[player] = (int)data;
    }
    
    public void setDraw(long data, int player){
        if(this.currentPlayer != player) System.out.println("Player missmatch");    // testing purpose, shouldn't be possible
        int c = 0;
        for(int i = 0; i < 4; ++i){
            if(cardsOnTable[i] >= 0) ++c;
        }
        if(c == 0) currentCall = card.getCard((int)data).color; // set the color of the round if it's the first draw
        
        // put the card on the table
        cardsOnTable[player] = (int)data;
        // and remove it from the hand
        for(int i = 0; i < 9; ++i){
            if(cardsOnHand[player][i] == (int)data) cardsOnHand[player][i] = -1;
        }
        this.currentPlayer = (++currentPlayer)%4;
    }
    
    
    public boolean isDrawValid(long data, int player){
        boolean CardOfCurrentCall = false;
        Card draw = card.getCard((int)data);
        //Card first = card.getCard((int)data);
        for(int i = 0; i < 9; ++i)
        {
            if(cardsOnHand[player][i] > -1)
            {
                if(card.getCard(cardsOnHand[player][i]).color == currentCall) CardOfCurrentCall = true;
            }
        }
        
        if(CardOfCurrentCall){
            if((draw.color == currentTrump) && (currentCall != currentTrump) && (currentCall != null)){          //Untertrumpfen nicht erlauben
                for(int i = 0; i < 4; ++i){
                    if(cardsOnTable[i] > -1){
                        if(card.getCard(cardsOnTable[i]).color == currentTrump){
                            if(card.getCard(cardsOnTable[i]).valueT > draw.valueT) return false;
                        }
                    }
                }
            }       
        }
        
        if((draw.color == currentTrump) || (draw.color == currentCall) || (currentCall == null)) return true;    // if draw is trump or serve: okay
        
        for(int i = 0; i < 9; ++i){             // otherwise, check if serve would be possible
            if(cardsOnHand[player][i] > -1){
                if(card.getCard(cardsOnHand[player][i]).color == currentCall){
                    if(!((card.getCard(cardsOnHand[player][i]).color == currentTrump) && (card.getCard(cardsOnHand[player][i]).face == Cards.Face.under)))  return false; //Buur muss nicht angegeben werden
                }
            }
        }
        //if serve not possible, any card is allowed
        return true;
    }    
    
    public long getPossibleDraws(int player)
    {
        boolean valid = false;
        for(int i = 0; i < 9; i++){
            possibleDraws[i] = -1;
            if(this.cardsOnHand[player][i] > -1){
                
                valid = checkPossibleDraws(cardsOnHand[player][i],player);
                if(valid)  possibleDraws[i] = cardsOnHand[player][i];
            }
        }
        
        //encode Possible Draws
        long data = 0L;
        
        for(int c = 0; c < 9;++c){
            if(this.possibleDraws[c] > -1){
                data |= (1L << this.possibleDraws[c]);
                System.out.print(this.possibleDraws[c] + " ");
            }
        }
        System.out.println();
        return data;
    }
    
    public boolean checkEstimates(){
        for(int i = 0; i < 4; ++i){
            if(estimates[i] == -1) return false;
        }
        return true;
    }
    
    public boolean checkDraws(){
        for(int i = 0; i < 4; ++i){
            if(cardsOnTable[i] == -1) return false;
        }
        return true;
    }
    
    public void calculateRoundResult(){
        --roundsLeft;
        //Zwischenspeichern
        Card[] onTable = {
            card.getCard(cardsOnTable[0]),
            card.getCard(cardsOnTable[1]),
            card.getCard(cardsOnTable[2]),
            card.getCard(cardsOnTable[3])
        };
        
        roundWinner = 0; 
        roundScore = 0;
        int highestRank = 0; 
        
        //Höchste Karte heraussuchen
        for(int i = 0; i < 4; ++i){
            int rank = Cards.getRanking(onTable[i], currentTrump, onTable[firstPlayer].color);
            if(highestRank < rank){
                highestRank = rank;
                roundWinner = i;
            }
        }
        //Punkte auf dem Tisch zusammenzählen
        for(int i = 0; i < 4; ++i){
            roundScore += onTable[i].color == currentTrump?onTable[i].valueT:onTable[i].valueS;
        }     
        
        //5 Punkte für den letzten Stich
        if (roundsLeft == 0) 
            roundScore += 5;           
        
        matchScores[roundWinner] += roundScore;
    }
    
    public void calculateMatchResult(){
        
        for(int i = 0; i < 4; ++i){
            matchDifferences[i] = abs(matchScores[i] - estimates[i]);
            
        }
    }
    
    public void calculateGameResult(){
        
        for(int i = 0; i < 4; ++i){
            gameDifferences[i] += matchDifferences[i];
        }
    }
    
    public int getGameDifference(int player)
    {
        return gameDifferences[player];
    }
    
    public int getRoundwinner(){
        currentCall = null;
        return roundWinner;
    }
    
    public int getRoundScore(){
        return roundScore;
    }
    
    public void setNextMatch(){
        ++this.match;
        this.currentPlayer = match%4;
    }
    
    public int getcardsLeft(){
        return roundsLeft;
    }
    
    public int getCurrentPlayer(){
        return this.currentPlayer;
    }
    
    public int getScore(int player){
        return  this.matchScores[player];
    }
    
    public int getEstimate(int player){
        return this.estimates[player];
    }
    
    public boolean isFinished(){
        return numberMatches == match;
    }
    
    public int getIsMatchWinner(int player){
        // returns the players has won (1) or lost (0) the match.
        int lowestScore = matchDifferences[0];
        
        for(int i = 1; i < 4; ++i){
            if(matchDifferences[i] < lowestScore){
                lowestScore = matchDifferences[i];
            }
        }
        
        return matchDifferences[player]==lowestScore?1:0;
    }
    
    public int getIsGameWinner(int player){
        // returns the players has won (1) or lost (0) the game.
        int lowestScore = gameDifferences[0];
        
        for(int i = 1; i < 4; ++i){
            if(gameDifferences[i] < lowestScore){
                lowestScore = gameDifferences[i];
            }
        }
        
        return gameDifferences[player]==lowestScore?1:0;
    }
    
    public int getGameWinner()
    {
        int player = 0;
        for(int i = 1; i < 4; ++i)
        {
            if(gameDifferences[player] > gameDifferences[i])
            {
                player = i;
            }
        }
        return player;
    }
    
    public int getTrump(){
        // returns the current trump for this match as index.
        return currentTrump.id;
    }
    
    public int getMatch(){
        return this.match;
    }
    
    // Fisher–Yates shuffle
    static void shuffleArray(int[] ar)
    {
      Random rnd = ThreadLocalRandom.current();
      for (int i = ar.length - 1; i > 0; i--)
      {
        int index = rnd.nextInt(i + 1);
        // Simple swap
        int a = ar[index];
        ar[index] = ar[i];
        ar[i] = a;
      }
    } 
    
    private boolean checkPossibleDraws(int nr, int player)
    {        
        int validCounter = 0;
         Card draw = card.getCard(nr);
        //Card first = card.getCard((int)data);
        
        for(int i = 0; i < 9; ++i){             // otherwise, check if serve would be possible
            if(cardsOnHand[player][i] > -1){
                if(card.getCard(cardsOnHand[player][i]).color == currentCall) validCounter++;
            }
        }
        
        if((draw.color == currentTrump) && (currentCall != currentTrump) && (currentCall != null)){          //Untertrumpfen nicht erlauben
            for(int i = 0; i < 4; ++i){
                if(cardsOnTable[i] > -1){
                    if(card.getCard(cardsOnTable[i]).color == currentTrump){
                        if(card.getCard(cardsOnTable[i]).valueT > draw.valueT) return false;
                    }
                }
            }
        }       
        
        if(validCounter > 0){
            if((draw.color == currentTrump) || (draw.color == currentCall) || (currentCall == null)) return true;    // if draw is trump or serve: okay     
        }
        else
            return true;
        
        return false;
    }
    
}


/*
Der Differenzlerjass - Spielregeln

Trumpf und Ansage
Für ein Spiel werden die 36 Karten gemischt und verteilt. Jeder Spieler erhält 
9 Karten. Bei jeder Spielrunde wird durch Zufall die Trumpf-Farbe bestimmt. Die 
Spieler schätzen nun, wie viele Punkte sie mit ihrem Blatt erreichen werden. 
Die Schätzung wird entweder offen oder verdeckt angesagt. Bei der verdeckten 
Ansage erfahren die anderen Mitspieler erst nach Ende der Runde wie viel jeder 
angesagt hat. Hingegen wird beim offenen Differenzler die Ansage für die 
anderen Mitspieler bereits am Anfang der Runde angezeigt.
Erscheint in der Software der Ansagedialog, kann entweder durch verschieben der 
Markierung auf der Punkteskala oder durch direktes Eintippen über die 
Zahlenfelder der geschätzte Punktwert eingegeben werden. Das Total der Karten 
ergibt 152 Punkte. Zusätzlich zählt der letzte Stich 5 Punkte. Es können also 
maximal 157 Punkte angesagt werden.

Das Spiel
Das Spiel beginnt nachdem alle Spieler ihre Ansage gemacht haben. Der erste 
Spieler gibt eine Karte und die anderen Spieler geben der Reihe nach entweder 
eine Karte der gleichen Farbe oder eine Trumpfkarte. Wer die höchste Karte bzw. 
stärkste Trumpfkarte gegeben hat, sticht und erhält die vier gespielten Karten. 
Der Stich hat einen Wert, der dem Punktewert aller vier Karten entspricht. Alle 
Stiche werden zusammengezählt und am Ende der Runde mit der angesagten Punktzahl 
verglichen. Die Differenzpunkte jeder Runde werden zusammengezählt und gewonnen 
hat, wer am Spielende am wenigsten Differenzpunkte erzielt hat. 

Werte für nicht-Trumpfkarten:
Karte		Punkte	
As              11
König           4
Ober/Dame 	3
Under/Bube 	2
Banner/Zehn 	10
Neun            -
Acht            -
Sieben          -
Sechs           - 

Werte beim Trumpf:	
Karte		Punkte	
Puur            20
Nell            14
As              11
König           4
Ober/Dame 	3
Banner/Zehn 	10
Acht            -
Sieben          -
Sechs           - 

Im Spiel sind immer 152 Punkte. Dazu kommen 5 Punkte für den letzten 
Stich. Das ergibt das Total von 157 Punkten.

*/