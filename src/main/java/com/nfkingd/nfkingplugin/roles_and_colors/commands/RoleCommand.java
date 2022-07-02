package com.nfkingd.nfkingplugin.roles_and_colors.commands;

import com.nfkingd.nfkingplugin.roles_and_colors.dto.PlayerRole;
import com.nfkingd.nfkingplugin.roles_and_colors.dto.RoleDto;
import com.nfkingd.nfkingplugin.roles_and_colors.utils.RolesJsonUtil;
import net.md_5.bungee.api.ChatColor;
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

        if (firstArgument.equals("add")) {
            return processAddCommand(commandSender, arguments);
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

        var color = getColorFromArguments2(arguments, argumentsCount);
        var role = arguments[1];

        var roleAdded = addRole(role, color);

        if (!roleAdded) {
            var message = "Role already exists!";
            sendErrorMessage(commandSender, message);
        }

        return true;
    }

    private boolean processAddCommand(CommandSender commandSender, String[] arguments) {
        var argumentsCount = arguments.length;

        if (sendErrorMessageForUnhandledArgumentCount(3, argumentsCount, commandSender)) {
            return true;
        }

        var optionalPlayer = getPlayer(commandSender, arguments);

        if (optionalPlayer.isEmpty()) {
            sendErrorMessage(commandSender, "Player was not found");
            return true;
        }

        var player = optionalPlayer.get();
        var role = arguments[2];

        var roleAddedToPlayer = addPlayerToRole(player, role);

        if (!roleAddedToPlayer) {
            var message = "Role does not exist!";
            sendErrorMessage(commandSender, message);
        }

        return true;
    }

    private boolean addPlayerToRole(Player player, String role) {
        var oldName = player.getName();
        var playerRole = new PlayerRole(oldName, role);

        var roleAddedToPlayer = RolesJsonUtil.savePlayerRoleToJson(playerRole);
        if (!roleAddedToPlayer) {
            return false;
        }
        var optionalRoleDto = RolesJsonUtil.getRoleFromJson(role);
        if (optionalRoleDto.isEmpty()) {
            return false;
        }
        var roleDto = optionalRoleDto.get();
        var newName = roleDto.getColor() + role + " - " + oldName;

        player.setPlayerListName(newName);
        player.setDisplayName(newName);

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

    private String getColorFromArguments2(String[] arguments, int argumentsCount) {
        if (argumentsCount == 4) {
            return ChatColor.of(arguments[2]) + "" + org.bukkit.ChatColor.valueOf(arguments[3]);
        } else if (argumentsCount == 3) {
            return handleHexaInputAndGetColorString(arguments[2]);
        }

        return ChatColor.WHITE + "";
    }

    private boolean addRole(String role, String color) {
        var roleDto = new RoleDto(role, color);

        return RolesJsonUtil.saveRoleToJson(roleDto);
    }

    private Optional<? extends Player> getPlayer(CommandSender commandSender, String[] arguments) {
        return commandSender.getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> p.getName().equals(arguments[1]))
                .findFirst();
    }

    private String handleHexaInputAndGetColorString(String argument) {
        if (argument.charAt(0) == '#') {
            return ChatColor.of(argument) + "";
        }

        return org.bukkit.ChatColor.valueOf(argument) + "";
    }
}
