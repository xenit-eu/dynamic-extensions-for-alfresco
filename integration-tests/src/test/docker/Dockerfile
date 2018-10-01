ARG ALFRESCO_BASE_IMAGE
ARG AMPS_DIR

FROM $ALFRESCO_BASE_IMAGE

ARG TOMCAT_DIR=/usr/local/tomcat

RUN mkdir -p $TOMCAT_DIR/amps

COPY $AMPS_DIR $TOMCAT_DIR/amps

RUN sed -i 's|<secure>true</secure>|<secure>false</secure>|g' /usr/local/tomcat/conf/web.xml

RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
            $TOMCAT_DIR/amps $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force