# Weather Data Visualizer

This java program reads weather data stored in a SQL database and render
diagrams and weather statistics in HTML (or any format).
Freemarker is used as template language.

See https://github.com/goeh/weather-collector for how data is downloaded from a Davis weather station.

## Prerequisites

### git

To download and build from source, git must be installed on the Raspberry Pi.
To install git, use the following command:

    sudo apt-get install git

### Java

Java must be installed on the Raspberry Pi prior to building and running the programs.
To install the default JDK, use the following command:

    sudo apt-get install default-jdk

Or to install a specific version:

    sudo apt-get install openjdk-11-jdk

## Build

    ./gradlew

When the build finish successfully, you will find `weather-visualizer.zip` in ./build/distributions.
Extract the archive where you want to install the program, for example in /home/pi/weather.
Then go to the folder where you extracted the archive and start the weather visualizer.

## Run

    bin/weather-visualizer $PWD/web $PWD/templates

The default database engine is H2 (www.h2database.com) and data is read from a file called `weather-db.mv.db`.
You can configure another JDBC database using the `visualizer.jdbc.xxx` properties (see below).
A suitable JDBC driver .jar must be located in the weather-visualizer-1.3.1/lib folder at runtime.

To make it easier to start the visualizer from `cron` or from the command line, create a start script.

### Start script

    #!/bin/bash
    #
    WEATHER_HOME=$HOME/weather
    VISUAL_HOME=$WEATHER_HOME/weather-visualizer-1.3.1
    
    export WEATHER_VISUALIZER_OPTS="-Djava.util.logging.config.file=$VISUAL_HOME/visualizer-logging.properties"
    cd $VISUAL_HOME
    bin/weather-visualizer $VISUAL_HOME/web $VISUAL_HOME/templates
    
    rsync -rltp --chmod=g+r,o+r web/ username@my.webserver.com:htdocs/weather

### Sample visualizer.properties

Put `visualizer.properties` in the `weather-visualizer-1.3.1` directory and `cd` to that directory before you start the program.

    visualizer.jdbc.driver=com.mysql.jdbc.Driver
    visualizer.jdbc.url=jdbc:mysql://localhost:3306/weather?user=weather&password=weather
    visualizer.outputDir=web

### Sample visualizer-logging.properties

Put `visualizer-logging.properties` in the `weather-visualizer-1.3.1` directory.

Log to console (debug logging)

    handlers = java.util.logging.ConsoleHandler

    java.util.logging.ConsoleHandler.level = INFO
    java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
    java.util.logging.SimpleFormatter.format=%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s %4$s: %5$s%n

    se.technipelago.level = ALL

Log to file (production logging)

    handlers = java.util.logging.FileHandler
    
    java.util.logging.FileHandler.level = INFO
    java.util.logging.FileHandler.pattern = collector.log
    java.util.logging.FileHandler.limit = 100000
    java.util.logging.FileHandler.count = 5
    java.util.logging.FileHandler.append= true
    java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter
    java.util.logging.SimpleFormatter.format=%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s %4$s: %5$s%n


### Sample HTML template (index.html)

See the class `se.technipelago.weather.template.PageMaker`, it includes a hard-coded list of template names
that it uses to merge with weather data to produce static HTML files. Templates use Freemarker syntax
to insert weather data.

    <ul>
        <li>Temperature: <strong>${temp_out?string("#0.0")}</strong> &deg;C, feels like: <strong>${chill?string("#0.0")}</strong> &deg;C</li>
        <li>Air pressure: <strong>${barometer}</strong> millibar and <strong>
            <#switch bar_trend>
              <#case -60>
                 rapid falling.
                 <#break>
              <#case -20>
                 falling.
                 <#break>
              <#case 0>
                 stable.
                 <#break>
              <#case 20>
                 raising.
                 <#break>
              <#case 60>
                 rapid raising.
                 <#break>
              <#default>
                 ?
            </#switch>
        </strong></li>
    </ul>
