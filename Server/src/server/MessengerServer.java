/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import client.Message;
import java.util.ArrayList;

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

    MessengerServer() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(SERVER_QUEUE_NAME, true, false, false, null);

        channel.basicQos(1);

        ArrayList<String> groupNames = getGroups();
        for (String name : groupNames) {
            channel.exchangeDeclare(name, "fanout");
        }
        //consumer = new QueueingConsumer(channel);
        //channel.basicConsume(SERVER_QUEUE_NAME, false, consumer);

        System.out.println(" [x] Awaiting RPC requests");
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("dapet");
                Message message;
                System.out.println("dapet");
                try {
                    message = Message.toMessage(body);
                    System.out.println(message.getType());
                    switch (message.getType()) {
                        //PM
                        case 0: {
                            break;
                        }
                        //GROUP
                        case 1: {
                            break;
                        }
                        //COMMAND
                        case 2: {
                            System.out.println(message.getContent());
                            String[] contents = message.getContent().split(" ");
                            System.out.println(contents[0]);
                            System.out.println(contents[0]);
                            System.out.println(contents[0]);
                            Message m;
                            if (contents[0].equalsIgnoreCase("register")) {
                                String userId = contents[1];
                                String password = contents[2];
                                if (getUserName(userId).equalsIgnoreCase(userId)) {
                                    System.out.println("user already exist!");
                                    m = new Message(1, SERVER_QUEUE_NAME, "FAIL");
                                } else {
                                    registerUser(userId, password);
                                    System.out.println("success!");
                                    m = new Message(1, SERVER_QUEUE_NAME, "SUCCESS");
                                }
                                BasicProperties replyProps = new BasicProperties.Builder()
                                        .correlationId(properties.getCorrelationId())
                                        .build();

                                channel.basicPublish("", properties.getReplyTo(), replyProps, m.toBytes());
                            } else if (contents[0].equalsIgnoreCase("login")) {
                                System.out.println(loginUser(contents[1], contents[2]));

                                m = new Message(2, SERVER_QUEUE_NAME, loginUser(contents[1], contents[2]));
                                m.setListFriend(getFriends(contents[1]));
                                m.setListGroup(getGroups(contents[1]));
                                BasicProperties replyProps = new BasicProperties.Builder()
                                        .correlationId(properties.getCorrelationId())
                                        .build();

                                channel.basicPublish("", properties.getReplyTo(), replyProps, m.toBytes());
                                //BIKIN LIST FRIEND SAMA LIST GROUP
                            } else if (contents[0].equalsIgnoreCase("creategroup")) {
                                System.out.println("CREATE GROUP: ");
                                String res = createGroup(message.getGroupName(), message.getListUser(), message.getSender());
                                String[] cek = res.split(" ");
                                if (cek[0].equalsIgnoreCase("success")) {
                                    //send to member
                                    channel.exchangeDeclare(message.getGroupName(), "fanout");
                                    for(String name : getMember(message.getGroupName())){
                                        channel.queueBind(name, message.getGroupName(), "");
                                    }
                                    m = new Message(2, SERVER_QUEUE_NAME, "joingroup");
                                    m.setGroupName(message.getGroupName());
                                    channel.basicPublish(message.getGroupName(), "", null, m.toBytes());
                                }
                                //send to sender
                                Message mu = new Message(2, SERVER_QUEUE_NAME, res);
                                mu.setListGroup(getGroups(message.getSender()));
                                channel.basicPublish("", message.getSender(), null, mu.toBytes());
                            } else if (contents[0].equalsIgnoreCase("leavegroup")) {
                                
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

    public String getUserName(String userId) {
        String result = "";
        try {
            String sql = "SELECT * FROM User WHERE user_id = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, userId);
            ResultSet res = dbStatement.executeQuery();
            int count = 0;
            if (res.next()) {
                count++;
                result = res.getString("user_id");
            }
            res.close();
            if (count < 1) {
                result = "empty";
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public boolean isUser(String userId, String password) {
        boolean result = false;
        try {
            String sql = "SELECT * FROM User WHERE user_id = ? AND password = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, userId);
            dbStatement.setString(2, password);
            ResultSet res = dbStatement.executeQuery();
            int count = 0;
            if (res.next()) {
                count++;
                result = true;
            }
            res.close();
            if (count < 1) {
                result = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String loginUser(String user, String password) {
        if (getUserName(user).equalsIgnoreCase("empty")) {
            return "user name doesn't exist";
        } else {
            if (isUser(user, password)) {
                return "success";
            } else {
                return "invalid password";
            }
        }
    }

    public boolean isGroupExist(String groupId) {
        boolean result = false;
        try {
            String sql = "SELECT * FROM GroupName WHERE group_id = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, groupId);
            ResultSet res = dbStatement.executeQuery();
            int count = 0;
            if (res.next()) {
                count++;
                result = true;
            }
            res.close();
            if (count < 1) {
                result = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String createGroup(String groupName, ArrayList<String> member, String Admin) {
        if (isGroupExist(groupName)) {
            return "Group already exist";
        } else if (getUserName(Admin).equalsIgnoreCase("empty")) {
            return "invalid user";
        } else {
            try {
                String sql = "INSERT INTO GroupName (group_id, Admin) VALUES (?,?)";
                PreparedStatement dbStatement = conn.prepareStatement(sql);
                dbStatement.setString(1, groupName);
                dbStatement.setString(2, Admin);
                dbStatement.executeUpdate();
                dbStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
                return "FAIL";
            }
            String temp = "";
            if (!member.isEmpty()) {
                member.add(Admin);
                for (String name : member) {
                    if (!getUserName(name).equalsIgnoreCase("empty")) {
                        try {
                            String sql = "INSERT INTO GroupMember (user_id, group_id) VALUES (?,?)";
                            PreparedStatement dbStatement = conn.prepareStatement(sql);
                            dbStatement.setString(1, name);
                            dbStatement.setString(2, groupName);
                            dbStatement.executeUpdate();
                            dbStatement.close();
                            System.out.println(name + " joined");
                        } catch (SQLException ex) {
                            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        temp += name + " not a user! ";
                    }
                }
            }
            return "success " + temp;
        }
    }

    public boolean registerUser(String name, String password) {
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

    public ArrayList<String> getFriends(String userId) {
        ArrayList<String> friends = new ArrayList();
        try {
            String sql = "SELECT * FROM Friend WHERE user_id = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, userId);
            ResultSet res = dbStatement.executeQuery();
            while (res.next()) {
                friends.add(res.getString("friend_id"));
            }
            res.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friends;
    }

    public ArrayList<String> getGroups(String userId) {
        ArrayList<String> groups = new ArrayList();
        try {
            String sql = "SELECT * FROM GroupMember WHERE user_id = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, userId);
            ResultSet res = dbStatement.executeQuery();
            while (res.next()) {
                groups.add(res.getString("group_id"));
            }
            res.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return groups;
    }

    public ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList();
        try {
            String sql = "SELECT * FROM GroupName";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            ResultSet res = dbStatement.executeQuery();
            while (res.next()) {
                groups.add(res.getString("group_id"));
            }
            res.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return groups;
    }

    public ArrayList<String> getMember(String groupId) {
        ArrayList<String> friends = new ArrayList();
        try {
            String sql = "SELECT * FROM GroupMember WHERE group_id = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, groupId);
            ResultSet res = dbStatement.executeQuery();
            while (res.next()) {
                friends.add(res.getString("user_id"));
            }
            res.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friends;
    }

    public ArrayList<String> getReceiverMember(String groupId, String sender) {
        ArrayList<String> friends = new ArrayList();
        try {
            String sql = "SELECT * FROM GroupMember WHERE group_id = ?";
            PreparedStatement dbStatement = conn.prepareStatement(sql);
            dbStatement.setString(1, groupId);
            ResultSet res = dbStatement.executeQuery();
            while (res.next()) {
                if (!sender.equalsIgnoreCase(res.getString("user_id"))) {
                    friends.add(res.getString("user_id"));
                }
            }
            res.close();
        } catch (SQLException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friends;
    }

    public boolean addFriend(String sender, String receiver) {
        if (sender.equalsIgnoreCase(getUserName(sender)) && receiver.equalsIgnoreCase(getUserName(receiver))) {
            try {
                String sql = "INSERT INTO Friend (user_id, friend_id) VALUES (?, ?)";
                PreparedStatement dbStatement = conn.prepareStatement(sql);
                dbStatement.setString(1, sender);
                dbStatement.setString(2, receiver);
                dbStatement.executeUpdate();

                sql = "INSERT INTO Friend (user_id, friend_id) VALUES (?, ?)";
                dbStatement = conn.prepareStatement(sql);
                dbStatement.setString(1, receiver);
                dbStatement.setString(2, sender);
                dbStatement.executeUpdate();

                dbStatement.close();
                System.out.println("add friend berhasil");

            } catch (SQLException ex) {
                Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /*public boolean joinGroup(String groupId, String userId) {
     try {
     String sql = "INSERT INTO GroupMember (user_id, group_id) VALUES (?, ?)";
     PreparedStatement dbStatement = conn.prepareStatement(sql);
     dbStatement.setString(1, userId);
     dbStatement.setString(2, groupId);
     dbStatement.executeUpdate();
            
     dbStatement.close();
     } catch (SQLException ex) {
     Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
     }
     }*/
    public void sendMessage(String groupId, String sender, String Content) {
        String message = sender + ": " + Content;
        ArrayList<String> receivers = getReceiverMember(groupId, sender);
        for (String receiver : receivers) {

        }
    }

    public void sendMessage(String receiver, String Sender) {

    }
}
