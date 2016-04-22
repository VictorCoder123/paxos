package com.rti.paxos.utils;

import com.rti.paxos.roles.Acceptor;

/**
 * Created by zhangqishen on 4/22/16.
 */
public class RunAcceptor {
    public static void main(String[] args) {
        System.out.println("Start Simulation");
        Acceptor acceptor = new Acceptor();
        acceptor.start();
    }
}
