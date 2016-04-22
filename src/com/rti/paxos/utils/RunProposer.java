package com.rti.paxos.utils;

import java.util.UUID;
import com.rti.paxos.roles.Proposer;

/**
 * Created by zhangqishen on 4/22/16.
 */
public class RunProposer {
    public static void main(String[] args) {
        System.out.println("Start Simulation");
        UUID id = UUID.randomUUID();
        Proposer proposer = new Proposer(id.toString(), 4);
        proposer.start();
    }
}
