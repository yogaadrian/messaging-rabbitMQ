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
        System.out.println("Masukkan Command. masukkan 'help' untuk mendapatkan petunjuk list command");
        while(true){
            String command = sc.nextLine();
            switch(command) {
                case "register":{
                    mclient.register();
                    break;
                }
                case "login":{
                    mclient.login();
                    break;
                }
                case "create-group":{
                    mclient.createGroup();
                    break;
                }
                case "leave-group":{
                    mclient.leaveGroup();
                    break;
                }
                case "add-friend":{
                    mclient.addFriend();
                    break;
                }
                case "chat-group":{
                    mclient.chatGroup();
                    break;
                }
                case "chat-friend":{
                    mclient.chatFriend();
                    break;
                }
                case "get-friends":{
                    mclient.getFriends();
                    break;
                }
                case "get-groups":{
                    mclient.getGroups();
                    break;
                }
                case "help":{
                    break;
                }
                default:{
                    System.out.println("Inputan salah");
                }
                
            }
            
        }
    }
    
    public static void help(){
        System.out.println("'register' untuk mendaftarkan user baru");
        System.out.println("'login' untuk masuk ke aplikasi dengan user yang sudah terdaftar");
        System.out.println("command dibawah ini memerlukan login terlebih dahulu");
        System.out.println("'create-group' untuk mendaftarkan grup baru");
        System.out.println("'leave-group' untuk meninggalkan group yang diikuti");
        System.out.println("'add-friend' untuk menambahkan teman baru");
        System.out.println("'chat-group' untuk chat ke group yang terdaftar");
        System.out.println("'chat-friend' untuk chat ke teman yang terdaftar");
        System.out.println("'get-friends' untuk mendapatkan list teman yang terdaftar");
        System.out.println("'get-groups' untuk mendaftarkan list group yang terdaftar");
    }



}
