package net.minebo.brawl.hook;

import net.minebo.brawl.hook.impl.FancyHologramHook;
import org.bukkit.Bukkit;

import java.util.List;

public abstract class PluginHook {

    public abstract String getPluginName();
    public abstract void initHook();

    public static List<PluginHook> hooks;

    public static void init() {
        hooks = List.of(
                new FancyHologramHook()
        );

        hooks.forEach(ph -> {
            if(Bukkit.getPluginManager().isPluginEnabled(ph.getPluginName())) {
                ph.initHook();
            }
        });
    }
}
