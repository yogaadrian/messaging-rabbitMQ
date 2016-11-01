/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.concurrent.TimeoutException;

/**
 *
 * @author FiqieUlya
 */
public class Main {
    public static void main(String[] argv)
        throws java.io.IOException, java.lang.InterruptedException, TimeoutException {
        MessengerServer server = new MessengerServer();
    }
}
