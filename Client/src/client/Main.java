/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yoga
 */
public class Main {
    static private Scanner sc = new Scanner(System.in);
    
    public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException, ClassNotFoundException {
        MessengerClient mclient = new MessengerClient();
        while(true){
            String command = sc.nextLine();
            switch(command) {
                case "register":{
                    mclient.register();
                    break;
                }
            }
            
        }
    }



}
