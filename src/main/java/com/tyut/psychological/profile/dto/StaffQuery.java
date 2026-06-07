package com.tyut.psychological.profile.dto;

import com.tyut.psychological.common.api.PageQuery;

public class StaffQuery extends PageQuery {
    private String keyword;
    private String staffType;
    private Integer status;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getStaffType() { return staffType; }
    public void setStaffType(String staffType) { this.staffType = staffType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
