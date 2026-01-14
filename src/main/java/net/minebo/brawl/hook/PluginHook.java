package net.minebo.brawl.hook;

import net.minebo.brawl.Brawl;
import net.minebo.brawl.hook.impl.FancyHologramHook;
import net.minebo.brawl.hook.impl.KothHook;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class PluginHook implements Listener {

    public abstract String getPluginName();
    public abstract void initHook();

    public static List<PluginHook> hooks;

    public static void init() {
        hooks = List.of(
                new FancyHologramHook(),
                new KothHook()
        );

        hooks.forEach(ph -> {
            if(Bukkit.getPluginManager().isPluginEnabled(ph.getPluginName())) {
                ph.initHook();
                Bukkit.getPluginManager().registerEvents(ph, Brawl.getInstance());
            }
        });
    }
}
