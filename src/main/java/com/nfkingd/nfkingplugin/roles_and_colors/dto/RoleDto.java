package com.nfkingd.nfkingplugin.roles_and_colors.dto;

public class RoleDto {

    private String role;
    private String color;

    public RoleDto() {

    }

    public RoleDto(String role, String color) {
        this.role = role;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
