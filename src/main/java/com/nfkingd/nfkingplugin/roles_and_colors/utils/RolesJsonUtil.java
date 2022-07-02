package com.nfkingd.nfkingplugin.roles_and_colors.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.nfkingd.nfkingplugin.roles_and_colors.dto.PlayerRole;
import com.nfkingd.nfkingplugin.roles_and_colors.dto.RoleDto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RolesJsonUtil {

    public static boolean saveRoleToJson(RoleDto roleDto) {
        var roles = getRolesFromJson();

        if (roles != null) {
            var doesRoleExist = roles.stream()
                    .anyMatch(r -> r.getRole().equals(roleDto.getRole()));

            if (!doesRoleExist) {
                roles.add(roleDto);
            } else {
                return false;
            }
        } else {
            roles = new ArrayList<>();
            roles.add(roleDto);
        }

        saveJson(roles, "roles.json");

        return true;
    }

    private static List<RoleDto> getRolesFromJson() {
        List<RoleDto> roles = new ArrayList<>();

        var gson = new Gson();
        try {
            File file = new File("roles.json");

            if (file.exists()) {
                JsonReader reader = new JsonReader(new FileReader(file.getAbsolutePath()));
                roles = gson.fromJson(reader, new TypeToken<List<RoleDto>>() {
                }.getType());
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return roles;
    }

    public static Optional<RoleDto> getRoleFromJson(String role) {
        var roles = getRolesFromJson();

        if (roles == null) {
            return Optional.empty();
        }

        return roles.stream()
                .filter(r -> r.getRole().equals(role))
                .findFirst();
    }

    public static boolean savePlayerRoleToJson(PlayerRole playerRole) {
        var playerRoles = getPlayerRolesFromJson();

        if (playerRoles != null) {
            var doesRoleExist = getRolesFromJson().stream()
                    .anyMatch(role -> role.getRole().equals(playerRole.getRole()));

            if (!doesRoleExist) {
                return false;
            }

            var doesPlayerHaveRole = playerRoles.stream()
                    .anyMatch(r -> r.getPlayerName().equals(playerRole.getPlayerName()));

            if (!doesPlayerHaveRole) {
                playerRoles.add(playerRole);
            } else {
                playerRoles.stream()
                        .filter(r -> r.getPlayerName().equals(playerRole.getPlayerName()))
                        .forEach(r -> {
                            r.setRole(playerRole.getRole());
                        });
            }
        } else {
            playerRoles = new ArrayList<>();
            playerRoles.add(playerRole);
        }

        saveJson(playerRoles, "player-roles.json");

        return true;
    }

    private static List<PlayerRole> getPlayerRolesFromJson() {
        List<PlayerRole> playerRoles = new ArrayList<>();

        var gson = new Gson();
        try {
            File file = new File("player-roles.json");

            if (file.exists()) {
                JsonReader reader = new JsonReader(new FileReader(file.getAbsolutePath()));
                playerRoles = gson.fromJson(reader, new TypeToken<List<PlayerRole>>() {
                }.getType());
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return playerRoles;
    }

    public static Optional<RoleDto> getRoleForPlayer(String playerName) {
        var playerRoles = getPlayerRolesFromJson();

        var optionalPlayerRole = playerRoles.stream()
                .filter(playerRole -> playerRole.getPlayerName().equals(playerName))
                .findFirst();

        if (optionalPlayerRole.isEmpty()) {
            return Optional.empty();
        }

        var playerRole = optionalPlayerRole.get();
        var roles = getRolesFromJson();

        return roles.stream()
                .filter(role -> role.getRole().equals(playerRole.getRole()))
                .findFirst();
    }

    public static boolean removeRoleFromPlayer(String playerName) {
        var playerRoles = getPlayerRolesFromJson();

        var optionalPlayerRole = playerRoles.stream()
                .filter(playerRole -> playerRole.getPlayerName().equals(playerName))
                .findFirst();

        if (optionalPlayerRole.isEmpty()) {
            return false;
        }

        var newPlayerList = playerRoles.stream()
                .filter(playerRole -> !playerRole.getPlayerName().equals(playerName))
                .toList();

        saveJson(newPlayerList, "player-roles.json");

        return true;
    }

    private static void saveJson(List<?> list, String pathName) {
        try {
            var gson = new Gson();
            File file = new File(pathName);
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            gson.toJson(list, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
