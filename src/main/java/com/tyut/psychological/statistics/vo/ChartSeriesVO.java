package com.tyut.psychological.statistics.vo;

import java.util.ArrayList;
import java.util.List;

public class ChartSeriesVO {
    private String name;
    private List<Long> data = new ArrayList<>();

    public ChartSeriesVO() {
    }

    public ChartSeriesVO(String name, List<Long> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Long> getData() { return data; }
    public void setData(List<Long> data) { this.data = data; }
}
