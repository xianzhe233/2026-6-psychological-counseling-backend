package com.tyut.psychological.statistics.vo;

import java.util.ArrayList;
import java.util.List;

public class ChartVO {
    private List<String> xAxis = new ArrayList<>();
    private List<ChartSeriesVO> series = new ArrayList<>();

    public List<String> getXAxis() { return xAxis; }
    public void setXAxis(List<String> xAxis) { this.xAxis = xAxis; }
    public List<ChartSeriesVO> getSeries() { return series; }
    public void setSeries(List<ChartSeriesVO> series) { this.series = series; }
}
