package com.rti.paxos.roles;

import java.util.Random;

import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.type.builtin.StringDataReader;
import com.rti.dds.type.builtin.StringDataWriter;
import com.rti.paxos.utils.StringParser;

public class Acceptor extends Processor {

    /**
     * Reader Adaptor to deal with prepare message from proposer
     */
    public class AcceptorPrepareAdaptor extends DataReaderAdapter {
        public void on_data_available(DataReader reader) {
            StringDataReader stringReader = (StringDataReader) reader;
            SampleInfo info = new SampleInfo();
            for (;;) {
                try {
                    String sample = stringReader.take_next_sample(info);
                    StringParser sp = new StringParser(sample);
                    int sample_value = sp.getProposal();
                    // Send back promise only it's larger than current value
                    if (info.valid_data && sample_value > value) {
                        System.out.println("Receive proposal " + sample + " and return promise.");
                        value = sample_value;
                        Thread.sleep(rand.nextInt(2000));
                        publish(promiseWriter, sample); // Send it back to proposers as accepted.
                    }
                }
                catch (RETCODE_NO_DATA noData) { break;}
                catch (RETCODE_ERROR e) { e.printStackTrace();}
                catch (InterruptedException e) {}
            }
        }
    }

    /**
     * Reader Adaptor to deal with accepted message from proposer after
     * a proposal reaches consensus by all other acceptors.
     */
    public class AcceptorAcceptAdaptor extends DataReaderAdapter {
        public void on_data_available(DataReader reader) {
            StringDataReader stringReader = (StringDataReader) reader;
            SampleInfo info = new SampleInfo();
            for (;;) {
                try {
                    String sample = stringReader.take_next_sample(info);
                    StringParser sp = new StringParser(sample);
                    int sample_value = sp.getProposal();
                    // Send back promise only it's larger than current value
                    if (info.valid_data){
                        System.out.println("Message from proposer to commit accepted value: "
                                + Integer.toString(sample_value));
                    }
                }
                catch (RETCODE_NO_DATA noData) { break;}
                catch (RETCODE_ERROR e) { e.printStackTrace();}
            }
        }
    }

    private int value = 0;
    private Random rand = new Random();

    // Create topics
    private Topic prepareTopic;
    private Topic promiseTopic;
    private Topic acceptTopic;

    private StringDataReader prepareReader;
    private StringDataWriter promiseWriter;
    private StringDataReader acceptReader;

    public Acceptor(){
        super();

        prepareTopic = createTopic("Prepare");
        promiseTopic = createTopic("Promise");
        acceptTopic = createTopic("Accepted");

        prepareReader = createReader(prepareTopic, new AcceptorPrepareAdaptor());
        acceptReader = createReader(acceptTopic, new AcceptorAcceptAdaptor());
        promiseWriter = createWriter(promiseTopic);
    }

    @Override
    public void start(){
        for (;;) {
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {}
        }
    }
}
