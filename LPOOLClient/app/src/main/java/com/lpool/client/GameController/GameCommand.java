package com.lpool.client.GameController;

import com.lpool.client.Network.Connector;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by André on 05/06/2015.
 */
public class GameCommand {
    private Connector.ProtocolCmd cmd;
    private ArrayList<Object> arguments;

    public GameCommand(String cmdStr) {
        Scanner sc = new Scanner(cmdStr);
        sc.useLocale(Locale.US);
        arguments = new ArrayList<Object>();

        cmd = null;
        if (!sc.hasNextInt())
        {
            sc.close();
            return;
        }

        // Decode command
        int cmd_int = sc.nextInt();
        if (cmd_int < 0 || cmd_int >= Connector.ProtocolCmd.values().length)
        {
            sc.close();
            return;
        }
        cmd = Connector.ProtocolCmd.values()[cmd_int];

        switch (cmd) {
            case KICK:
            case WAIT:
            case BIH:
                break;
            case PLAY:
                if(!sc.hasNextInt())
                    return;
                int ball_type = sc.nextInt();
                arguments.add(ball_type);
                break;
            case END:
                if (!sc.hasNextInt()) {
                    sc.close();
                    cmd = null;
                    return;
                }
                Boolean win = (sc.nextInt() != 0) ? true : false;
                arguments.add(win);

                if (!sc.hasNextInt()) {
                    sc.close();
                    cmd = null;
                    return;
                }
                Connector.EndReason reason = Connector.EndReason.values()[sc.nextInt()];
                arguments.add(reason);
                break;
            default:
                cmd = null;
                break;
        }
    }

    public Connector.ProtocolCmd getCmd() {
        return cmd;
    }

    public ArrayList<Object> getArgs() {
        return arguments;
    }

    public static GameCommand decodeMessage(String cmdStr){
        return new GameCommand(cmdStr);
    }
}
