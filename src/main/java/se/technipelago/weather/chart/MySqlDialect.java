package se.technipelago.weather.chart;

public class MySqlDialect implements SqlDialect {
    @Override
    public String selectGroupedByHour(String column) {
        return "SELECT date_format(ts, '%Y-%m-%d %H:00:00') AS day, AVG(" + column + ") AS value FROM archive WHERE " + column + " IS NOT NULL AND ts BETWEEN ? AND ? GROUP BY 1";
    }

    @Override
    public String selectGroupedByDay(String column) {
        return "SELECT date_format(ts, '%Y-%m-%d') AS day, AVG(" + column + ") AS value FROM archive WHERE " + column + " IS NOT NULL AND ts BETWEEN ? AND ? GROUP BY 1";
    }

    @Override
    public String selectRainGroupedByDay() {
        return "SELECT date_format(ts, '%Y-%m-%d') AS day, SUM(rain) AS rain FROM archive WHERE ts BETWEEN ? AND ? GROUP BY 1";
    }

    @Override
    public String selectRainGroupedByMonth() {
        return "SELECT date_format(ts, '%Y-%m-15') AS day, SUM(rain) AS rain FROM archive WHERE ts BETWEEN ? AND ? GROUP BY 1";
    }
}
