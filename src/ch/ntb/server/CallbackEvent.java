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
public interface CallbackEvent {
    
    public void process(String msg);
}
