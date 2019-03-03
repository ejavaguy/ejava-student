FROM jboss/wildfly:13.0.0.Final

USER root

RUN yum -y install net-tools less && yum clean all

COPY src/main/resources /opt/jboss/wildfly/
RUN chown -R jboss:0 ${JBOSS_HOME} \
  && chmod -R g+rw ${JBOSS_HOME}

USER jboss

RUN /opt/jboss/wildfly/bin/add-user.sh admin password1! --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
