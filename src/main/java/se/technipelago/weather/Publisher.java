/*
 *  Copyright 2006 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package se.technipelago.weather;

import se.technipelago.weather.chart.Generator;
import se.technipelago.weather.ftp.FtpTransport;
import se.technipelago.weather.template.PageMaker;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Goran Ehrsson <goran@technipelago.se>
 */
public class Publisher {

    private static final Logger log = Logger.getLogger(Publisher.class.getName());
    private static final String PROPERTIES_FILE = "visualizer.properties";

    private static Properties getProperties() {
        final Properties prop = new Properties();
        InputStream fis = null;
        try {
            File file = new File(PROPERTIES_FILE);
            if(file.exists()) {
                fis = new FileInputStream(file);
                prop.load(fis);
            } else {
                log.log(Level.WARNING, PROPERTIES_FILE + " not found, using default values.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
        }
        return prop;
    }

    public static void main(String[] args) {
        final List<File> files = new ArrayList<File>();
        String outputDir = null;
        if (args.length > 0) {
            outputDir = args[0];
            if (outputDir.endsWith("/")) {
                outputDir = outputDir.substring(0, outputDir.length() - 1);
            }
        }

        // Generate charts.
        Generator generator = new Generator();
        generator.setOutputDirectory(outputDir);
        generator.init();
        Map<String, Object> data = generator.getWeatherData();
        add(files, generator.generateCurrentCharts(data), outputDir);
        add(files, generator.generateHistoryCharts(-30, 31), outputDir);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        //for(int year = 2005; year < 2009; year++) {
        add(files, generator.generateMonthlyCharts(year), outputDir);
        add(files, generator.generateYearlyCharts(year), outputDir);
        //}
        // Since we fetch data from the weather station every 10 minutes and we
        // upload new pages to Internet after 3 minutes, we consider the HTML page
        // stale after 15 minutes. Therefore we add an Date object that we can
        // use in the 'Expires' HTTP header.
        Date timestamp = (Date) data.get("timestamp");
        if (timestamp != null) {
            Date expires = new Date(timestamp.getTime() + 1000 * 60 * 15);
            data.put("expires", expires);
        }
        // Generate HTML files.
        final PageMaker pm = new PageMaker();
        pm.setTemplateDirectory(args[1]);
        pm.setOutputDirectory(outputDir);
        add(files, pm.generateHTML(data), outputDir);

        generator.cleanup();

        // Upload files to web hotel.
        try {
            final Properties prop = getProperties();
            final String host = prop.getProperty("visualizer.ftp.host", "");
            if(host != null && host.trim().length() > 0) {
                final String username = prop.getProperty("visualizer.ftp.username", "anonymous");
                final String password = prop.getProperty("visualizer.ftp.password", "anonymous");
                final String directory = prop.getProperty("visualizer.ftp.directory", "/");
                new FtpTransport().sendFiles("ftp://" + username + ":" + password + "@" + host + directory, files.toArray(new File[files.size()]));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to upload files", e);
            throw new RuntimeException(e);
        }
    }

    private static void add(List<File> list, String[] array, final String dir) {
        for (String filename : array) {
            list.add(new File(dir != null ? dir + "/" + filename : filename));
        }
    }
}
