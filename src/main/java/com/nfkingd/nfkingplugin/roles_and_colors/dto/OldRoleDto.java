package com.nfkingd.nfkingplugin.roles_and_colors.dto;

public class OldRoleDto {

    private String playerName;
    private String formattedName;
    private String color;

    public OldRoleDto() {

    }

    public OldRoleDto(String playerName, String formattedName, String color) {
        this.playerName = playerName;
        this.formattedName = formattedName;
        this.color = color;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public String getColor() {
        return color;
    }

    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
