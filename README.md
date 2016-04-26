## Paxos implementation in Docker

### Description
Use RTI DDS Connext Professional 5.2.0 on Ubuntu 14.04 as basis of distributed system in multi-containers Docker environment and implement Paxos algorithm.

### Install docker and create containers from image
* Clone repository and open project folder.
```
git clone https://github.com/VictorCoder123/paxos.git
```
* Place target,host bundles and the license file in sub-directory named `dep/`.
```
 ~/paxos/dep/rti_connext_dds-5.2.0-pro-host-x64Linux.run
 ~/paxos/dep/rti_connext_dds-5.2.0-pro-target-x64Linux3gcc4.8.2.rtipkg
 ~/paxos/dep/rti_license.dat
```
* Build Docker image in format of `<repo>/<imagename>:tag` from Dockerfile in main directory
```
docker build -t zhangqs/rti_dds:5.2.0 .
```
* Run Docker container in command
```
docker run -i -t zhangqs/rti_dds:5.2.0 /bin/bash
```

### Run Paxos example in multiple containers
* In bash script `runContainer.sh`, replace Image ID and path with your own and add more containers if you need more, then run this script in command line.
```
// Customize this command with local configuration in 'runContainer.sh'
docker run -d -v /home/qishen/Desktop/Projects/paxos:/home/root/rti_workspace/5.2.0/examples/connext_dds/java/paxos -p 5003:80 -i 4878a6a8e076 
// Run this script
sh ./runContainer.sh
```
* By default number of Acceptors is 2 and if you create more Acceptors, then parameter in `RunProposer.java` needs to be changed.
```
public static void main(String[] args) {
    System.out.println("Start Simulation");
    UUID id = UUID.randomUUID();
    // Change number of acceptors below.
    Proposer proposer = new Proposer(id.toString(), 2);
    proposer.start();
}
```
* Run Acceptor inside the container
```
cd $RTI_WORKSPACE/5.2.0/examples/connext_dds/java/paxos/
Build the example with: ./build.sh
Run publisher with: ./runAcceptor.sh
```

* Run Proposer inside container
```
cd $RTI_WORKSPACE/5.2.0/examples/connext_dds/java/paxos/
Build the example with: ./build.sh
Run subscriber with: ./runProposer.sh
```