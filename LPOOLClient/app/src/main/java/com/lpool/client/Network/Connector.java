package com.lpool.client.Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by André on 03/06/2015.
 */
public class Connector {

    public static enum ProtocolCmd {
        ANGLE, // angle
        FIRE, // force[0, 1] x-spin[-1, 1] y-spin[-1, 1]
        PING,
        PONG,
        JOIN,
        QUIT,
        KICK,
        BIH,
        ACKBIH,
        PLACECB // x-pos[0, 1] y-pos[0, 1]
    };

    // TCP
    private volatile Socket socket;
    // UDP
    private DatagramSocket datagramSocket;

    private String serverIP;
    private int serverPort;
    private Boolean running = true;

    public Connector(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
        running = true;
        initializeClientThread();
        initializeHeartbeatThread();
    }



    public Boolean sendUPDMessage(String message) {

        if(!running)
            return false;

        if (socket == null || datagramSocket == null)
            return false;
        try {
            String data = message;
            byte[] msg = data.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(serverIP), serverPort);
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean sendTCPMessage(String message) {

        if(!running)
            return false;

        if (socket == null)
            return false;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            pw.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    class ClientThread implements Runnable {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                socket = new Socket(serverAddr, serverPort);
                datagramSocket = new DatagramSocket(serverPort);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class beatThread implements Runnable {
        @Override
        public void run() {
            while (running)
            {
                try {
                    System.out.println("Receiving...");
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = br.readLine();
                    System.out.println("Received...");
                    if (str == null) {
                        System.out.println("received null");
                        break;
                    }
                    else if(str.equals(ProtocolCmd.PING.ordinal() + "" + '\n'))
                    {
                        System.out.println("Got PING");
                        try {
                            String data = new String("" + ProtocolCmd.PONG.ordinal() + '\n');
                            byte[] msg = data.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(serverIP), serverPort);
                            datagramSocket.send(sendPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeClientThread() {
        new Thread(new ClientThread()).start();
    }

    private void initializeHeartbeatThread() { new beatThread().run();}

    public void stop() {
        running = false;
    }

    public void restart() {
        running = true;
        initializeClientThread();
        initializeHeartbeatThread();
    }
}
