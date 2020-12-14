package me.cooldcb.escrateaddon;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ESCrateAddon extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Listener[] listeners = new Listener[] {
            this,
            new CrateEvents(this)
        };

        registerEvents(listeners);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
