package com.nfkingd.nfkingplugin.roles_and_colors.commands;

import com.nfkingd.nfkingplugin.roles_and_colors.dto.PlayerRole;
import com.nfkingd.nfkingplugin.roles_and_colors.dto.RoleDto;
import com.nfkingd.nfkingplugin.roles_and_colors.utils.RolesJsonUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class RoleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] arguments) {
        if (!commandSender.hasPermission("roles.admin")) {
            sendErrorMessage(commandSender, "You don't have the permission");
            return true;
        }

        if (arguments.length == 0) {
            sendErrorMessage(commandSender, "This command requires arguments");
            return true;
        }

        var firstArgument = arguments[0];

        if (firstArgument.equals("create")) {
            return processCreateCommand(commandSender, arguments);
        }

        if (firstArgument.equals("edit")) {
            return processEditCommand(commandSender, arguments);
        }

        if (firstArgument.equals("delete")) {
            return processDeleteCommand(commandSender, arguments);
        }

        if (firstArgument.equals("add")) {
            return processAddCommand(commandSender, arguments);
        }

        if (firstArgument.equals("rm")) {
            return processRmCommand(commandSender, arguments);
        }

        var message = "/role " + arguments[0] + " does not exist";
        sendErrorMessage(commandSender, message);

        return true;
    }

    private boolean processCreateCommand(CommandSender commandSender, String[] arguments) {
        var argumentsCount = arguments.length;

        if (sendErrorMessageForUnhandledArgumentCount(2, 4, argumentsCount, commandSender)) {
            return true;
        }

        var color = getColorFromArguments(arguments, argumentsCount);
        var role = arguments[1];
        var roleCreated = createRole(role, color);

        if (!roleCreated) {
            var message = "Role already exists!";
            sendErrorMessage(commandSender, message);
        }

        return true;
    }

    private boolean processEditCommand(CommandSender commandSender, String[] arguments) {
        var argumentsCount = arguments.length;

        if (sendErrorMessageForUnhandledArgumentCount(2, 4, argumentsCount, commandSender)) {
            return true;
        }

        var color = getColorFromArguments(arguments, argumentsCount);
        var role = arguments[1];
        var roleEdited = editRole(role, color);

        if (!roleEdited) {
            var message = "Role does not exist!";
            sendErrorMessage(commandSender, message);
        }

        return true;
    }

    private boolean editRole(String role, String color) {
        var roleDto = new RoleDto(role, color);
        var optionalPlayerName = RolesJsonUtil.getPlayerFromRole(role);
        var isRoleEdited = RolesJsonUtil.editRole(roleDto);

        if (!isRoleEdited) {
            return false;
        }

        if (optionalPlayerName.isPresent()) {
            var playerName = optionalPlayerName.get();

            RolesJsonUtil.removeRoleFromPlayer(playerName);

            var optionalPlayer = getPlayer(playerName);

            if (optionalPlayer.isPresent()) {
                var player = optionalPlayer.get();
                var name = player.getName();

                player.setPlayerListName(name);
                player.setDisplayName(name);
            }
        }

        return true;
    }

    private boolean processDeleteCommand(CommandSender commandSender, String[] arguments) {
        var argumentsCount = arguments.length;

        if (sendErrorMessageForUnhandledArgumentCount(2, argumentsCount, commandSender)) {
            return true;
        }

        var role = arguments[1];
        var roleDeleted = deleteRole(role);

        if (!roleDeleted) {
            var message = "Role could not be deleted!";
            sendErrorMessage(commandSender, message);
        }

        return true;
    }

    private boolean deleteRole(String role) {
        var optionalPlayerName = RolesJsonUtil.getPlayerFromRole(role);
        var isRoleDeleted = RolesJsonUtil.deleteRole(role);

        if (!isRoleDeleted) {
            return false;
        }

        if (optionalPlayerName.isPresent()) {
            var playerName = optionalPlayerName.get();

            RolesJsonUtil.removeRoleFromPlayer(playerName);

            var optionalPlayer = getPlayer(playerName);

            if (optionalPlayer.isPresent()) {
                var player = optionalPlayer.get();
                var name = player.getName();

                player.setPlayerListName(name);
                player.setDisplayName(name);
            }
        }

        return true;
    }

    private boolean processAddCommand(CommandSender commandSender, String[] arguments) {
        var argumentsCount = arguments.length;

        if (sendErrorMessageForUnhandledArgumentCount(3, argumentsCount, commandSender)) {
            return true;
        }

        var playerName = arguments[1];
        var role = arguments[2];
        var roleAddedToPlayer = addPlayerToRole(playerName, role);

        if (!roleAddedToPlayer) {
            var message = "Role does not exist!";
            sendErrorMessage(commandSender, message);
        }

        return true;
    }

    private boolean processRmCommand(CommandSender commandSender, String[] arguments) {
        var argumentsCount = arguments.length;

        if (sendErrorMessageForUnhandledArgumentCount(2, argumentsCount, commandSender)) {
            return true;
        }

        var playerName = arguments[1];
        var roleRemovedFromPlayer = removeRoleFromPlayer(playerName);

        if (!roleRemovedFromPlayer) {
            var message = "Player does not have a role!";
            sendErrorMessage(commandSender, message);
        }

        return true;
    }

    private boolean removeRoleFromPlayer(String playerName) {
        var roleRemovedFromPlayer = RolesJsonUtil.removeRoleFromPlayer(playerName);

        if (!roleRemovedFromPlayer) {
            return false;
        }

        var optionalPlayer = getPlayer(playerName);

        if (optionalPlayer.isPresent()) {
            var player = optionalPlayer.get();

            player.setPlayerListName(playerName);
            player.setDisplayName(playerName);
        }

        return true;
    }

    private boolean addPlayerToRole(String playerName, String role) {
        var playerRole = new PlayerRole(playerName, role);

        var roleAddedToPlayer = RolesJsonUtil.savePlayerRoleToJson(playerRole);
        if (!roleAddedToPlayer) {
            return false;
        }

        var optionalRoleDto = RolesJsonUtil.getRoleFromJson(role);
        if (optionalRoleDto.isEmpty()) {
            return false;
        }

        var roleDto = optionalRoleDto.get();
        var newName = roleDto.getColor() + role + " - " + playerName;

        var optionalPlayer = getPlayer(playerName);

        if (optionalPlayer.isPresent()) {
            var player = optionalPlayer.get();

            player.setPlayerListName(newName);
            player.setDisplayName(newName);
        }

        return true;
    }

    private boolean sendErrorMessageForUnhandledArgumentCount(int requiredArgumentsCount, int argumentsCount
            , CommandSender commandSender) {
        return sendErrorMessageForUnhandledArgumentCount(requiredArgumentsCount, requiredArgumentsCount
                , argumentsCount, commandSender);
    }

    private boolean sendErrorMessageForUnhandledArgumentCount(int minimalArgumentCount, int maximalArgumentCount
            , int argumentsCount, CommandSender commandSender) {
        if (argumentsCount < minimalArgumentCount) {
            return sendErrorMessageForMissingArguments(minimalArgumentCount, argumentsCount, commandSender);
        } else if (argumentsCount > maximalArgumentCount) {
            return sendErrorMessageForExcessArguments(maximalArgumentCount, argumentsCount, commandSender);
        }

        return false;
    }

    private boolean sendErrorMessageForMissingArguments(int minimalArgumentCount, int argumentsCount, CommandSender commandSender) {
        var missingArgumentCount = minimalArgumentCount - argumentsCount;
        var message = "You must add at least " + missingArgumentCount + " more argument(s)";
        sendErrorMessage(commandSender, message);

        return true;
    }

    private boolean sendErrorMessageForExcessArguments(int maximalArgumentCount, int argumentsCount, CommandSender commandSender) {
        var excessArgumentCount = argumentsCount - maximalArgumentCount;
        var message = "You must remove at least " + excessArgumentCount + " argument(s)";
        sendErrorMessage(commandSender, message);

        return true;
    }

    private void sendErrorMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(ChatColor.DARK_RED + message);
    }

    private String getColorFromArguments(String[] arguments, int argumentsCount) {
        if (argumentsCount == 4) {
            return ChatColor.of(arguments[2]) + "" + org.bukkit.ChatColor.valueOf(arguments[3]);
        } else if (argumentsCount == 3) {
            return handleHexaInputAndGetColorString(arguments[2]);
        }

        return ChatColor.WHITE + "";
    }

    private boolean createRole(String role, String color) {
        var roleDto = new RoleDto(role, color);

        return RolesJsonUtil.saveRoleToJson(roleDto);
    }

    private String handleHexaInputAndGetColorString(String argument) {
        if (argument.charAt(0) == '#') {
            return ChatColor.of(argument) + "";
        }

        return org.bukkit.ChatColor.valueOf(argument) + "";
    }

    private Optional<? extends Player> getPlayer(String playerName) {
        return Bukkit.getServer().getOnlinePlayers().stream()
                .filter(p -> p.getName().equals(playerName))
                .findFirst();
    }
}
