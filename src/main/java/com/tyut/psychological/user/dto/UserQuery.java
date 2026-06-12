package com.tyut.psychological.user.dto;

import com.tyut.psychological.common.api.PageQuery;

public class UserQuery extends PageQuery {
    private String keyword;
    private String roleCode;
    private Integer status;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
