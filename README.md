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
* Build Docker image in format of **<repo>/<imagename>:tag** from Dockerfile in main directory
```
docker build -t zhangqs/rti_dds:5.2.0 .
```
* Run Docker container in command
```
docker run -i -t zhangqs/rti_dds:5.2.0 /bin/bash
```
### Run simple pub-sub example
* Run publisher inside the container
```
cd $RTI_WORKSPACE/5.2.0/examples/connext_dds/java/hello_simple
Build the example with: ./build.sh
Run publisher with: ./runPub.sh
```

* Run subscriber inside container
```
cd $RTI_WORKSPACE/5.2.0/examples/connext_dds/java/hello_simple
Build the example with: ./build.sh
Run subscriber with: ./runSub.sh
```