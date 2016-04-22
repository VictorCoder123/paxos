package com.rti.paxos.utils;

/**
 * String parser parses message in Paxos network as all
 * messages are written in simple string format with
 * ID and proposal number.
 */
public class StringParser {
    private String ID;
    private int proposal;

    public StringParser(String msg){
        String[] list = msg.split(" ");
        ID = list[0];
        proposal = Integer.parseInt(list[1]);
    }

    public String getID(){ return ID; }

    public int getProposal(){ return proposal; }

    public static String createMsg(String id, int proposal){
        return id + " " + Integer.toString(proposal);
    }
}
