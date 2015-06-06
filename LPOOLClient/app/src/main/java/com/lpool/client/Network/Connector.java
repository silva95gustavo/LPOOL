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

    public static final int HEARTBEAT_TIMEOUT = 30; // seconds
    public static final int HEARTBEAT_INTERVAL = 20; // seconds
    private Timer timer;
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
        PLAY,  // 0(no ball), 1(not-stripes), 2(stripes), 3(black ball)    TODO
        WAIT,
        BIH,
        END // winner(boolean) End.Reason
    }

    public enum BallType {
        NONE,
        SOLID,
        STRIPE,
        BLACK
    }

    // TCP
    private volatile Socket socket;
    private static volatile Socket testSocket;
    private static InetAddress tempServerAddr;
    private static PrintWriter pw;
    // UDP
    private DatagramSocket datagramSocket;

    private String serverIP;
    private int serverPort;
    private Boolean running = true;

    private ArrayList<Receiver> receivers;

    public Connector(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
        running = true;
        timer = new Timer();
        receivers = new ArrayList<Receiver>();
        initializeClientThread(); // Initializes receiver/heartbeat thread
        System.out.println("Connecting to " + serverIP + " " + serverPort);
    }

    public Boolean sendUDPMessage(String message) {

        if(!running)
            return false;

        if (socket == null || datagramSocket == null)
            return false;
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

        if (socket == null)
            return false;
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

    class ClientThread implements Runnable {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                socket = new Socket(serverAddr, serverPort);
                datagramSocket = new DatagramSocket(serverPort);
                initializeReceiverThread();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class receiverThread implements Runnable    {
        @Override
        public void run() {
            startHeartBeat();
            while (running)
            {
                try {
                    System.out.println("Receiving...");
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = br.readLine();
                    if (str == null) {
                        System.out.println("received null");
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
                datagramSocket.disconnect();
            if(socket != null)
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startHeartBeat() {
        timer.schedule(new HeartBeatTask(), HEARTBEAT_INTERVAL * 1000);
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

            if(!gotPONG)
                stopReceivers();
            else {
                timer.schedule(new HeartBeatTask(), HEARTBEAT_INTERVAL * 1000);
            }

        }
    }

    private void stopReceivers() {
        for(int i = 0; i < receivers.size(); i++) {
            if(receivers.get(i) != null)
                receivers.get(i).disconnect();
        }
    }

    public static Boolean isServerRunning(String ip, int port) {

        testSocket = null;
        pw = null;
        final int tempPort = port;
        final String tempIP = ip;
        final Boolean result[] = new Boolean[1];
        result[0] = true;

        Thread attempt = new Thread( new Runnable() {
            public void run() {
                try {
                    tempServerAddr = InetAddress.getByName(tempIP);
                    if(!tempServerAddr.isAnyLocalAddress() && !tempServerAddr.isLinkLocalAddress() && !tempServerAddr.isLoopbackAddress() && !tempServerAddr.isMulticastAddress() && !tempServerAddr.isSiteLocalAddress()) {
                        result[0] = false;
                        System.out.println("serverAddr bad");
                        tempServerAddr = null;
                        return;
                    }
                    testSocket = new Socket(tempServerAddr, tempPort);
                    if(testSocket == null) {
                        System.out.println("NULL");
                    }
                } catch (UnknownHostException e1) {
                    result[0] = false;
                    return;
                } catch (IOException e1) {
                    result[0] = false;
                    return;
                }
            }
        });

        attempt.start();
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            return false;
        }

        System.out.println("1");
        if (testSocket == null) {
            tempServerAddr = null;
            return false;
        }
        System.out.println("2");

        if(!result[0]) {
            attempt.interrupt();
            tempServerAddr = null;
            try {
                testSocket.close();
            } catch(IOException e2) {}
            testSocket = null;
            return false;
        }

        System.out.println("3");
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(testSocket.getOutputStream())), true);
            pw.println("" + Connector.ProtocolCmd.JOIN.ordinal() + " " + '\n');
        } catch (IOException e) {
            System.out.println("4");
            tempServerAddr = null;
            attempt.interrupt();
            try {
                testSocket.close();
            } catch(IOException e2) {}
            pw.close();
            testSocket = null;
            pw = null;
            return false;
        }
        System.out.println("5");

        tempServerAddr = null;
        attempt.interrupt();
        try {
            testSocket.close();
        } catch(IOException e2) {}
        pw.close();
        testSocket = null;
        pw = null;
        return true;
    }
}
