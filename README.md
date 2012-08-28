# Weather Data Visualizer

This java program reads weather data stored in a SQL database and render
diagrams and weather statistics in HTML (or any format).
Freemarker is used as template language.

## Start script

    #!/bin/bash
    #
    VISUAL_HOME=$HOME

    cd $VISUAL_HOME

    java -classpath "$VISUAL_HOME/visualizer.jar:$VISUAL_HOME/commons-net-2.2.jar:$VISUAL_HOME/freemarker-2.3.16.jar:$VISUAL_HOME/jcommon-1.0.15.jar:$VISUAL_HOME/jfreechart-1.0.12.jar:$VISUAL_HOME/mysql-connector-java-5.0.11-bin.jar" se.technipelago.weather.Publisher $VISUAL_HOME/web $VISUAL_HOME/templates
