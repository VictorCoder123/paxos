package com.rti.paxos.roles;

import java.util.Random;

import com.rti.dds.topic.Topic;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.type.builtin.StringDataReader;
import com.rti.paxos.utils.StringParser;

public class Acceptor extends Processor {

    private int value = 0;
    private Random rand = new Random();

    public Acceptor(){
        topic = createTopic("Prepare");
        Topic writeTopic = createTopic("Promise");
        dataReader = createReader(topic);
        dataWriter = createWriter(writeTopic);
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
                // Send back promise only it's larger than current value
                if (info.valid_data && sample_value > value) {
                    System.out.println(sample_value);
                    value = sample_value;
                    Thread.sleep(rand.nextInt(2000));
                    publish(sample); // Send it back to proposers as accepted.
                }
            }
            catch (RETCODE_NO_DATA noData) { break;}
            catch (RETCODE_ERROR e) { e.printStackTrace();}
            catch (InterruptedException e) {}
        }
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
