#!/bin/sh

docker run -d -v /home/qishen/Desktop/Projects/paxos:/home/root/rti_workspace/5.2.0/examples/connext_dds/java/paxos -p 5000:80 -i 4878a6a8e076 

docker run -d -v /home/qishen/Desktop/Projects/paxos:/home/root/rti_workspace/5.2.0/examples/connext_dds/java/paxos -p 5001:80 -i 4878a6a8e076 

docker run -d -v /home/qishen/Desktop/Projects/paxos:/home/root/rti_workspace/5.2.0/examples/connext_dds/java/paxos -p 5002:80 -i 4878a6a8e076 

docker run -d -v /home/qishen/Desktop/Projects/paxos:/home/root/rti_workspace/5.2.0/examples/connext_dds/java/paxos -p 5003:80 -i 4878a6a8e076 
