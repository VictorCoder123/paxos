package com.rti.paxos.utils;

import java.util.UUID;
import com.rti.paxos.roles.Proposer;

/**
 * Created by zhangqishen on 4/22/16.
 */
public class RunProposer {
    public static void main(String[] args) {
        System.out.println("Start Simulation");
        // Create unique ID to each proposer in order to be recognized by acceptors.
        UUID id = UUID.randomUUID();
        Proposer proposer = new Proposer(id.toString(), 2);
        proposer.start();
    }
}
