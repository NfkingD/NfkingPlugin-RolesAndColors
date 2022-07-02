package com.nfkingd.nfkingplugin.roles_and_colors.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.nfkingd.nfkingplugin.roles_and_colors.dto.PlayerRoleDto;
import com.nfkingd.nfkingplugin.roles_and_colors.dto.RoleDto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RolesJsonUtil {

    public static void savePlayerRoleToJson(PlayerRoleDto newRole) {
        var roles = getPlayerRolesFromJson();

        if (roles != null) {
            var hasPlayerRole = roles.stream()
                    .anyMatch(r -> r.getPlayerName().equals(newRole.getPlayerName()));

            if (!hasPlayerRole) {
                roles.add(newRole);
            } else {
                roles.stream()
                        .filter(r -> r.getPlayerName().equals(newRole.getPlayerName()))
                        .forEach(r -> {
                            r.setFormattedName(newRole.getFormattedName());
                            r.setColor(newRole.getColor());
                        });
            }
        } else {
            roles = new ArrayList<>();
            roles.add(newRole);
        }

        try {
            var gson = new Gson();
            File file = new File("player-roles.json");
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            gson.toJson(roles, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PlayerRoleDto> getPlayerRolesFromJson() {
        List<PlayerRoleDto> roles = new ArrayList<>();

        var gson = new Gson();
        try {
            File file = new File("player-roles.json");

            if (file.exists()) {
                JsonReader reader = new JsonReader(new FileReader(file.getAbsolutePath()));
                roles = gson.fromJson(reader, new TypeToken<List<PlayerRoleDto>>() {
                }.getType());
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return roles;
    }

    public static Optional<PlayerRoleDto> getPlayerFromRoles(String playerName) {
        List<PlayerRoleDto> roles = getPlayerRolesFromJson();

        if (roles == null) {
            return Optional.empty();
        }

        return roles.stream()
                .filter(r -> r.getPlayerName().equals(playerName))
                .findFirst();
    }

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

        try {
            var gson = new Gson();
            File file = new File("roles.json");
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            gson.toJson(roles, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static List<RoleDto> getRolesFromJson() {
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
}
