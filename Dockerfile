# Dockerfile for installing RTI DDS Connext Professional 5.2.0 on Ubuntu 14.04
#
# To build the docker image:
# 1. Create new build directory
#	Example: ~/dds
# 2. Place this Dockerfile in build directory
#	Example: ~/dds/Dockerfile
# 3. Place target & host bundles and the license file in sub-directory named dep/
#	Example: ~/dds/dep/rti_connext_dds-5.2.0-pro-host-x64Linux.run
#		 ~/dds/dep/rti_connext_dds-5.2.0-pro-target-x64Linux3gcc4.8.2.rtipkg
#		 ~/dds/dep/rti_license.dat
# 4. Build Docker image from base directory with:
#	docker build -t <repo>/<imagename>:tag .
#	Example: docker build -t kharesp/rti_dds:5.2.0 .
# 5. Run Docker container with:
#	docker run -t -i <repo>/<imagename>:tag /bin/bash
#	Example: docker run -t -i kharesp/rti_dds:5.2.0 /bin/bash
#
#
# To run hello_simple pub-sub example:
# 1. Run publisher docker container in one terminal with:
#    	docker run -t -i <repo>/<imagename>:tag /bin/bash
#    Once inside the container:
#	a. cd $RTI_WORKSPACE/5.2.0/examples/connext_dds/java/hello_simple
#	b. Build the example with: ./build.sh
#	c. Run publisher with: ./runPub.sh
#
# 2. Run subscriber docker container in another terminal with:
#    	docker run -t -i <repo>/<imagename>:tag /bin/bash
#    Once inside the container:
#	a. cd $RTI_WORKSPACE/5.2.0/examples/connext_dds/java/hello_simple
#	b. Build the example with: ./build.sh
#	c. Run subscriber with: ./runSub.sh


# Base image
FROM ubuntu:14.04

MAINTAINER shweta khare <shweta.p.khare@vanderbilt.edu>

ENV CONNEXT_VER=5.2.0 \
    	CONNEXT_PACKAGE_TYPE=pro\
	HOME=/home/root \
	HOST_PLATFORM=x64Linux \
	GCC=3gcc4.8.2

# Install Oracle JDK 8
RUN DEBIAN_FRONTEND=noninteractive apt-get update && \
 	apt-get install -y python-software-properties software-properties-common && \
 	add-apt-repository ppa:webupd8team/java && \
	apt-get update && \
	echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | \
		debconf-set-selections && \
	apt-get install -y oracle-java8-installer && \
        apt-get install -y ant

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-8-oracle \
	PATH=$PATH:/usr/lib/jvm/java-8-oracle/bin

# Install RTI DDS Connext 5.2.0
WORKDIR /home/root/dev_environ
COPY dep/ dds/
RUN INSTALLER=rti_connext_dds-$CONNEXT_VER-$CONNEXT_PACKAGE_TYPE-host-$HOST_PLATFORM.run && \
 	chmod +x dds/$INSTALLER && \
 	sync && \
 	./dds/$INSTALLER --mode unattended --prefix /home/root/dev_environ/dds && \
        mv dds/rti_license.dat dds/rti_connext_dds-$CONNEXT_VER/

RUN BUNDLE=rti_connext_dds-$CONNEXT_VER-$CONNEXT_PACKAGE_TYPE-target-$HOST_PLATFORM$GCC.rtipkg && \
	dds/rti_connext_dds-$CONNEXT_VER/bin/rtipkginstall ./dds/$BUNDLE

# Set RTI DDS Connext environment variables
ENV RTI_LICENSE_FILE=$HOME/dev_environ/dds/rti_connext_dds-$CONNEXT_VER/rti_license.dat \
	 NDDSHOME=$HOME/dev_environ/dds/rti_connext_dds-$CONNEXT_VER \
	 LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$HOME/dev_environ/dds/rti_connext_dds-$CONNEXT_VER/lib/$HOST_PLATFORM$GCC \
	 RTI_WORKSPACE=$HOME/rti_workspace \
	 PATH=$PATH:$HOME/dev_environ/dds/rti_connext_dds-$CONNEXT_VER/bin

# Change workdir
WORKDIR /home/root/workspacer