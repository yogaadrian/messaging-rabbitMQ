/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitmqtes;

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

    public MessengerClient() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.basicQos(10); // accept only one unack-ed message at a time (see below)

        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        autoAck = false; // acknowledgment is covered below

    }

    public int login(String userid, String password) throws IOException {
        channel.queueDeclare(userid, false, false, false, null);
        channel.basicConsume(userid, autoAck, consumer);
        return 0;
    }

    public int register() throws IOException, InterruptedException {
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
                break;
            }
        }
        return 0;

    }
}
