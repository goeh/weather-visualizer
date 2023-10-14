FROM eclipse-temurin:17.0.8_7-jre-jammy

ARG ZIP=visualizer.zip
ARG HOME=/opt/weather
ENV VISUALIZER_HOME=$HOME
ENV VISUALIZER_CONFIG=$VISUALIZER_HOME/config
ENV VISUALIZER_TEMPLATES=$VISUALIZER_HOME/templates
ENV VISUALIZER_OUTPUT=$VISUALIZER_HOME/web
ENV VISUALIZER_REMOTE="user@server.com:/var/wwwroot/weather"
ENV VISUALIZER_OPTS="-Djava.util.logging.config.file=$VISUALIZER_CONFIG/visualizer-logging.properties"

RUN apt-get update && apt-get -y -qq install unzip rsync ssh cron && which cron && rm -rf /etc/cron.*/*

RUN ls build/distributions

ADD build/distributions/$ZIP /tmp/visualizer.zip
RUN mkdir -p $VISUALIZER_HOME && unzip -d $VISUALIZER_HOME /tmp/visualizer.zip && rm -f /tmp/visualizer.zip

ADD crontab.txt /etc/crontab
RUN chmod 644 /etc/crontab

ADD start.sh $VISUALIZER_HOME/start.sh
RUN chmod 750 $VISUALIZER_HOME/start.sh

ADD entrypoint.sh /entrypoint.sh
RUN chmod 750 /entrypoint.sh
RUN mkdir -p /root/.ssh && chmod 755 /root/.ssh

ENTRYPOINT ["/entrypoint.sh"]
CMD ["cron", "-f", "-l", "2"]
