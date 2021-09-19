package se.technipelago.weather.chart;

public class H2Dialect implements SqlDialect {
    @Override
    public String selectGroupedByHour(String column) {
        return "SELECT formatdatetime(ts, 'yyyy-MM-dd HH:00:00') AS day, AVG(" + column + ") AS value FROM archive WHERE " + column + " IS NOT NULL AND ts BETWEEN ? AND ? GROUP BY day";
    }

    @Override
    public String selectGroupedByDay(String column) {
        return "SELECT formatdatetime(ts, 'yyyy-MM-dd') AS day, AVG(" + column + ") AS value FROM archive WHERE " + column + " IS NOT NULL AND ts BETWEEN ? AND ? GROUP BY day";
    }

    @Override
    public String selectRainGroupedByDay() {
        return "SELECT formatdatetime(ts, 'yyyy-MM-dd') AS day, SUM(rain) AS rain FROM archive WHERE ts BETWEEN ? AND ? GROUP BY day";
    }

    @Override
    public String selectRainGroupedByMonth() {
        return "SELECT formatdatetime(ts, 'yyyy-MM-15') AS day, SUM(rain) AS rain FROM archive WHERE ts BETWEEN ? AND ? GROUP BY day";
    }
}
