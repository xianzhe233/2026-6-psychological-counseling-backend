package com.tyut.psychological.common.vo;

public class OptionVO {
    private String label;
    private Long value;

    public OptionVO() {}

    public OptionVO(String label, Long value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Long getValue() { return value; }
    public void setValue(Long value) { this.value = value; }
}
