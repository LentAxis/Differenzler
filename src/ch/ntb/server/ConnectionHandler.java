/*
 * NTB - Interstaatliche Hochschule für Technik Buchs
 * Schoenauweg 4, 9000 St. Gallen
 * All rights reserved
 *
 * Reference: https://www.differenzler.ch/
 */
package ch.ntb.server;

import ch.ntb.server.com.google.gson.Gson;
import java.net.UnknownHostException;
/**
 *
 * @author andreas.scholz@ost.ch
 */
public class ConnectionHandler extends Thread {

        private final CallbackEvent event;
        //private final CallbackType  type;
        private BroadcastCommunicationString    brdComStrg;
        private UnicastCommunicationString      uniComStrg;
        //private final javax.swing.JTextArea     textField;
        private int port;
        private int[] ports;
        private String host;
        private volatile boolean running = true;

        private int delaytimeout = 300;
        private Gson gson;

        public ConnectionHandler(int p, String h, CallbackEvent event){

            gson = new Gson();
            this.port = p;
            this.host = h;
            try {
                brdComStrg = new BroadcastCommunicationString(host);
            } catch (UnknownHostException e) {
                e.printStackTrace(System.out);
            }

            this.event = event;

        }

        public ConnectionHandler(int p, CallbackEvent event){


            gson = new Gson();
            this.port = p;
            try {
                uniComStrg = new UnicastCommunicationString();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            this.event = event;

        }
        
        public ConnectionHandler(int p[], CallbackEvent event){

            gson = new Gson();
            this.port = -1;
            this.ports = p;
            try {
                uniComStrg = new UnicastCommunicationString();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            this.event = event;

        }

        public boolean testPort(){
            if(brdComStrg != null){
                try {
                    brdComStrg.open(port);
                    brdComStrg.close();
                } catch (Exception e) {
                    //e.printStackTrace(System.out);
                    return false;
                }
            }else{
                try {
                    uniComStrg.open(port);
                    uniComStrg.close();
                } catch (Exception e) {
                    //e.printStackTrace(System.out);
                    return false;
                }
            }
            return true;
        }
        
        
        @Override
        public void run() {

            running = true;
            if(brdComStrg != null){
                try {
                    brdComStrg.open(port);
                    String receiveMsg;
                    while (running) {

                        brdComStrg.waitForMessage();
                        receiveMsg = brdComStrg.getMessageAsString();

                        event.process(receiveMsg);
                        delay();
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }else{
                try {
                    uniComStrg.open(port);
                    String receiveMsg;
                    while (running) {

                        uniComStrg.waitForMessage();
                        receiveMsg = uniComStrg.getMessageAsString();

                        event.process(receiveMsg);
                        delay();
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }

            }

        }
        public void prepare() {
            System.out.println("Connection handling started..");

            try {
                if(brdComStrg != null){
                    brdComStrg.open(port);
                }else{
                    uniComStrg.open();
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        public void publish(String str){
            try {
                if(brdComStrg != null){
                    brdComStrg.sendMessageAsString(host, port, str);
                }else{
                    uniComStrg.sendMessageAsString("localhost", port, str);
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        
        public void publish(String str, int p){
            try {
                if(brdComStrg != null){
                    System.out.println("Broadcast doesn't support explicit port");
                }else{
                    uniComStrg.sendMessageAsString("localhost", ports[p], str);
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        synchronized void delay() {
            try {
                Thread.sleep(delaytimeout);
                //wait(delaytimeout);
            } catch (InterruptedException e) {
                System.out.println("Exception in delay (" + e + ")");
                System.exit(0);
            }
        }

        public void terminate() /*throws InterruptedException*/ {
            running = false;
            try {
                if(brdComStrg != null){
                    brdComStrg.close();
                }else{
                    uniComStrg.close();
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

        }

        public void setDelay(int d){
            delaytimeout = d;
        }
    }
