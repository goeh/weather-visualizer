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
package se.technipelago.weather.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.GradientPaintTransformType;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.StandardGradientPaintTransformer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Goran Ehrsson <goran@technipelago.se>
 */
public class Generator {

    public static final int MIN = Integer.MIN_VALUE;
    public static final int MAX = Integer.MAX_VALUE;
    public static final int AVG = 0;
    public static final int SUM = 1;
    public static final int DIAL_WIDTH = 216;
    //public static final int DIAL_WIDTH = 256;
    public static final int DIAL_HEIGHT = 216;
    //public static final int DIAL_HEIGHT = 275;
    private static final Logger log = Logger.getLogger(Generator.class.getName());
    private static final String PROPERTIES_FILE = "visualizer.properties";
    private String outputDir;
    private Connection conn;
    private SqlDialect dialect;

    public static void main(String[] args) {

        Generator generator = new Generator();

        if (args.length > 0) {
            String outputDir = args[0];
            if (outputDir.endsWith("/")) {
                outputDir = outputDir.substring(0, outputDir.length() - 1);
            }
            generator.setOutputDirectory(outputDir);
        }

        // Generate charts.
        generator.init();
        Map<String, Object> data = generator.getWeatherData();
        generator.generateCurrentCharts(data);
        generator.generateHistoryCharts(-30, 31);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        generator.generateMonthlyCharts(year);
        generator.generateYearlyCharts(year);
    }

    public void setOutputDirectory(final String dir) {
        outputDir = dir;
        if (outputDir != null) {
            new File(outputDir).mkdirs();
        }
    }

    public String[] generateCurrentCharts(Map<String, Object> data) {

        List<String> files = new ArrayList<String>();
        try {
            // Current readings.

            // Temperature.
            float temp = (Float) data.get("temp_out");
            int hum = (Integer) data.get("hum_out");
            createTemperatureDial(temp, hum, "temperature.png");
            files.add("temperature.png");

            // Wind speed/direction.
            float speed = (Float) data.get("wind_avg");
            float high = (Float) data.get("wind_high");
            int dir = (Integer) data.get("wind_dir");
            createWindDial(speed, high, "wind.png");
            files.add("wind.png");
            createWindCompass(dir, "compass.png");
            files.add("compass.png");

        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O Error", e);
        }

        return files.toArray(new String[files.size()]);
    }

    public static Timespan createTimespan(final int startOffset, final int days) {
        return new Timespan(startOffset, days);
    }

    public String[] generateHistoryCharts(final int startOffset, final int days) {

        init();

        List<String> files = new ArrayList<String>();
        try {
            // Historical data.
            final Timespan period = new Timespan(startOffset, days);
            createHistoryChart(period, "temp_out", "Temperatur", "\u00B0Celcius", "temperature_hist.png");
            files.add("temperature_hist.png");
            createHistoryChart(period, "barometer", "Barometer", "Millibar", "barometer_hist.png");
            files.add("barometer_hist.png");
            createHistoryChart(period, "hum_out", "Luftfuktighet", "Procent", "humidity_hist.png");
            files.add("humidity_hist.png");
            createHistoryChart(period, "wind_avg", "Vindhastighet", "m/s", "wind_hist.png");
            files.add("wind_hist.png");
            createHistoryChart(period, "solar", "Solstr\u00e5lning", "W/m\u00B2", "solar_hist.png");
            files.add("solar_hist.png");
            createHistoryChart(period, "uv", "UV-index", "UV", "uv_hist.png");
            files.add("uv_hist.png");
            createRainHistoryChart(period, "rain_hist.png");
            files.add("rain_hist.png");

        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O Error", e);
        }

        return files.toArray(new String[files.size()]);
    }

    public String[] generateMonthlyCharts(final int year) {

        init();

        final List<String> files = new ArrayList<String>();
        final DateFormat fmt = new SimpleDateFormat("yyyy-MM");
        try {
            // Historical data.
            Calendar cal = Calendar.getInstance();
            long now = cal.getTimeInMillis();
            cal.set(Calendar.YEAR, year);
            cal.add(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            for (int i = 0; i < 12 && cal.getTimeInMillis() < now; i++) {
                Date start = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.MILLISECOND, -1);
                Date stop = cal.getTime();
                cal.add(Calendar.MILLISECOND, 1);
                Timespan period = new Timespan(start, stop);
                String month = fmt.format(start);
                createHistoryChart(period, "temp_out", "Temperatur", "\u00B0Celcius", "temperature_" + month + ".png");
                files.add("temperature_" + month + ".png");
                createHistoryChart(period, "barometer", "Barometer", "Millibar", "barometer_" + month + ".png");
                files.add("barometer_" + month + ".png");
                createHistoryChart(period, "hum_out", "Luftfuktighet", "Procent", "humidity_" + month + ".png");
                files.add("humidity_" + month + ".png");
                createHistoryChart(period, "wind_avg", "Vindhastighet", "m/s", "wind_" + month + ".png");
                files.add("wind_" + month + ".png");
                createHistoryChart(period, "solar", "Solstr\u00e5lning", "W/m\u00B2", "solar_" + month + ".png");
                files.add("solar_" + month + ".png");
                createHistoryChart(period, "uv", "UV-index", "UV", "uv_" + month + ".png");
                files.add("uv_" + month + ".png");
                createRainHistoryChart(period, "rain_" + month + ".png");
                files.add("rain_" + month + ".png");
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O Error", e);
        }

        return files.toArray(new String[files.size()]);
    }

    public String[] generateYearlyCharts(int year) {

        init();

        List<String> files = new ArrayList<String>();
        try {
            // Historical data.
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            int currentMonth = cal.get(Calendar.MONTH);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            Date start = cal.getTime();
            if (year < currentYear) {
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
            } else {
                cal.set(Calendar.MONTH, currentMonth);
            }
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.MILLISECOND, -1);
            Date stop = cal.getTime();
            Timespan period = new Timespan(start, stop);
            createHistoryChart(period, "temp_out", "Temperatur", "\u00B0Celcius", "temperature_" + year + ".png");
            files.add("temperature_" + year + ".png");
            createHistoryChart(period, "barometer", "Barometer", "Millibar", "barometer_" + year + ".png");
            files.add("barometer_" + year + ".png");
            createHistoryChart(period, "hum_out", "Luftfuktighet", "Procent", "humidity_" + year + ".png");
            files.add("humidity_" + year + ".png");
            createHistoryChart(period, "wind_avg", "Vindhastighet", "m/s", "wind_" + year + ".png");
            files.add("wind_" + year + ".png");
            createHistoryChart(period, "solar", "Solstr\u00e5lning", "W/m\u00B2", "solar_" + year + ".png");
            files.add("solar_" + year + ".png");
            createHistoryChart(period, "uv", "UV-index", "UV", "uv_" + year + ".png");
            files.add("uv_" + year + ".png");
            createRainHistoryChart(period, "rain_" + year + ".png");
            files.add("rain_" + year + ".png");
        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O Error", e);
        }
        return files.toArray(new String[files.size()]);
    }

    private Properties getProperties() {
        final Properties prop = new Properties();
        InputStream fis = null;
        try {
            File file = new File(System.getenv("VISUALIZER_CONFIG"), PROPERTIES_FILE);
            if (file.exists()) {
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

    public void init() {
        if (conn == null) {
            try {
                final Properties prop = getProperties();
                if (outputDir == null) {
                    setOutputDirectory(prop.getProperty("visualizer.outputDir"));
                }
                final String driverName = prop.getProperty("visualizer.jdbc.driver", "org.h2.Driver");
                Class.forName(driverName);
                conn = DriverManager.getConnection(prop.getProperty("visualizer.jdbc.url", "jdbc:h2:file:./weatherDb"));
                dialect = initSqlDialect(driverName);
            } catch (ClassNotFoundException e) {
                log.log(Level.SEVERE, "Cannot find JDBC driver", e);
                throw new RuntimeException(e);
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Cannot connect to database", e);
                throw new RuntimeException(e);
            }
        }
    }

    private SqlDialect initSqlDialect(String driverName) {
        if (driverName.contains("mysql")) {
            return new MySqlDialect();
        }
        return new H2Dialect();
    }

    public void cleanup() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                log.log(Level.WARNING, "Failed to close database connection", ex);
            }
        }
    }

    public Map<String, Object> getWeatherData() {
        Map<String, Object> map = new HashMap<String, Object>();

        // Put archived data into the map.
        setArchivedData(map);

        // Put current data into the map.
        setCurrentData(map);

        // Daily high/low values.
        //
        Date timestamp = (Date) map.get("timestamp");
        if (timestamp == null) {
            throw new RuntimeException("No data!");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -6);
        Date day7 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 6);
        Date day0 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date day1 = cal.getTime();
        // High values.
        setHiLowData(day0, day1, "temp_out", true, map, "dailyHighTemp");
        setHiLowData(day0, day1, "hum_out", true, map, "dailyHighHumidity");
        setHiLowData(day0, day1, "wind_high", true, map, "dailyHighWind");
        // Low values.
        setHiLowData(day0, day1, "temp_out", false, map, "dailyLowTemp");
        setHiLowData(day0, day1, "hum_out", false, map, "dailyLowHumidity");
        setHiLowData(day0, day1, "wind_avg", false, map, "dailyLowWind");

        map.put("rain_today", getValue(day0, day1, "rain", SUM));

        map.put("solar_hours", getSolarHours(new Timespan(day7, day1)));

        Date midsummer = getMidsummerEve();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        Date thursday = cal.getTime();
        if (thursday.getTime() > midsummer.getTime()) {
            map.put("solligan", getSolarHours(new Timespan(midsummer, thursday)));
            map.put("solligan_time", thursday);
        } else {
            map.put("solligan", Float.valueOf(0));
        }

        return map;
    }

    /**
     * I Sverige är midsommardagen, helgdagen, en rörlig helgdag och infaller på lördagen mellan 20 och 26 juni.
     * Midsommarafton infaller därför alltid dagen innan, på en fredag som inträffar mellan 19 och 25 juni.
     *
     * @return
     */
    private Date getMidsummerEve() {
        Calendar cal = Calendar.getInstance();
        while (cal.get(Calendar.MONTH) != Calendar.JUNE) {
            cal.add(Calendar.MONTH, -1);
        }
        cal.set(Calendar.DAY_OF_MONTH, 19);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    private void setArchivedData(Map<String, Object> map) {
        PreparedStatement stmt = null;
        ResultSet result = null;

        init();

        try {
            stmt = conn.prepareStatement("SELECT * FROM archive WHERE ts = (SELECT MAX(ts) FROM archive)");
            result = stmt.executeQuery();
            if (result.next()) {
                map.put("timestamp", result.getTimestamp(2));
                map.put("temp_out", result.getFloat(3));
                map.put("temp_in", result.getFloat(4));
                map.put("hum_out", result.getInt(5));
                map.put("hum_in", result.getInt(6));
                map.put("dew", calculateDewPoint(result.getFloat(3), result.getInt(5)));
                map.put("heat_index", calculateHeatIndex(result.getFloat(3), result.getInt(5)));
                map.put("barometer", result.getFloat(7));
                map.put("rain", result.getFloat(8));
                map.put("rain_rate", result.getFloat(9));
                map.put("wind_avg", result.getFloat(10));
                map.put("wind_dir", getDirection(result.getInt(11)));
                map.put("wind_dir_name", getDirectionName(result.getInt(11)));
                map.put("wind_high", result.getFloat(12));
                map.put("chill", calculateWindChill(result.getFloat(3), result.getFloat(10)));
                map.put("solar", result.getInt(13));
                map.put("uv", result.getFloat(14));
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close ResultSet", ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close select statement", ex);
                }
            }
        }
    }

    private void setCurrentData(Map<String, Object> map) {
        PreparedStatement stmt = null;
        ResultSet result = null;

        init();

        try {
            stmt = conn.prepareStatement("SELECT * FROM current");
            result = stmt.executeQuery();
            if (result.next()) {
                // id (primary key) is column index 1
                map.put("bar_trend", result.getInt(2));
                map.put("console_battery", result.getFloat(3));
                map.put("forecast_icons", result.getString(4));
                map.put("forecast_msg", result.getString(5));
                map.put("sunrise", result.getTimestamp(6));
                map.put("sunset", result.getTimestamp(7));
                //map.put("timestamp", result.getTimestamp(8));
                map.put("transmit_battery", result.getInt(9));
            } else {
                map.put("bar_trend", 0);
                map.put("console_battery", 4.5);
                map.put("forecast_icons", "none");
                map.put("forecast_msg", "NO CURRENT VALUES");
                map.put("sunrise", new Date());
                map.put("sunset", new Date());

            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close ResultSet", ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close select statement", ex);
                }
            }
        }
    }

    private float getSolarHours(final Timespan period) {
        PreparedStatement stmt = null;
        ResultSet result = null;
        float f = 0f;

        init();

        try {
            stmt = conn.prepareStatement("SELECT COUNT(*) / 6 AS hours FROM archive WHERE solar > 120 AND ts BETWEEN ? AND ?");
            stmt.setTimestamp(1, new java.sql.Timestamp(period.getStartTime().getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(period.getEndTime().getTime()));
            result = stmt.executeQuery();
            if (result.next()) {
                f = result.getFloat(1);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close ResultSet", ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close select statement", ex);
                }
            }
        }
        return f;
    }

    private boolean setHiLowData(Date from, Date to, String column, boolean max, Map<String, Object> map, String key) {
        HiLow hilo = getHighLow(from, to, column, max);
        boolean rval;
        if (hilo != null) {
            map.put(key + "Value", hilo.getValue());
            map.put(key + "Time", hilo.getTimestamp());
            rval = true;
        } else {
            rval = false;
        }
        return rval;
    }

    private HiLow getHighLow(Date from, Date to, String column, boolean max) {
        PreparedStatement stmt = null;
        ResultSet result = null;
        HiLow hilo = null;
        String func = max ? "DESC" : "ASC";

        init();

        try {
            stmt = conn.prepareStatement("SELECT ts, " + column + " FROM archive WHERE ts BETWEEN ? AND ? ORDER BY " + column + " " + func + ", ts ASC");
            stmt.setTimestamp(1, new java.sql.Timestamp(from.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(to.getTime()));
            result = stmt.executeQuery();
            if (result.next()) {
                hilo = new HiLow(result.getTimestamp(1), result.getFloat(2));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close ResultSet", ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close select statement", ex);
                }
            }
        }
        return hilo;
    }

    private float getValue(Date from, Date to, String column, int func) {
        PreparedStatement stmt = null;
        ResultSet result = null;
        String f;
        float value = 0f;

        switch (func) {
            case MIN:
                f = "MIN";
                break;
            case MAX:
                f = "MAX";
                break;
            case SUM:
                f = "SUM";
                break;
            case AVG:
                f = "AVG";
                break;
            default:
                throw new IllegalArgumentException("Illegal function: " + func);
        }

        init();

        try {
            stmt = conn.prepareStatement("SELECT " + f + "(" + column + ") FROM archive WHERE ts BETWEEN ? AND ?");
            stmt.setTimestamp(1, new java.sql.Timestamp(from.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(to.getTime()));
            result = stmt.executeQuery();
            if (result.next()) {
                value = result.getFloat(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close ResultSet", ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close select statement", ex);
                }
            }
        }
        return value;
    }

    public static final String[] WIND_DIRECTION_NAMES = {"N", "NNO", "NO", "ONO", "O",
            "OSO", "SO", "SSO", "S", "SSV", "SV", "VSV", "V", "VNV", "NV", "NNV"
    };
    public static final int[] WIND_DIRECTION_DEGREES = new int[16];

    static {
        float d = 0.0f;
        for (int i = 0; i < 16; i++) {
            WIND_DIRECTION_DEGREES[i] = Math.round(d);
            d += 22.5;
        }
    }

    private int getDirection(Integer dir) {
        if (dir == null || dir.intValue() == -1) {
            return -1;
        }
        return WIND_DIRECTION_DEGREES[dir.intValue()];
    }

    private String getDirectionName(Integer dir) {
        if (dir == null || dir.intValue() == -1) {
            return "?";
        }
        return WIND_DIRECTION_NAMES[dir.intValue()];
    }

    private float calculateDewPoint(float tempC, int humidity) {
        double es = 6.11 * Math.pow(10.0, (7.5 * tempC / (237.7 + tempC)));
        double e = (humidity * es) / 100;
        double dew = (-430.22 + 237.7 * Math.log(e)) / (-Math.log(e) + 19.08);
        return (float) dew;
    }

    /**
     * Calculate wind chill.
     * The "Chilled" air temperature can also be expressed as a function of
     * wind velocity and ambient air temperature.
     *
     * @param tempC     temperature in degrees Celcius
     * @param windSpeed wind speed in meters per second (m/s).
     * @return chilled air temperature
     */
    private float calculateWindChill(final double tempC, final double windSpeed) {
        double tempF = celcius2fahrenheit(tempC);
        double mph = windSpeed * 2.23694;
        if (tempF < 50.0 && mph > 3.0) {
            // Wind chill is only defined for temperatures below 50 F and
            // wind speed above 3 MPH.
            double chillF = 35.74 + (0.6215 * tempF) - (35.75 * Math.pow(mph, 0.16)) + (0.4275 * tempF * Math.pow(mph, 0.16));
            double chillC = (Math.round(fahrenheit2celcius(chillF) * 10)) / 10.0;
            return (float) chillC;
        }
        return (float) tempC;
    }

    /**
     * TODO This version does not work, the formula is incorrect
     * interpreted from http://www.gorhamschaffler.com/humidity_formulas.htm
     *
     * @param tempC temperature in Celcius
     * @param RH    relative humidity in percent (100% = 100)
     * @return heat index or apparent temperature
     */
    private float calculateHeatIndex(float tempC, int RH) {
        double Tf = celcius2fahrenheit(tempC);
        double RH2 = Math.pow(RH, 2);
        double Tf2 = Math.pow(Tf, 2);
        double hi = -42.379 + (2.04901523 * Tf) + (10.14333127 * RH) - ((0.22475541 * Tf) * RH) - 0.00683783 * Tf2 - 0.05481717 * RH2 + 0.00122874 * Tf2 * RH + 0.00085282 * Tf * RH2 - 0.00000199 * Tf2 * RH2;
        return (float) hi;
    }

    /**
     * Convert degrees Celcius to degrees Fahrenheit.
     *
     * @param celcius the temperature in Celcius.
     * @return the temperature in Fahrenheit.
     */
    private double celcius2fahrenheit(double celcius) {
        return celcius * 9 / 5 + 32;
    }

    /**
     * Convert degrees Fahrenheit to degrees Celcius.
     *
     * @param f the temperature in Fahrenheit.
     * @return the temperature in Celcius.
     */
    private double fahrenheit2celcius(double f) {
        return (f - 32) * 5 / 9.0;
    }

    private XYDataset createHistoryDataset(final Date begin, final Date end, final String column, final String label) throws SQLException {
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        PreparedStatement stmt = null;
        ResultSet result = null;
        long spanDays = (end.getTime() - begin.getTime()) / 1000 / 60 / 60 / 24;
        try {
            final java.sql.Timestamp sqlBegin = new java.sql.Timestamp(begin.getTime());
            final java.sql.Timestamp sqlEnd = new java.sql.Timestamp(end.getTime());
            if (spanDays < 100) {
                stmt = conn.prepareStatement("SELECT ts, " + column + " FROM archive WHERE " + column + " IS NOT NULL AND ts BETWEEN ? AND ? ORDER BY ts");
            } else if (spanDays < 1000) {
                stmt = conn.prepareStatement(dialect.selectGroupedByHour(column));
            } else {
                stmt = conn.prepareStatement(dialect.selectGroupedByDay(column));
            }
            stmt.setTimestamp(1, sqlBegin);
            stmt.setTimestamp(2, sqlEnd);
            result = stmt.executeQuery();

            final TimeSeries s1 = new TimeSeries(label);
            while (result.next()) {
                final java.sql.Timestamp ts = result.getTimestamp(1);
                final long timestamp = ts.getTime();
                s1.add(new FixedMillisecond(timestamp), result.getFloat(2));
            }
            dataset.addSeries(s1);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    log.log(Level.SEVERE, "Failed to close ResultSet", e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close select statement", ex);
                }
            }
        }

        return dataset;
    }

    private static final DateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat M = new SimpleDateFormat("MMM");
    private static final DateFormat DAY = new SimpleDateFormat("d");

    private CategoryDataset createRainDataset(final Date begin, final Date end) throws SQLException {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        PreparedStatement stmt = null;
        ResultSet result = null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        int y1 = cal.get(Calendar.YEAR);
        int m1 = cal.get(Calendar.MONTH);
        cal.setTime(end);
        int y2 = cal.get(Calendar.YEAR);
        int m2 = cal.get(Calendar.MONTH);
        long spanDays = (end.getTime() - begin.getTime()) / 1000 / 60 / 60 / 24;
        DateFormat fmt = spanDays < 40 ? DAY : M;
        try {
            final java.sql.Timestamp sqlBegin = new java.sql.Timestamp(begin.getTime());
            final java.sql.Timestamp sqlEnd = new java.sql.Timestamp(end.getTime());
            if (spanDays < 100) {
                stmt = conn.prepareStatement(dialect.selectRainGroupedByDay());
            } else {
                stmt = conn.prepareStatement(dialect.selectRainGroupedByMonth());
            }

            stmt.setTimestamp(1, sqlBegin);
            stmt.setTimestamp(2, sqlEnd);
            result = stmt.executeQuery();

            while (result.next()) {
                final String ymd = result.getString(1);
                final Date day = YMD.parse(ymd);
                final float rain = result.getFloat(2);
                dataset.addValue(rain, "Rain", fmt.format(day));
            }
        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse rain date", e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    log.log(Level.SEVERE, "Failed to close ResultSet", e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.log(Level.WARNING, "Failed to close select statement", ex);
                }
            }
        }

        return dataset;
    }

    private JFreeChart createLineChart(final String title, final String valueLabel, final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title, null, valueLabel,
                dataset, false, false, false);

        if (dataset.getItemCount(0) < 30) {
            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            final XYPlot plot = (XYPlot) chart.getPlot();
            plot.setRenderer(renderer);
        }
        return chart;
    }

    private JFreeChart createBarChart(final String title, final String valueLabel, final CategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createBarChart(
                title, null, valueLabel,
                dataset, PlotOrientation.VERTICAL, false, false, false);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis axis = plot.getDomainAxis();
        axis.setLabelFont(new Font("Arial", Font.PLAIN, 6));
        return chart;
    }

    public void createHistoryChart(final Timespan period, final String column, final String title, final String legend, final String filename) throws IOException {
        OutputStream out = new FileOutputStream(outputDir != null ? outputDir + "/" + filename : filename);
        try {
            createHistoryChart(period, column, title, legend, out, 920, 320);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void createHistoryChart(final Timespan period, final String column, final String title, final String legend, final OutputStream out, final int width, final int height) throws IOException {
        try {
            log.fine("Period from " + period.getStartTime().toString() + " to " + period.getEndTime().toString());
            XYDataset dataset = createHistoryDataset(period.getStartTime(), period.getEndTime(), column, title);
            JFreeChart chart = createLineChart(title + " " + period, legend, dataset);
            chart.setBackgroundPaint(VERY_LIGHT_GRAY);
            ChartUtils.writeChartAsPNG(out, chart, width, height);
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQL Error", e);
        }
    }

    public void createRainHistoryChart(final Timespan period, final String filename) throws IOException {
        OutputStream out = new FileOutputStream(outputDir != null ? outputDir + "/" + filename : filename);
        try {
            createRainHistoryChart(period, out, 920, 320);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    public void createRainHistoryChart(final Timespan period, final OutputStream out, final int width, final int height) throws IOException {
        try {
            log.fine("Period from " + period.getStartTime().toString() + " to " + period.getEndTime().toString());
            CategoryDataset dataset = createRainDataset(period.getStartTime(), period.getEndTime());
            JFreeChart chart = createBarChart("Nederb\u00f6rd " + period, "millimeter", dataset);
            chart.setBackgroundPaint(VERY_LIGHT_GRAY);
            ChartUtils.writeChartAsPNG(out, chart, width, height);
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQL Error", e);
        }
    }

    private static final Color VERY_LIGHT_GRAY = new Color(0xe3, 0xe6, 0xe0);

    private void createTemperatureDial(float temperature, int humidity, final String filename) throws IOException {
        ValueDataset dataset1 = new DefaultValueDataset(temperature);
        ValueDataset dataset2 = new DefaultValueDataset(humidity);

        // get data for diagrams
        DialPlot plot = new DialPlot();
        plot.setView(0.0, 0.0, 1.0, 1.0);
        plot.setDataset(0, dataset1);
        plot.setDataset(1, dataset2);
        StandardDialFrame dialFrame = new StandardDialFrame();
        dialFrame.setBackgroundPaint(Color.lightGray);
        dialFrame.setForegroundPaint(Color.darkGray);
        plot.setDialFrame(dialFrame);

        GradientPaint gp = new GradientPaint(new Point(), new Color(
                255, 255, 255), new Point(), new Color(170, 170, 220));
        DialBackground db = new DialBackground(gp);
        db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.VERTICAL));
        plot.setBackground(db);

        // Temperature
        DialTextAnnotation annotation1 = new DialTextAnnotation(
                "\u00B0C");
        annotation1.setFont(new Font("Dialog", Font.BOLD, 10));
        annotation1.setRadius(0.76);

        plot.addLayer(annotation1);

        // Humidity
        DialTextAnnotation annotation2 = new DialTextAnnotation(
                "%");
        annotation2.setFont(new Font("Dialog", Font.BOLD, 10));
        annotation2.setPaint(Color.blue);
        annotation2.setRadius(0.4);

        plot.addLayer(annotation2);

        // Temperature
        DialValueIndicator dvi = new DialValueIndicator(0);
        dvi.setFont(new Font("Dialog", Font.PLAIN, 9));
        dvi.setOutlinePaint(Color.darkGray);
        //dvi.setBackgroundPaint(new Color(0xee, 0xee, 0xf6));
        NumberFormat fmt = new DecimalFormat("#");
        fmt.setMaximumFractionDigits(1);
        fmt.setMinimumIntegerDigits(1);
        dvi.setNumberFormat(fmt);
        dvi.setRadius(0.71);
        dvi.setAngle(-88.0); // -103
        dvi.setInsets(new RectangleInsets(0.0, 8.0, 0.0, 2.0)); // top, left, bottom, right
        plot.addLayer(dvi);

        StandardDialScale scale = new StandardDialScale(-30, 30, -120, -300, 5, 4);
        scale.setTickRadius(0.88);
        scale.setTickLabelOffset(0.15);
        scale.setTickLabelFont(new Font("Dialog", Font.PLAIN, 10));
        NumberFormat fmt3 = new DecimalFormat("#");
        fmt3.setMaximumFractionDigits(0);
        scale.setTickLabelFormatter(fmt3);
        plot.addScale(0, scale);

        // Humidity
        DialValueIndicator dvi2 = new DialValueIndicator(1);
        dvi2.setFont(new Font("Dialog", Font.PLAIN, 9));
        dvi2.setOutlinePaint(Color.blue);
        //dvi2.setBackgroundPaint(new Color(0xee, 0xee, 0xf6));
        NumberFormat fmt2 = new DecimalFormat("#");
        fmt2.setMaximumFractionDigits(0);
        dvi2.setNumberFormat(fmt2);

        dvi2.setRadius(0.59);
        dvi2.setAngle(-90.0); // -77
        dvi2.setInsets(new RectangleInsets(0.0, 1.0, 0.0, 1.0));
        plot.addLayer(dvi2);

        StandardDialScale scale2 = new StandardDialScale(0, 100, -120, -300, 10, 4);
        scale2.setTickRadius(0.50);
        scale2.setTickLabelOffset(0.15);
        scale2.setTickLabelFont(new Font("Dialog", Font.PLAIN, 9));
        scale2.setTickLabelFormatter(fmt3);
        scale2.setMajorTickPaint(Color.blue);
        plot.addScale(1, scale2);
        plot.mapDatasetToScale(1, 1);

        // Add needles.
        // To make the temperature needle the front-most needle,
        // it must be added after humidity needle.
        // Humidity needle.
        DialPointer needle2 = new DialPointer.Pin(1);
        needle2.setRadius(0.50);
        plot.addLayer(needle2);

        // Temperature needle.
        DialPointer needle = new DialPointer.Pointer(0);
        Color darkGreen = new Color(0x15, 0x49, 0x1f);
        ((DialPointer.Pointer) needle).setFillPaint(darkGreen);
        plot.addLayer(needle);

        // Add a cap at the dial center.
        DialCap cap = new DialCap();
        cap.setRadius(0.10);
        plot.setCap(cap);

        JFreeChart chart = new JFreeChart(plot);
        //TextTitle title = new TextTitle("Temperatur/luftfuktighet", new Font("Dialog", Font.BOLD, 12));
        //title.setPaint(Color.DARK_GRAY);
        //chart.setTitle(title);
        chart.setBackgroundPaint(VERY_LIGHT_GRAY);

        OutputStream out = null;
        try {
            out = new FileOutputStream(outputDir != null ? outputDir + "/" + filename : filename);
            ChartUtils.writeChartAsPNG(out, chart, DIAL_WIDTH, DIAL_HEIGHT);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void createWindDial(float speed, float high, final String filename) throws IOException {
        ValueDataset dataset1 = new DefaultValueDataset(speed);
        ValueDataset dataset2 = new DefaultValueDataset(high);

        // get data for diagrams
        DialPlot plot = new DialPlot();
        plot.setView(0.0, 0.0, 1.0, 1.0);
        plot.setDataset(0, dataset1);
        plot.setDataset(1, dataset2);
        StandardDialFrame dialFrame = new StandardDialFrame();
        dialFrame.setBackgroundPaint(Color.lightGray);
        dialFrame.setForegroundPaint(Color.darkGray);
        plot.setDialFrame(dialFrame);

        GradientPaint gp = new GradientPaint(new Point(), new Color(
                255, 255, 255), new Point(), new Color(170, 170, 220));
        DialBackground db = new DialBackground(gp);
        db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.VERTICAL));
        plot.setBackground(db);

        // Wind Speed
        DialTextAnnotation annotation1 = new DialTextAnnotation(
                "m/s");
        annotation1.setFont(new Font("Dialog", Font.BOLD, 10));
        annotation1.setRadius(0.76);

        plot.addLayer(annotation1);

        DialValueIndicator dvi = new DialValueIndicator(0);
        dvi.setFont(new Font("Dialog", Font.PLAIN, 9));
        dvi.setOutlinePaint(Color.darkGray);
        //dvi.setBackgroundPaint(new Color(0xee, 0xee, 0xf6));
        NumberFormat fmt = new DecimalFormat("#");
        fmt.setMaximumFractionDigits(1);
        fmt.setMinimumIntegerDigits(1);
        dvi.setNumberFormat(fmt);
        dvi.setRadius(0.71);
        dvi.setAngle(-89.0); // -103
        dvi.setInsets(new RectangleInsets(0.0, 2.0, 0.0, 2.0)); // top, left, bottom, right
        plot.addLayer(dvi);

        StandardDialScale scale = new StandardDialScale(0, 20, -120, -300, 1, 0);
        scale.setTickRadius(0.88);
        scale.setTickLabelOffset(0.15);
        scale.setTickLabelFont(new Font("Dialog", Font.PLAIN, 10));
        NumberFormat fmt3 = new DecimalFormat("#");
        fmt3.setMaximumFractionDigits(0);
        scale.setTickLabelFormatter(fmt3);
        plot.addScale(0, scale);

        // Add needles.
        // To make the average speed needle the front-most needle,
        // it must be added after high speed needle.
        // High speed needle.
        DialPointer needle2 = new DialPointer.Pin(1);
        needle2.setRadius(0.62);
        plot.addLayer(needle2);

        // Average speed needle.
        DialPointer needle = new DialPointer.Pointer(0);
        Color darkGreen = new Color(0x15, 0x49, 0x1f);
        ((DialPointer.Pointer) needle).setFillPaint(darkGreen);
        plot.addLayer(needle);

        // Add a cap at the dial center.
        DialCap cap = new DialCap();
        cap.setRadius(0.10);
        plot.setCap(cap);

        JFreeChart chart = new JFreeChart(plot);
        //TextTitle title = new TextTitle("Vindhastighet", new Font("Dialog", Font.BOLD, 12));
        //title.setPaint(Color.DARK_GRAY);
        //chart.setTitle(title);
        chart.setBackgroundPaint(VERY_LIGHT_GRAY);

        OutputStream out = null;
        try {
            out = new FileOutputStream(outputDir != null ? outputDir + "/" + filename : filename);
            ChartUtils.writeChartAsPNG(out, chart, DIAL_WIDTH, DIAL_HEIGHT);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void createWindCompass(int direction, final String filename) throws IOException {
        final ValueDataset dataset = new DefaultValueDataset(direction);
        final CompassPlot plot2 = new CompassPlot(dataset);
        plot2.setSeriesNeedle(7);
        plot2.setSeriesPaint(0, Color.red);
        plot2.setSeriesOutlinePaint(0, Color.red);

        final JFreeChart chart = new JFreeChart(plot2);
        //final TextTitle title = new TextTitle("Vindriktning", new Font("Dialog", Font.BOLD, 12));
        //title.setPaint(Color.DARK_GRAY);
        //chart.setTitle(title);
        chart.setBackgroundPaint(VERY_LIGHT_GRAY);

        OutputStream out = null;
        try {
            out = new FileOutputStream(outputDir != null ? outputDir + "/" + filename : filename);
            ChartUtils.writeChartAsPNG(out, chart, DIAL_WIDTH, DIAL_HEIGHT);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static class Timespan {

        private static final Locale SWEDISH = new Locale("sv", "SE");
        private static final DateFormat MY_FORMAT = new SimpleDateFormat("MMMM yyyy", SWEDISH);
        private final Date from;
        private final Date to;

        public Timespan(int startOffset, int days) {
            if (days < 1) {
                throw new IllegalArgumentException("days must be > 0");
            }
            if (startOffset > 0) {
                startOffset = -startOffset;
            }
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.add(Calendar.DAY_OF_MONTH, startOffset);
            from = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, days);
            cal.add(Calendar.MILLISECOND, -1);
            to = cal.getTime();
        }

        public Timespan(final Date from, final Date to) {
            this.from = from;
            this.to = to;
        }

        public Date getStartTime() {
            return this.from;
        }

        public Date getEndTime() {
            return this.to;
        }

        @Override
        public String toString() {
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            int fromYear = cal.get(Calendar.YEAR);
            int fromMonth = cal.get(Calendar.MONTH);
            cal.setTime(to);
            int toYear = cal.get(Calendar.YEAR);
            int toMonth = cal.get(Calendar.MONTH);
            if (toYear == fromYear && toMonth == fromMonth) {
                return MY_FORMAT.format(from);
            } else {
                return MY_FORMAT.format(from) + " - " + MY_FORMAT.format(to);
            }

        }
    }

    private static class HiLow {

        private Date timestamp;
        private double value;

        public HiLow() {
        }

        public HiLow(Date timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
