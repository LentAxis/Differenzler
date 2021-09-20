/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 * Spielregeln: siehe GameEngine.
 */
package ch.ntb.server;

/**
 *
 * @author andreas.scholz@ost.ch
 */

// draw (=1 card of 1 player) -> round (=4 draws) -> match (=9 Rounds) -> game (=4 matches)
public enum MessageType {
    JOINREQUEST     (0), // <-> client asks to join the table (game may be running or not) 
    NEWGAME         (1), // <-> client asks for new game
    QUITREQUEST     (2), // <-> client asks to retire
    KI_REQUEST      (3), // <- client asks to add/connect a KI player
    JOINED          (4), //  -> server tell who joint the game
    
    ESTIMATEREQUEST (5), // <-> server asks for an estimate, client answers
    DRAWREQUEST     (6), // <-> server asks for a card, client answers
    OPPONENTDRAW    (7), //  -> server tells which card of other was played
    ROUNDWINNER     (8), //  -> server tells who got the stack == stack animation / is next first
    ROUNDSCORE      (9), //  -> server tells the score of current round to winner
    
    MATCHWINNER    (10), //  -> server tells score of a player after a single match
    MATCHSCORE     (11), //  -> server tells score of a player after a single match
    MATCHESTIMATE  (12), //  -> server tells estimate of a player after a single match
    
    TRUMP          (13), //  -> server tells which color is trump
    FINISH         (14), //  -> server tells who won 3 matches; also end game
    REJECTED       (15); //  -> server unhappy about message, error code in data
    

    public final int id;
    MessageType(int i){ id = i; }
}
