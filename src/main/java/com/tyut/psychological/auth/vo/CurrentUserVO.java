package com.tyut.psychological.auth.vo;

import com.tyut.psychological.common.enums.RoleCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CurrentUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private List<RoleCode> roles;
    private RoleCode primaryRole;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<RoleCode> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleCode> roles) {
        this.roles = roles;
    }

    public RoleCode getPrimaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(RoleCode primaryRole) {
        this.primaryRole = primaryRole;
    }
}
