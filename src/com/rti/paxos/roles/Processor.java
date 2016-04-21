package com.rti.paxos.roles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.StringDataReader;
import com.rti.dds.type.builtin.StringDataWriter;
import com.rti.dds.type.builtin.StringTypeSupport;


public class Processor extends DataReaderAdapter {
    // For clean shutdown sequence
    private static boolean shutdown_flag = false;
    private DomainParticipant participant;
    private StringDataReader dataReader;
    private StringDataWriter dataWriter;
    private Topic topic;

    /**
     * Processor Constructor to create topic, domain participant and
     * dataReader to be used for communication.
     */
    public Processor(){
        // Create the DDS Domain participant on domain ID 0
        participant = DomainParticipantFactory.get_instance().create_participant(
                0, // Domain ID = 0
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        // Fail to create domain participant.
        if (participant == null) System.err.println("Unable to create domain participant");

        // Create new topic
        topic = createTopic("Hello World");
        if(topic == null) return;

        // Create reader and writer based on topic
        dataReader = createReader(topic);
        dataWriter = createWriter(topic);
    }

    /**
     * Generate a new topic based on given string.
     * @param topicValue
     * @return Topic
     */
    public Topic createTopic(String topicValue) {
        Topic new_topic = participant.create_topic(
                topicValue,
                StringTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (new_topic == null) System.err.println("Unable to create topic.");
        return new_topic;
    }

    /**
     * Create new data reader given a topic
     * @param topic
     * @return StringDataReader
     */
    public StringDataReader createReader(Topic topic){
        // Create the data reader using the default subscriber
        StringDataReader new_dataReader = (StringDataReader) participant.create_datareader(
                topic,
                Subscriber.DATAREADER_QOS_DEFAULT,
                new Processor(),         // Listener
                StatusKind.DATA_AVAILABLE_STATUS);
        // Fail to create dataReader
        if (new_dataReader == null) System.err.println("Unable to create DDS Data Reader");
        return new_dataReader;
    }

    /**
     * Create new data writer given a topic
     * @param topic
     * @return StringDataWriter
     */
    public StringDataWriter createWriter(Topic topic){
        // Create the data writer using the default publisher
        StringDataWriter new_dataWriter = (StringDataWriter) participant.create_datawriter(
                topic,
                Publisher.DATAWRITER_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (new_dataWriter == null) System.err.println("Unable to create data writer\n");
        return new_dataWriter;
    }

    /**
     * Use data writer to publish or broadcast message to other
     * existing receivers given certain topic.
     * @param message
     */
    public void publish(String message){
        dataWriter.write(message, InstanceHandle_t.HANDLE_NIL);
    };

    /**
     * This method gets called back by DDS when one or more data samples have
     * been received.
     */
    public void on_data_available(DataReader reader) {
        StringDataReader stringReader = (StringDataReader) reader;
        SampleInfo info = new SampleInfo();
        for (;;) {
            try {
                String sample = stringReader.take_next_sample(info);
                if (info.valid_data) {
                    System.out.println(sample);
                    if (sample.equals("")) {
                        shutdown_flag = true;
                    }
                }
            } catch (RETCODE_NO_DATA noData) {
                // No more data to read
                break;
            } catch (RETCODE_ERROR e) {
                // An error occurred
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the simulation of processor on network
     */
    public void start(){
        System.out.println("Ready to read data.");
        System.out.println("Press CTRL+C to terminate.");
        for (;;) {
            try {
                Thread.sleep(2000);
                if(shutdown_flag) break;
            } catch (InterruptedException e) {
                // Nothing to do...
            }
        }
        // Clean up after termination.
        System.out.println("Shutting down...");
        participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }

    // Create and run a processor.
    public static final void main(String[] args) {
        // Create new processor and start it.
        Processor processor = new Processor();
        processor.start();
    }
}