/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author yoga
 */
public class MessengerServer {

    private static final String SERVER_QUEUE_NAME = "server_queue";
    private static java.sql.Connection conn = DBConnector.connect();
    private List<String> queueName;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private Consumer consumer;
    
    MessengerServer() throws IOException, TimeoutException{
        factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(SERVER_QUEUE_NAME, true, false, false, null);

        channel.basicQos(1);
        //consumer = new QueueingConsumer(channel);
        //channel.basicConsume(SERVER_QUEUE_NAME, false, consumer);

        System.out.println(" [x] Awaiting RPC requests");
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Message message;
                try {
                    message = Message.toMessage(body);
                    switch(message.getType()){
                        //PM
                        case 1 : {
                            break;
                        }
                        //GROUP
                        case 2: {
                            break;
                        }
                        //COMMAND
                        case 3: {
                            String [] contents = message.getContent().split(" ");
                            if(contents[0].equalsIgnoreCase("register")){
                                String userId = contents[2];
                                String password = contents[3];
                                if(getUser(userId).equalsIgnoreCase(userId)){
                                    System.out.println("user does exist!");
                                }else{
                                    registerUser(userId, password);
                                }
                            }
                            break;
                        }
                    }
                    
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
                }
;                 
                //System.out.println(" [x] Received '" + message + "'");
                /*try {
                
                } finally {
                    System.out.println(" [x] Done");
                }*/
            }
        };
        boolean autoAck = true; // acknowledgment is covered below
        channel.basicConsume(SERVER_QUEUE_NAME, autoAck, consumer);
    }
    
    public String getUser(String userId){
        String result = "";
        try {
            String sql = "SELECT * FROM User WHERE user_id = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, userId);
            ResultSet res = dbStatement.executeQuery();
            int count = 0;
            if (res.next()){
                count++;
                result= res.getString("user_id");
            }
            res.close();
            if(count < 1){
                result ="empty";
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public boolean registerUser(String name, String password){
        boolean status = false;
        try {
            String sql = "INSERT INTO User (user_id, password) VALUES (?, ?)";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, name);
            dbStatement.setString(2, password);
            dbStatement.executeUpdate();
            
            dbStatement.close();
            status = true;
        } catch (SQLException ex) {
            System.out.println("user already exist");
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    
    /*public boolean loginUser(String name, String password){
        
    }*/
    
    
    
}