package com.nfkingd.nfkingplugin.roles_and_colors.dto;

public class PlayerRole {

    private String playerName;
    private String role;

    public PlayerRole() {

    }

    public PlayerRole(String playerName, String role) {
        this.playerName = playerName;
        this.role = role;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
