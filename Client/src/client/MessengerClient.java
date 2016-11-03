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
    boolean autoAck = true;
    private boolean isLogin;
    private String id;
    private ArrayList<String> listfriend, listgroup;

    public MessengerClient() throws IOException, TimeoutException {
        isLogin = false;
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        //channel.basicQos(1); // accept only one unack-ed message at a time (see below)
        channel.queueDeclare(serverqueue, true, false, false, null);
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                //channel.basicAck(envelope.getDeliveryTag(), false);
                Message m = null;
                try {
                    m = Message.toMessage(body);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MessengerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                switch (m.getType()) {
                    case 0: {
                        System.out.println(m.getSender() + " : " + m.getContent());
                        break;
                    }
                    case 1: {
                        System.out.println(m.getGroupName() + " , " + m.getSender() + " : " + m.getContent());
                        break;
                    }
                    case 2: {
                        switch (m.getContent()) {
                            case "joingroup": {
                                listgroup.add(m.getGroupName());
                                System.out.println("join group " + m.getGroupName());
                                break;
                            }
                            case "leavegroup": {
                                listgroup.remove(m.getGroupName());
                                System.out.println("leave group " + m.getGroupName());
                                break;
                            }
                            case "addfriend": {
                                listfriend.add(m.getFriendID());
                                System.out.println("addfriend " + m.getFriendID());
                                break;
                            }
                            default: {
                                System.out.println(m.getContent());
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        };
        autoAck = true; // acknowledgment is covered below

    }

    public int getFriends() throws IOException {
        if (isLogin) {
            System.out.println("[List Friend]");
            for (int i = 0; i < listfriend.size(); i++) {
                System.out.println(listfriend.get(i));
            }
            return 1;
        }
        System.out.println("Perlu melakukan login");
        return 0;
    }

    public int getGroups() throws IOException {
        if (isLogin) {
            System.out.println("[List Group]");
            for (int i = 0; i < listgroup.size(); i++) {
                System.out.println(listgroup.get(i));
            }
            return 1;
        }
        System.out.println("Perlu melakukan login");
        return 0;
    }

    public int chatFriend() throws IOException {
        if (isLogin) {
            System.out.print("Masukkan nama teman : ");
            String namauser = sc.nextLine();
            if (!listfriend.contains((String) namauser) || id.equalsIgnoreCase(namauser)) {
                System.out.println("tidak ada teman itu");
                return 0;
            }
            System.out.print("Masukkan pesan : ");

            String content = sc.nextLine();
            Message m = new Message(0, id, content);
            m.setFriendID(namauser);
            channel.basicPublish("", serverqueue, null, m.toBytes());
            return 1;
        }
        System.out.println("Perlu melakukan login");
        return 0;
    }

    public int chatGroup() throws IOException {
        if (isLogin) {
            System.out.print("Masukkan nama group : ");
            String namagroup = sc.nextLine();
            if (!listgroup.contains((String) namagroup)) {
                System.out.println("tidak ada group itu");
                return 0;
            }
            System.out.print("Masukkan pesan : ");
            String content = sc.nextLine();
            Message m = new Message(1, id, content);
            m.setGroupName(namagroup);
            channel.basicPublish("", serverqueue, null, m.toBytes());
            return 1;
        }
        System.out.println("Perlu melakukan login");
        return 0;
    }

    public int addFriend() throws IOException {
        if (isLogin) {
            System.out.print("Masukkan nama teman : ");
            String namauser = sc.nextLine();
            if (listfriend.contains((String) namauser) || id.equalsIgnoreCase(namauser)) {
                System.out.println("sudah menjadi teman");
                return 0;
            }
            String content = "addfriend";
            Message m = new Message(2, id, content);
            m.setFriendID(namauser);
            channel.basicPublish("", serverqueue, null, m.toBytes());
            return 1;

        }
        System.out.println("Perlu melakukan login");
        return 0;
    }

    public int leaveGroup() throws IOException {
        if (isLogin) {
            System.out.print("Masukkan nama grup : ");
            String namagrup = sc.nextLine();
            if (!listgroup.contains((String) namagrup)) {
                System.out.println("tidak terdaftar dalam grup");
                return 0;
            }
            String content = "leavegroup";
            Message m = new Message(2, id, content);
            m.setGroupName(namagrup);
            channel.basicPublish("", serverqueue, null, m.toBytes());
            return 1;
        }
        System.out.println("Perlu melakukan login");
        return 0;
    }

    public int addUsertoGroup() throws IOException {
        if (isLogin) {
            System.out.print("Masukkan nama grup : ");
            String namagrup = sc.nextLine();
            System.out.println(listgroup.isEmpty());
            if (listgroup.isEmpty() || !listgroup.contains((String) namagrup)) {
                System.out.println("tidak ada group tersebut");
                return 0;
            }
            System.out.println("Masukkan user id : ");
            String userid = sc.nextLine();

            String content = "addusertogroup";
            Message m = new Message(2, id, content);
            m.setGroupName(namagrup);
            m.setFriendID(userid);
            channel.basicPublish("", serverqueue, null, m.toBytes());
            return 1;
        } else {
            System.out.println("Perlu melakukan login");
            return 0;
        }

    }

    public int createGroup() throws IOException {
        if (isLogin) {
            System.out.print("Masukkan nama grup : ");
            String namagrup = sc.nextLine();
            if (!listgroup.isEmpty() && listgroup.contains((String) namagrup)) {
                System.out.println("sudah terdaftar dalam group");
                return 0;
            }
            System.out.println("Masukkan user id member dengan menekan enter setelah setiap user id. masukkan -1 untuk berhenti");
            String userid = sc.nextLine();
            ArrayList<String> listusers = new ArrayList<String>();
            while (!userid.equalsIgnoreCase("-1")) {
                listusers.add(userid);
                userid = sc.nextLine();
            }
            String content = "creategroup";
            Message m = new Message(2, id, content);
            m.setGroupName(namagrup);
            m.setUserIDs(listusers);
            channel.basicPublish("", serverqueue, null, m.toBytes());
            return 1;
        } else {
            System.out.println("Perlu melakukan login");
            return 0;
        }

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

                    listfriend = response.getListFriend();
                    listgroup = response.getListGroup();
                    if (listgroup == null) {
                        listgroup = new ArrayList<String>();
                    }
                    if (listfriend == null) {
                        listfriend = new ArrayList<String>();
                    }

                    isLogin = true;
                    id = userid;
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
