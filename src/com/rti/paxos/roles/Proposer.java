package com.rti.paxos.roles;

import java.util.Random;

import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.type.builtin.StringDataReader;
import com.rti.paxos.utils.StringParser;

public class Proposer extends Processor {

    private String ID;
    private int proposal = 0;
    private Random rand = new Random();
    private int numQuorum;
    private int numAccepted = 0;

    /**
     * Constructor for Proposer
     */
    public Proposer(String id, int Quorum){
        super();
        numQuorum = Quorum;
        ID = id;
        topic = createTopic("Prepare");
        Topic readTopic = createTopic("Promise");
        dataWriter = createWriter(topic);
        dataReader = createReader(readTopic);
    }

    /**
     * Send message in combination of ID and proposal value
     * to all existing acceptors
     */
    public void sendProposal(){
        proposal += rand.nextInt(10);
        if(numAccepted >= numQuorum/2 + 1){
            // TODO: Commit value in current proposal, otherwise ignore it.
        }
        numAccepted = 0;
        String msg = StringParser.createMsg(ID, proposal);
        publish(msg);
        System.out.println(ID + " sends proposal " + Integer.toString(proposal));
    }

    @Override
    public StringDataReader createReader(Topic topic){
        // Create the data reader using the default subscriber
        StringDataReader new_dataReader = (StringDataReader) participant.create_datareader(
                topic,
                Subscriber.DATAREADER_QOS_DEFAULT,
                new Proposer("", 0),         // Listener
                StatusKind.DATA_AVAILABLE_STATUS);
        // Fail to create dataReader
        if (new_dataReader == null) System.err.println("Unable to create DDS Data Reader");
        return new_dataReader;
    }

    @Override
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
