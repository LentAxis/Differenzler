/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 */
package ch.ntb.server;

/**
 *
 * @author andreas.scholz@ost.ch
 */
public class Cards {

    private final Card[] cards = new Card[]{
        new Card("Eicheln 6","00.png",Face.sechs, Color.Eicheln,0,0),
        new Card("Eicheln 7","01.png",Face.sieben, Color.Eicheln,0,0),
        new Card("Eicheln 8","02.png",Face.acht, Color.Eicheln,0,0),
        new Card("Eicheln 9","03.png",Face.neun, Color.Eicheln,0,14),
        new Card("Eicheln Banner","04.png",Face.banner, Color.Eicheln,10,10),
        new Card("Eicheln Under","05.png",Face.under, Color.Eicheln,2,20),
        new Card("Eicheln Ober","06.png",Face.ober, Color.Eicheln,3,3),
        new Card("Eicheln König","07.png",Face.koenig, Color.Eicheln,4,4),
        new Card("Eicheln Ass","08.png",Face.ass, Color.Eicheln,11,11),
        
        new Card("Rosen 6","09.png",Face.sechs, Color.Rosen,0,0),
        new Card("Rosen 7","10.png",Face.sieben, Color.Rosen,0,0),
        new Card("Rosen 8","11.png",Face.acht, Color.Rosen,0,0),
        new Card("Rosen 9","12.png",Face.neun, Color.Rosen,0,14),
        new Card("Rosen Banner","13.png",Face.banner, Color.Rosen,10,10),
        new Card("Rosen Under","14.png",Face.under, Color.Rosen,2,20),
        new Card("Rosen Ober","15.png",Face.ober, Color.Rosen,3,3),
        new Card("Rosen König","16.png",Face.koenig, Color.Rosen,4,4),
        new Card("Rosen Ass","17.png",Face.ass, Color.Rosen,11,11),
        
        new Card("Schilten 6","18.png",Face.sechs, Color.Schilten,0,0),
        new Card("Schilten 7","19.png",Face.sieben, Color.Schilten,0,0),
        new Card("Schilten 8","20.png",Face.acht, Color.Schilten,0,0),
        new Card("Schilten 9","21.png",Face.neun, Color.Schilten,0,14),
        new Card("Schilten Banner","22.png",Face.banner, Color.Schilten,10,10),
        new Card("Schilten Under","23.png",Face.under, Color.Schilten,2,20),
        new Card("Schilten Ober","24.png",Face.ober, Color.Schilten,3,3),
        new Card("Schilten König","25.png",Face.koenig, Color.Schilten,4,4),
        new Card("Schilten Ass","26.png",Face.ass, Color.Schilten,11,11),
        
        new Card("Schellen 6","27.png",Face.sechs, Color.Schellen,0,0),
        new Card("Schellen 7","28.png",Face.sieben, Color.Schellen,0,0),
        new Card("Schellen 8","29.png",Face.acht, Color.Schellen,0,0),
        new Card("Schellen 9","30.png",Face.neun, Color.Schellen,0,14),
        new Card("Schellen Banner","31.png",Face.banner, Color.Schellen,10,10),
        new Card("Schellen Under","32.png",Face.under, Color.Schellen,2,20),
        new Card("Schellen Ober","33.png",Face.ober, Color.Schellen,3,3),
        new Card("Schellen König","34.png",Face.koenig, Color.Schellen,4,4),
        new Card("Schellen Ass","35.png",Face.ass, Color.Schellen,11,11)
    };

    private static final int[][] weight = new int[][]{
        {1, 10}, //Sechs
        {2, 11}, //Sieben
        {3, 12}, //Acht
        {4, 17}, //Neun 
        {5, 13}, //Banner/Zehn
        {6, 18}, //Under/Bube
        {7, 14}, //Ober/Dame
        {8, 15}, //König
        {9, 16}, //Ass
    };

    public enum Face {
        sechs   (0),
        sieben  (1),
        acht    (2),
        neun    (3),
        banner  (4),
        under   (5),     // bube
        ober    (6),     // dame
        koenig  (7),     // koenig
        ass      (8);

        public final int id;

        Face(int i) {
            id = i;
        }
    }

    public enum Color {
        Eicheln(0),
        Rosen(1),
        Schilten(2),
        Schellen(3);

        public final int id;

        Color(int i) {
            id = i;
        }
    }

    public Card getCard(int index) {
        return cards[index];
    }

    // toRank == first card
    public static int getRanking(Card toRank, Color trump) {
        return weight[toRank.face.id][(toRank.color == trump ? 1 : 0)];
    }

    public static int getRanking(Card toRank, Color trump, Color first) {
        return weight[toRank.face.id][(toRank.color == trump ? 1 : 0)] * (toRank.color == first || toRank.color == trump ? 1 : 0);
    }

    public Color getColor(int id) {
        switch (id) {
            case 0:
                return Color.Eicheln;
            case 1:
                return Color.Rosen;
            case 2:
                return Color.Schilten;
            case 3:
                return Color.Schellen;
            default:
                return null;
        }
    }

    public int getValue(int index, Color trump) {
        Card currentCard = getCard(index);
        if (currentCard.color == trump) {
            return currentCard.valueT;
        } else {
            return currentCard.valueS;
        }
    }
    
    public Cards() {
    }

}
