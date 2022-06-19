package com.nfkingd.nfkingplugin.roles_and_colors.listeners;


import com.nfkingd.nfkingplugin.roles_and_colors.utils.RolesJsonUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class RoleEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var optionalRole = RolesJsonUtil.getPlayerFromRoles(player.getName());

        if (optionalRole.isPresent()) {
            var role = optionalRole.get();
            player.setPlayerListName(role.getFormattedName());
            player.setDisplayName(role.getFormattedName());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        var player = event.getPlayer();
        var optionalRole = RolesJsonUtil.getPlayerFromRoles(player.getName());

        if (optionalRole.isPresent()) {
            var role = optionalRole.get();
            var displayName = player.getDisplayName();
            var message = event.getMessage();

            event.setFormat("<" + displayName + ChatColor.WHITE + "> " + role.getColor() + message);
        }
    }
}
