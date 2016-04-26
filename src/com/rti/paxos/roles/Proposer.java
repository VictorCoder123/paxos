package com.rti.paxos.roles;

import java.util.Random;

import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.type.builtin.StringDataReader;

import com.rti.dds.type.builtin.StringDataWriter;
import com.rti.paxos.utils.StringParser;

public class Proposer extends Processor {

    public class ProposerAdaptor extends DataReaderAdapter {

        public void on_data_available(DataReader reader) {
            StringDataReader stringReader = (StringDataReader) reader;
            SampleInfo info = new SampleInfo();
            for (;;) {
                try {
                    String sample = stringReader.take_next_sample(info);
                    StringParser sp = new StringParser(sample);
                    int sample_value = sp.getProposal();
                    String sample_id = sp.getID();
                    // Ignore response if it is from previous session or other proposer.
                    if (info.valid_data && sample_value >= proposal
                            && ID.equalsIgnoreCase(sample_id)) {
                        System.out.println("Receive promise from Acceptor: " + Integer.toString(sample_value));
                        numAccepted++;
                    }
                    else {
                        System.out.println("Message invalid or ignored.");
                    }
                }
                catch (RETCODE_NO_DATA noData) { break;}
                catch (RETCODE_ERROR e) { e.printStackTrace();}
            }
        }
    }

    // Initialize with default values
    private String ID;
    private int proposal = 0;
    private Random rand = new Random();
    private int numQuorum;
    private int numAccepted = 0;

    // Create topics
    private Topic prepareTopic;
    private Topic promiseTopic;
    private Topic acceptTopic;

    // Create readers and writers
    private StringDataReader promiseReader;
    private StringDataWriter prepareWriter;
    private StringDataWriter acceptWriter;

    /**
     * Constructor for Proposer
     */
    public Proposer(String id, int Quorum){
        super();

        numQuorum = Quorum;
        ID = id;

        prepareTopic = createTopic("Prepare");
        promiseTopic = createTopic("Promise");
        acceptTopic = createTopic("Accepted");

        promiseReader = createReader(promiseTopic, new ProposerAdaptor());
        prepareWriter = createWriter(prepareTopic);
        acceptWriter = createWriter(acceptTopic);
    }

    /**
     * Send message in combination of ID and proposal value
     * to all existing acceptors
     */
    public void sendProposal(){
        if(numAccepted >= numQuorum/2 + 1){
            // Current proposal reach consensus and send accepted message back to all acceptors
            System.out.println("Reach consensus to commit current value of proposal: "
                    + Integer.toString(proposal));
            String acceptMsg = StringParser.createMsg(ID, proposal);
            publish(acceptWriter, acceptMsg);
        }
        proposal += rand.nextInt(10);
        numAccepted = 0;
        String msg = StringParser.createMsg(ID, proposal);
        publish(prepareWriter, msg);
        System.out.println(ID + " sends proposal " + Integer.toString(proposal));
    }

    @Override
    public void start(){
        for (;;) {
            try {
                // Send increasing every two seconds
                sendProposal();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Clean up after termination.
                System.out.println("Shutting down...");
                participant.delete_contained_entities();
                DomainParticipantFactory.get_instance().delete_participant(participant);
            }
        }
    }
}
