package se.technipelago.weather.chart;

public interface SqlDialect {
    String selectGroupedByHour(String column);

    String selectGroupedByDay(String column);

    String selectRainGroupedByDay();

    String selectRainGroupedByMonth();
}
