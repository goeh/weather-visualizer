# Weather Data Visualizer

This java program reads weather data stored in a SQL database and render
diagrams and weather statistics in HTML (or any format).
Freemarker is used as template language.

## Build

    ./gradlew
     
## Start script

    #!/bin/bash
    #
    WEATHER_HOME=$HOME
    VISUAL_HOME=$WEATHER_HOME/weather-visualizer-1.3
    
    export VISUALIZER_OPTS="-Djava.util.logging.config.file=$VISUAL_HOME/visualizer-logging.properties"
    cd $VISUAL_HOME
    bin/weather-visualizer $VISUAL_HOME/web $VISUAL_HOME/templates
    
    rsync -rltp --chmod=g+r,o+r web/ username@my.webserver.com:htdocs/weather

## Sample visualizer.properties

Put `visualizer.properties` in the `weather-visualizer-1.3` directory and `cd` to that directory before you start the program.

    visualizer.jdbc.driver=com.mysql.jdbc.Driver
    visualizer.jdbc.url=jdbc:mysql://localhost:3306/weather?user=weather&password=weather
    visualizer.outputDir=web

## Sample visualizer-logging.properties

Put `visualizer-logging.properties` in the `weather-visualizer-1.3` directory.

    handlers = java.util.logging.FileHandler
    
    java.util.logging.FileHandler.level = ALL
    java.util.logging.FileHandler.pattern = visualizer.log
    java.util.logging.FileHandler.limit = 100000
    java.util.logging.FileHandler.count = 5
    java.util.logging.FileHandler.append= true
    java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter
    java.util.logging.SimpleFormatter.format=%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s %4$s: %5$s%n
    
    se.technipelago.level = INFO

## Sample HTML template (index.html)

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
