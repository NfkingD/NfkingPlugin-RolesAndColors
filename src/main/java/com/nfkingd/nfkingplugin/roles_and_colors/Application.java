package com.nfkingd.nfkingplugin.roles_and_colors;

import com.nfkingd.nfkingplugin.roles_and_colors.commands.RoleCommand;
import com.nfkingd.nfkingplugin.roles_and_colors.listeners.RoleEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Application extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new RoleEventListener(), this);
        Objects.requireNonNull(this.getCommand("role")).setExecutor(new RoleCommand());
    }
}
