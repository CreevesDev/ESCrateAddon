package me.cooldcb.escrateaddon;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ESCrateAddon extends JavaPlugin {

    Listener[] listeners = new Listener[] {new CrateEvents(this)};

    @Override
    public void onEnable() {
        registerEvents(listeners);
    }


    public void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
