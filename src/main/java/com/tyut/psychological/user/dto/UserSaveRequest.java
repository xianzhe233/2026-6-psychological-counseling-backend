package com.tyut.psychological.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class UserSaveRequest {
    private String username;
    @NotBlank(message = "姓名不能为空")
    private String realName;
    private String phone;
    private String email;
    private String password;
    @NotEmpty(message = "角色不能为空")
    private List<String> roleCodes;
    private Integer status = 1;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
