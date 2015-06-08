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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by André on 03/06/2015.
 */
public class Connector {

    public static final int HEARTBEAT_TIMEOUT = 10; // seconds
    public static final int HEARTBEAT_INTERVAL = 5; // seconds
    private Timer timer;
    private Thread connectionChecker;
    private Boolean gotPONG = false;

    public enum EndReason
    {
        BLACK_BALL_SCORED_AS_LAST,
        BLACK_BALL_SCORED_ACCIDENTALLY,
        TIMEOUT,
        DISCONNECT
    }

    public enum ProtocolCmd {
        ANGLE, // angle
        FIRE, // force[0, 1] x-spin[-1, 1] y-spin[-1, 1]
        PING,
        PONG,
        JOIN,
        QUIT,
        KICK,
        MOVECB, // x-pos[0, 1] y-pos[0, 1]
        PLACECB, // x-pos[0, 1] y-pos[0, 1]
        PLAY,  // 0(no ball), 1(not-stripes), 2(stripes), 3(black ball)
        WAIT,
        BIH,
        END // winner(boolean) End.Reason
    }

    public enum PlayingForBall {
        NONE,
        SOLID,
        STRIPE,
        BLACK
    }

    // TCP
    private volatile Socket socket;
    // UDP
    private DatagramSocket datagramSocket;

    private String serverIP;
    private int serverPort;
    private Boolean running = true;

    private ArrayList<Receiver> receivers;
    private String firstMessage;

    public Connector(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
        running = true;
        timer = new Timer();
        receivers = new ArrayList<Receiver>();
        initializeClientThread(); // Initializes receiver/heartbeat thread
        System.out.println("Connecting to " + serverIP + " " + serverPort);
    }

    public Connector(String ip, int port, String firstMessage) {
        this(ip, port);
        this.firstMessage = firstMessage;
    }

    public Boolean sendUDPMessage(String message) {

        if(!running)
            return false;

        if (socket == null || datagramSocket == null) {
            System.out.println("UDP message not sent -> socket closed");
            return false;
        }
        try {
            String data = message;
            byte[] msg = message.getBytes();
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

        if (socket == null) {
            System.out.println("TCP message not sent -> socket closed");
            return false;
        }
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            pw.println(message);
            System.out.println("Sent " + message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void checkerThread() {
        connectionChecker = new Thread( new Runnable() {
            public void run() {
                while(running){
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(socket == null || datagramSocket == null) {
                        System.out.println("Innactive connection");
                        stopReceivers();
                        disconnect();
                    }
                }
            }
        });

        connectionChecker.start();
    }

    class ClientThread implements Runnable {
        public void run() {
            try {
                System.out.println("Part 1");
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                System.out.println("Part 2");
                if(socket == null) {
                    System.out.println("Creating new TCP socket");
                    socket = new Socket(serverAddr, serverPort);
                }
                System.out.println("Part 3");
                if(datagramSocket == null) {
                    System.out.println("Creating new UDP socket");
                    datagramSocket = new DatagramSocket(serverPort);
                }
                System.out.println("Part 4");
                initializeReceiverThread();
            } catch (UnknownHostException e1) {
                System.out.println("Error 1");
                e1.printStackTrace();
            } catch (IOException e1) {
                System.out.println("Error 2");
                e1.printStackTrace();
            }
            checkerThread();
        }
    }

    class receiverThread implements Runnable    {
        @Override
        public void run() {
            System.out.println("Receiver");
            startHeartBeat();

            if(firstMessage != null) {
                sendTCPMessage(firstMessage);
            }

            if(connectionChecker != null) {
                connectionChecker.interrupt();
            }
            while (running)
            {
                try {
                    System.out.println("Receiving...");
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = br.readLine();
                    if (str == null) {
                        System.out.println("received null");
                        stopReceivers();
                        disconnect();
                        break;
                    }
                    System.out.println("Received message \"" + str + "\"");
                    if(str.equals(ProtocolCmd.PING.ordinal() + ""))
                    {
                        System.out.println("Got PING");
                        sendTCPMessage("" + ProtocolCmd.PONG.ordinal());
                        System.out.println("Sent PONG");
                    }
                    else if(str.equals(ProtocolCmd.PONG.ordinal() + "")) {
                        System.out.println("Got PONG");
                        gotPONG = true;
                    } else
                        spreadMessage(str);
                } catch (IOException e) {
                    System.out.println("Error 3");
                    e.printStackTrace();
                }
            }
        }
    }

    private void spreadMessage(String message) {
        for(int i = 0; i < receivers.size(); i++) {
            if(receivers.get(i) != null)
                receivers.get(i).getMessage(message);
        }
    }

    private void initializeClientThread() {
        new Thread(new ClientThread()).start();
    }

    private void initializeReceiverThread() { new receiverThread().run();}

    public void addReceiver(Receiver receiver) {
        receivers.add(receiver);
    }

    public void removeReceiver(Receiver receiver) {
        if(receivers.indexOf(receiver) != -1) {
            receivers.remove(receiver);
        }
    }

    public void stop() {
        running = false;
        receivers.clear();
    }

    public void restart() {
        running = true;
        initializeClientThread();
        initializeReceiverThread();
    }

    public void disconnect() {
        stop();
        try {
            if(datagramSocket != null) {
                System.out.println("UDP Socket closed");
                datagramSocket.close();
                datagramSocket = null;
            }
            if(socket != null) {
                System.out.println("TCP Socket closed");
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startHeartBeat() {
        timer.schedule(new HeartBeatTask(), 1000);
    }

    private class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Sending PING");
            sendTCPMessage(ProtocolCmd.PING.ordinal() + "");
            gotPONG = false;

            try {
                Thread.sleep(HEARTBEAT_TIMEOUT * 1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if(!gotPONG) {
                System.out.println("TIMEOUT");
                stopReceivers();
            }
            else {
                timer.schedule(new HeartBeatTask(), HEARTBEAT_INTERVAL * 1000);
            }

        }
    }

    private void stopReceivers() {
        System.out.println("Stopping receivers");
        for(int i = 0; i < receivers.size(); i++) {
            if(receivers.get(i) != null)
                receivers.get(i).disconnect();
        }
    }

}
