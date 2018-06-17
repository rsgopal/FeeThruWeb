# feethru_os_image
FROM openshift/base-centos7

LABEL maintainer="Ramamurthy Gopalakrishnan <rsgopal@gmail.com>"

ENV GRADLE_VERSION 4.5.1

LABEL io.k8s.description="Platform for building FeeThruWeb" \
      io.k8s.display-name="builder FeeThruWeb" \
      io.openshift.expose-services="8080:http" \
      io.openshift.tags="builder,java.gradle" \
      org.jboss.deployments-dir="/deployments" 

# Install Java
RUN INSTALL_PKGS="java-1.8.0-openjdk java-1.8.0-openjdk-devel" && \
    yum install -y $INSTALL_PKGS && \
    rpm -V $INSTALL_PKGS && \
    yum clean all -y && \
    mkdir -p /opt/s2i/destination
    
# Install Gradle
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip && \
    mkdir /opt/gradle && \
    unzip -d /opt/gradle gradle-${GRADLE_VERSION}-bin.zip && \
    rm gradle-${GRADLE_VERSION}-bin.zip && \
    ln -s /opt/gradle/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle

COPY ./s2i/bin/ /usr/libexec/s2i
RUN chown -R 1001:1001 /opt/app-root

USER 1001

EXPOSE 8080

CMD ["/usr/libexec/s2i/usage"]