package com.rti.paxos.roles;

import java.util.Random;
import com.rti.paxos.roles.Processor;

public class Proposer extends Processor {
    private int proposal = 0;

    /**
     * Constructor for Proposer role of Processor
     */
    public Proposer(){
        super();
    }

    /**
     * Generate random proposal number, which should be larger than
     * last proposal sent by current proposer.
     * @return Integer
     */
    public int generateProposal(){
        Random rand = new Random();
        int new_proposal = proposal + rand.nextInt();
        return new_proposal;
    }

    /**
     * Send proposal to all existing acceptors.
     */
    public void sendProposal(){
        proposal = generateProposal();
    }
}
