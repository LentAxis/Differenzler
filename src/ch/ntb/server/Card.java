/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ntb.server;

/**
 *
 * @author danie
 */
    public class Card{
        // attributs are public but immutable, read only;
        public final String name;
        public final String file;
        public final Cards.Face face;
        public final Cards.Color color;
        public final int valueS;
        public final int valueT;

        public Card(String name, String file, Cards.Face face, Cards.Color color, int valueS, int valueT) {
            this.name = name;
            this.file = file;
            this.face = face;
            this.color = color;
            this.valueS = valueS;
            this.valueT = valueT;
        }
    }
