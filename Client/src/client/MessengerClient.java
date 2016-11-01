/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.Message;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yoga
 */
public class MessengerClient {

    private final static String serverqueue = "server_queue";
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private Scanner sc = new Scanner(System.in);
    private final Consumer consumer;
    boolean autoAck;
    private boolean isLogin;
    private String id;
    private ArrayList<String> listfriend, listgroup;

    public MessengerClient() throws IOException, TimeoutException {
        isLogin = false;
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.basicQos(10); // accept only one unack-ed message at a time (see below)
        channel.queueDeclare(serverqueue, true, false, false, null);
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                channel.basicAck(envelope.getDeliveryTag(), false);
                Message m=null;
                try {
                    m = Message.toMessage(body);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MessengerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                switch(m.getType()){
                    case 0:{
                        break;
                    }
                    case 1:{
                        break;
                    }
                    case 2:{
                        switch(m.getContent()){
                            case "Group has been already exist":{
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        };
        autoAck = false; // acknowledgment is covered below

    }

    public int createGroup() throws IOException {
        if (isLogin) {
            System.out.print("Masukkan nama grup : ");
            String namagrup = sc.nextLine();
            System.out.println("Masukkan user id member dengan menekan enter setelah setiap user id. masukkan -1 untuk berhenti");
            String userid = sc.nextLine();
            ArrayList<String> listusers = new ArrayList<String>();
            while (!userid.equalsIgnoreCase("-1")) {
                listusers.add(userid);
                userid = sc.nextLine();
            }
            String content = "creategroup " + namagrup;
            Message m = new Message(2, id, content);
            m.setUserIDs(listusers);
            channel.basicPublish("", serverqueue, null, m.toBytes());
        }
        return 0;
    }

    public int login() throws IOException, InterruptedException, ClassNotFoundException {
        String queueName = channel.queueDeclare().getQueue();
        QueueingConsumer qc = new QueueingConsumer(channel);
        channel.basicConsume(queueName, autoAck, qc);
        System.out.print("Masukkan userid : ");
        String userid = sc.nextLine();
        System.out.print("Masukkan password : ");
        String password = sc.nextLine();
        String content = "login " + userid + " " + password;
        Message m = new Message(2, userid, content);
        String corrId = java.util.UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(queueName)
                .build();
        channel.basicPublish("", serverqueue, props, m.toBytes());

        while (true) {
            QueueingConsumer.Delivery delivery = qc.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                Message response = Message.toMessage(delivery.getBody());
                if (response.getContent().equalsIgnoreCase("success")) {
                    System.out.println("berhasil");
                    listfriend=new ArrayList<String>(m.getListFriend());
                    listgroup=new ArrayList<String>(m.getListGroup());
                    isLogin = true;
                    id=userid;
                    channel.queueDeclare(userid, true, false, false, null);
                    channel.basicConsume(userid, true, consumer);
                    return 1;
                } else {
                    System.out.println("gagal");
                    return 0;
                }
            }
        }
    }

    public int register() throws IOException, InterruptedException, ClassNotFoundException {
        String queueName = channel.queueDeclare().getQueue();
        QueueingConsumer qc = new QueueingConsumer(channel);
        channel.basicConsume(queueName, autoAck, qc);
        System.out.print("Masukkan userid : ");
        String userid = sc.nextLine();
        System.out.print("Masukkan password : ");
        String password = sc.nextLine();
        String content = "register " + userid + " " + password;
        Message m = new Message(2, userid, content);
        String corrId = java.util.UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(queueName)
                .build();
        channel.basicPublish("", serverqueue, props, m.toBytes());

        while (true) {
            QueueingConsumer.Delivery delivery = qc.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                //response = new String(delivery.getBody());
                Message response = Message.toMessage(delivery.getBody());
                if (response.getContent().equalsIgnoreCase("success")) {
                    System.out.println("berhasil");
                    return 1;
                } else {
                    System.out.println("gagal");
                    return 0;
                }
            }
        }

    }
}
