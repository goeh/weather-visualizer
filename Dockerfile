FROM eclipse-temurin:17.0.8_7-jre-jammy

ARG UID=1005
ARG GID=100
ARG HOME=/opt/weather
ENV VISUALIZER_HOME=$HOME
ENV VISUALIZER_CONFIG=$VISUALIZER_HOME/config
ENV VISUALIZER_TEMPLATES=$VISUALIZER_HOME/templates
ENV VISUALIZER_OUTPUT=$VISUALIZER_HOME/web
ENV VISUALIZER_REMOTE="user@server.com:/var/wwwroot/weather"
ENV VISUALIZER_OPTS="-Djava.util.logging.config.file=$VISUALIZER_CONFIG/visualizer-logging.properties"

RUN apt-get update && apt-get -y -qq install unzip rsync ssh
#RUN groupadd -g $GID users
RUN useradd -m -u $UID -g $GID weather

ADD ./build/distributions/visualizer.zip /tmp/visualizer.zip
ADD ./entrypoint.sh $VISUALIZER_HOME/entrypoint.sh
RUN mkdir -p $VISUALIZER_HOME && unzip -d $VISUALIZER_HOME /tmp/visualizer.zip && rm -f /tmp/visualizer.zip
RUN chown -R ${UID}:${GID} $VISUALIZER_HOME && chmod 750 $VISUALIZER_HOME/entrypoint.sh

USER $UID
WORKDIR $VISUALIZER_HOME

ENTRYPOINT exec $VISUALIZER_HOME/entrypoint.sh
