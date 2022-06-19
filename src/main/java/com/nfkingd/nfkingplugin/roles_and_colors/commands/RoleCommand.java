package com.nfkingd.nfkingplugin.roles_and_colors.commands;

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
    public boolean onCommand(CommandSender commandSender, Command command, String alias,  String[] arguments) {
            if (!commandSender.hasPermission("roles.admin")) {
                sendErrorMessage(commandSender, "You don't have the permission");
                return true;
            }

            if (arguments.length == 0) {
                sendErrorMessage(commandSender, "This command requires arguments");
                return true;
            }

            var firstArgument = arguments[0];

            if (firstArgument.equals("set")) {
                return processSetCommand(commandSender, arguments);
            }

            var message = "/role " + arguments[0] + " does not exist";
            sendErrorMessage(commandSender, message);

            return true;
        }

    private boolean processSetCommand(CommandSender commandSender, String[] arguments) {
        var argumentsCount = arguments.length;

        if (argumentsCount < 3) {
            return sendErrorMessageForMissingArguments(argumentsCount, commandSender);
        } else if (argumentsCount > 5) {
            return sendErrorMessageForExcessArguments(argumentsCount, commandSender);
        }

        var optionalPlayer = getPlayer(commandSender, arguments);

        if (optionalPlayer.isEmpty()) {
            sendErrorMessage(commandSender, "Player was not found");
            return true;
        }

        var player = optionalPlayer.get();
        var color = getColorFromArguments(arguments, argumentsCount);
        var formattedName = color + arguments[2] + " - " + player.getName();

        updatePlayerName(player, formattedName, color);

        return true;
    }

    private boolean sendErrorMessageForMissingArguments(int argumentsCount, CommandSender commandSender) {
        var missingArgumentCount = 3 - argumentsCount;
        var message = "You must add at least " + missingArgumentCount + " more argument(s)";
        sendErrorMessage(commandSender, message);

        return true;
    }

    private boolean sendErrorMessageForExcessArguments(int argumentsCount, CommandSender commandSender) {
        var excessArgumentCount = argumentsCount - 5;
        var message = "You must remove at least " + excessArgumentCount + " argument(s)";
        sendErrorMessage(commandSender, message);

        return true;
    }

    private void sendErrorMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(ChatColor.DARK_RED + message);
    }

    private Optional<? extends Player> getPlayer(CommandSender commandSender, String[] arguments) {
        return commandSender.getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> p.getName().equals(arguments[1]))
                .findFirst();
    }

    private String getColorFromArguments(String[] arguments, int argumentsCount) {
        if (argumentsCount == 5) {
            return ChatColor.of(arguments[3]) + "" + org.bukkit.ChatColor.valueOf(arguments[4]);
        } else if (argumentsCount == 4) {
            return handleInputAndGetColorString(arguments);
        }

        return ChatColor.WHITE + "";
    }

    private String handleInputAndGetColorString(String[] arguments) {
        if (arguments[3].charAt(0) == '#') {
            return ChatColor.of(arguments[3]) + "";
        }

        return org.bukkit.ChatColor.valueOf(arguments[3]) + "";
    }

    private void updatePlayerName(Player player, String formattedName, String color) {
        player.setPlayerListName(formattedName);
        player.setDisplayName(formattedName);

        var role = new RoleDto(player.getName(), player.getPlayerListName(), color);
        RolesJsonUtil.saveRoleToJson(role);
    }
}
